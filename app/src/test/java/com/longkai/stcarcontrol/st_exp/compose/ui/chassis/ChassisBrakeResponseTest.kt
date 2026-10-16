package com.longkai.stcarcontrol.st_exp.compose.ui.chassis

import android.app.Application
import android.os.Looper
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.*
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], application = Application::class)
@LooperMode(LooperMode.Mode.PAUSED)
class ChassisBrakeResponseTest {
    private val repository = RecordingRepository()
    private var now = 10_000L
    private lateinit var model: ChassisViewModel

    @Before
    fun setUp() {
        model = ChassisViewModel(
            repository, clockMillis = { now }, receptionDispatcher = Dispatchers.Main.immediate
        )
        model.onPageEntered()
        idle()
        model.onControlTabSelected(ChassisControlTab.BrakePedal)
    }

    @After
    fun tearDown() {
        model.onPageExited()
        idle()
    }

    @Test
    fun recordsOnlyTheTwoSecondWindowAndFreezesActualReports() {
        report(9_999, ehb = 99f, emb = 199f)
        submit()
        assertEquals(listOf(8000), repository.combinedSubmissions)
        assertEquals(1, repository.reports.subscriptionCount.value)
        assertNull(model.uiState.value.brakeResponse)
        report(9_999)
        report(10_000, ehb = 100f, emb = 200f)
        report(10_000, ehb = 110f, emb = 210f)
        report(10_037, ehb = 300f, emb = 400f)
        advanceBy(1_999)
        assertNull(model.uiState.value.brakeResponse)
        report(11_999, ehb = 900f, emb = 1000f)
        report(12_000)
        report(12_001)
        advanceBy(1)

        val response = model.uiState.value.brakeResponse!!
        assertEquals(10_000L, response.startedAtMillis)
        assertEquals(listOf(10_000L, 10_000L, 10_037L, 11_999L), response.samples.map { it.timestampMillis })
        assertEquals(listOf(100f, 110f, 300f, 900f), response.samples.map { it.ehbForceN })
        assertEquals(listOf(200f, 210f, 400f, 1000f), response.samples.map { it.embForceN })
        report(12_100, ehb = 1500f, emb = 1600f)
        advanceBy(ChassisViewModel.DISPLAY_INTERVAL_MILLIS)
        assertSame(response, model.uiState.value.brakeResponse)
        assertEquals(1500f, model.telemetryState.value.telemetry!!.ehbForceN, 0f)
        model.onBrakeResponseClosed()
        assertNull(model.uiState.value.brakeResponse)
        assertEquals(listOf(8000), repository.combinedSubmissions)
    }

    @Test
    fun reportFrequencyDoesNotLimitTheNumberOfRecordedSamples() {
        submit()
        repeat(400) { report(now + it * 5L) }
        advanceBy(2_000)
        assertEquals(400, model.uiState.value.brakeResponse!!.samples.size)
        assertEquals(11_995L, model.uiState.value.brakeResponse!!.samples.last().timestampMillis)
    }

    @Test
    fun aNewSubmissionReplacesTheRecordingAndItsDeadline() {
        submit()
        report(now, ehb = 100f)
        advanceBy(1_000)
        submit("9000")
        report(now, ehb = 900f)
        advanceBy(1_000)
        assertNull(model.uiState.value.brakeResponse)
        advanceBy(999)
        assertNull(model.uiState.value.brakeResponse)
        advanceBy(1)
        assertEquals(listOf(8000, 9000), repository.combinedSubmissions)
        assertEquals(11_000L, model.uiState.value.brakeResponse!!.startedAtMillis)
        assertEquals(listOf(900f), model.uiState.value.brakeResponse!!.samples.map { it.ehbForceN })
        submit("10000")
        assertNull(model.uiState.value.brakeResponse)
        advanceBy(2_000)
        assertTrue(model.uiState.value.brakeResponse!!.samples.isEmpty())
    }

    @Test
    fun reportsDuringSuspendingSubmissionAreKeptWithoutExtendingTheWindow() {
        val pending = CompletableDeferred<ChassisCommandResult>()
        repository.pendingSubmission = pending
        submit()
        advanceBy(500)
        report(now)
        pending.complete(ChassisCommandResult.Submitted)
        idle()
        advanceBy(1_499)
        assertNull(model.uiState.value.brakeResponse)
        advanceBy(1)
        assertEquals(listOf(10_500L), model.uiState.value.brakeResponse!!.samples.map { it.timestampMillis })
    }

    @Test
    fun rejectedSubmissionShowsTheExistingErrorWithoutAResponse() {
        repository.result = ChassisCommandResult.Rejected(ChassisError.NotReady)
        submit()
        report(now)
        advanceBy(2_000)
        assertNull(model.uiState.value.brakeResponse)
        assertEquals(ChassisError.NotReady, model.uiState.value.error)
        assertTrue(model.uiState.value.enabledControls.isEmpty())
    }

    @Test
    fun previewOnlyDoesNotOpenAResponse() {
        repository.result = ChassisCommandResult.PreviewOnly
        submit()
        report(now)
        advanceBy(2_000)
        assertNull(model.uiState.value.brakeResponse)
        assertNull(model.uiState.value.error)
    }

    @Test
    fun editsInvalidOrStaleSubmissionsAndSingleFieldCommandsDoNotRecord() {
        val generation = model.uiState.value.generation(ChassisControlField.Ehb)
        model.onCombinedBrakesChanged(8000, generation)
        advanceBy(2_000)
        assertTrue(repository.combinedSubmissions.isEmpty())
        assertNull(model.uiState.value.brakeResponse)
        for (invalid in listOf("", "1.5", "-1", "20001")) {
            submit(invalid)
            assertEquals(ChassisError.InvalidControl, model.uiState.value.error)
            model.dismissError()
        }
        model.onCombinedBrakesInputSubmitted("8000", generation + 1)
        model.onControlInputSubmitted(ChassisControlField.Ehb, "6000", generation)
        model.onControlInputSubmitted(ChassisControlField.Emb, "7000", generation)
        idle()
        model.lockControls()
        submit()
        report(now)
        advanceBy(2_000)
        assertTrue(repository.combinedSubmissions.isEmpty())
        assertEquals(2, repository.singleSubmissions.size)
        assertNull(model.uiState.value.brakeResponse)
    }

    @Test
    fun lockingDoesNotCancelASubmittedRecordingOrRequestComparison() {
        submit()
        model.lockControls()
        report(now)
        model.onCompareOpened()
        idle()
        advanceBy(2_000)
        assertNotNull(model.uiState.value.brakeResponse)
        assertTrue(model.uiState.value.comparisonOpen)
        assertEquals(1, repository.comparisonRequests)
        model.onBrakeResponseClosed()
        assertTrue(model.uiState.value.comparisonOpen)
        assertEquals(listOf(8000), repository.combinedSubmissions)
    }

    @Test
    fun pageExitCancelsCaptureAndClearsCompletedResultsWithoutSending() {
        submit()
        report(now)
        advanceBy(1_000)
        model.onPageExited()
        idle()
        assertEquals(0, repository.reports.subscriptionCount.value)
        advanceBy(2_000)
        assertNull(model.uiState.value.brakeResponse)
        model.onPageEntered()
        model.onControlTabSelected(ChassisControlTab.BrakePedal)
        submit()
        advanceBy(2_000)
        assertNotNull(model.uiState.value.brakeResponse)
        model.onPageExited()
        idle()
        assertNull(model.uiState.value.brakeResponse)
        assertEquals(listOf(8000, 8000), repository.combinedSubmissions)
        assertEquals(0, repository.comparisonRequests)
    }

    @Test
    fun pageExitDuringSubmissionCannotOpenAStaleDialogAfterReentry() {
        val pending = CompletableDeferred<ChassisCommandResult>()
        repository.pendingSubmission = pending
        submit()
        model.onPageExited()
        idle()
        pending.complete(ChassisCommandResult.Submitted)
        model.onPageEntered()
        idle()
        advanceBy(2_000)
        assertNull(model.uiState.value.brakeResponse)
        assertEquals(listOf(8000), repository.combinedSubmissions)
    }

    private fun submit(text: String = "8000") {
        model.onCombinedBrakesInputSubmitted(text, model.uiState.value.generation(ChassisControlField.Ehb))
        idle()
    }

    private fun report(timestamp: Long, ehb: Float = 6000f, emb: Float = 7000f) {
        assertTrue(repository.reports.tryEmit(ChassisTelemetry(timestamp, 0f, 0f, ehb, emb, 0f, 48f)))
        idle()
    }

    private fun advanceBy(millis: Long) {
        now += millis
        shadowOf(Looper.getMainLooper()).idleFor(millis, TimeUnit.MILLISECONDS)
    }

    private fun idle() = shadowOf(Looper.getMainLooper()).idle()

    private class RecordingRepository : ChassisRepository {
        override val config = ChassisControlConfig()
        override val isDemo = false
        val reports = MutableSharedFlow<ChassisTelemetry>(extraBufferCapacity = 512)
        override val telemetry = reports
        val combinedSubmissions = mutableListOf<Int>()
        val singleSubmissions = mutableListOf<ChassisControl>()
        var comparisonRequests = 0
        var result: ChassisCommandResult = ChassisCommandResult.Submitted
        var pendingSubmission: CompletableDeferred<ChassisCommandResult>? = null

        override suspend fun submitControl(control: ChassisControl): ChassisCommandResult {
            singleSubmissions += control
            return result
        }

        override suspend fun submitCombinedBrakes(forceN: Int): ChassisCommandResult {
            combinedSubmissions += forceN
            return pendingSubmission?.await() ?: result
        }

        override suspend fun requestComparison(): ChassisComparisonResult {
            comparisonRequests++
            return ChassisComparisonResult.Success(ChassisComparison(List(50) { 0 }, List(50) { 0 }))
        }

        override suspend fun setEpb(enabled: Boolean) = result
        override suspend fun setCurrentOffset(enabled: Boolean) = result
        override suspend fun setEmergencyStop(enabled: Boolean) = result
    }
}

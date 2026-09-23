package com.longkai.stcarcontrol.st_exp.compose.ui.chassis

import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelProvider
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.ChassisCommandResult
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.ChassisControlConfig
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.ChassisControlState
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.ChassisControlTab
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.ChassisError
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.ChassisRepository
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.ChassisTelemetry
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.ChassisTelemetryException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChassisViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val stores = mutableListOf<ViewModelStore>()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        stores.forEach { it.clear() }
        dispatcher.scheduler.runCurrent()
        Dispatchers.resetMain()
    }

    @Test
    fun `drags update draft immediately and only release submits latest target`() = runTest {
        val repository = RecordingRepository()
        val model = createModel(repository)
        val collection = observe(model)
        runCurrent()
        val measured = sample(1_000L, speed = 1.25f)
        repository.samples.emit(measured)
        runCurrent()

        model.onSpeedStepChanged(-5)
        model.onSteeringStepChanged(5)
        model.onEhbLevelChanged(0.25f)
        model.onEmbLevelChanged(0.75f)
        assertEquals(-5, model.uiState.value.controls.speedStep)
        assertEquals(5, model.uiState.value.controls.steeringStep)
        assertEquals(0.25f, model.uiState.value.controls.ehbLevel, 0f)
        assertEquals(0.75f, model.uiState.value.controls.embLevel, 0f)
        runCurrent()
        assertTrue(repository.targets.isEmpty())
        assertEquals(measured, model.uiState.value.telemetry)

        model.commitControls()
        runCurrent()
        assertEquals(listOf(model.uiState.value.controls), repository.targets)
        assertEquals(measured, model.uiState.value.telemetry)

        repository.samples.emit(sample(1_100L, speed = 2f))
        runCurrent()
        assertEquals(-5, model.uiState.value.controls.speedStep)
        assertEquals(2f, model.uiState.value.telemetry!!.speedKph, 0f)
        collection.cancelAndJoin()
    }

    @Test
    fun `positive negative and neutral drafts are supported`() = runTest {
        val repository = RecordingRepository()
        val model = createModel(repository)
        for (step in listOf(-5, 5, 0)) {
            model.onSpeedStepChanged(step)
            model.onSteeringStepChanged(step)
            model.commitControls()
            runCurrent()
        }
        assertEquals(listOf(-5, 5, 0), repository.targets.map { it.speedStep })
        assertEquals(listOf(-5, 5, 0), repository.targets.map { it.steeringStep })
    }

    @Test
    fun `invalid and nonfinite drafts preserve last valid normalized levels`() = runTest {
        val repository = RecordingRepository()
        val model = createModel(repository)
        model.onEhbLevelChanged(0.4f)
        model.onEmbLevelChanged(0.6f)

        for (invalid in listOf(-0.1f, 1.1f, Float.NaN, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY)) {
            model.onEhbLevelChanged(invalid)
            model.onEmbLevelChanged(invalid)
            assertEquals(0.4f, model.uiState.value.controls.ehbLevel, 0f)
            assertEquals(0.6f, model.uiState.value.controls.embLevel, 0f)
            assertEquals(ChassisError.InvalidControl, model.uiState.value.error)
            model.dismissError()
            assertNull(model.uiState.value.error)
        }
        model.onSpeedStepChanged(Int.MIN_VALUE)
        model.onSteeringStepChanged(Int.MAX_VALUE)
        assertEquals(0, model.uiState.value.controls.speedStep)
        assertEquals(0, model.uiState.value.controls.steeringStep)
        runCurrent()
        assertTrue(repository.targets.isEmpty())
    }

    @Test
    fun `normalized level endpoints and configurable demo step limits are accepted`() = runTest {
        val repository = RecordingRepository(config = ChassisControlConfig(-2, 3, -1, 2))
        val model = createModel(repository)
        model.onSpeedStepChanged(-2)
        model.onSteeringStepChanged(2)
        model.onEhbLevelChanged(1f)
        model.onEmbLevelChanged(1f)
        model.commitControls()
        runCurrent()
        model.onSpeedStepChanged(-3)
        model.onSteeringStepChanged(3)
        assertEquals(-2, model.uiState.value.controls.speedStep)
        assertEquals(2, model.uiState.value.controls.steeringStep)
        model.onEhbLevelChanged(0f)
        model.onEmbLevelChanged(0f)
        model.commitControls()
        runCurrent()

        assertEquals(repository.config, model.uiState.value.config)
        assertEquals(listOf(1f, 0f), repository.targets.map { it.ehbLevel })
        assertEquals(listOf(1f, 0f), repository.targets.map { it.embLevel })
    }

    @Test
    fun `offset is explicit intent without committing an in-progress drag`() = runTest {
        val repository = RecordingRepository()
        val model = createModel(repository)
        model.onSpeedStepChanged(2)
        model.commitControls()
        runCurrent()
        model.onSpeedStepChanged(5)
        model.onEhbLevelChanged(0.8f)
        model.onCurrentOffsetChanged(true)
        runCurrent()

        assertTrue(model.uiState.value.controls.currentOffsetEnabled)
        assertEquals(5, model.uiState.value.controls.speedStep)
        assertEquals(
            ChassisControlState(speedStep = 2, currentOffsetEnabled = true),
            repository.targets.last(),
        )
        model.onCurrentOffsetChanged(false)
        runCurrent()
        assertEquals(ChassisControlState(speedStep = 2), repository.targets.last())
        model.commitControls()
        runCurrent()
        assertEquals(model.uiState.value.controls, repository.targets.last())
    }

    @Test
    fun `read collect restart and recreation do not submit commands`() = runTest {
        val repository = RecordingRepository()
        val model = createModel(repository)
        assertEquals(ChassisControlState(), model.uiState.value.controls)
        runCurrent()
        assertEquals(0, repository.startedCollections)

        val first = observe(model)
        runCurrent()
        assertEquals(1, repository.activeCollections)
        val second = observe(model)
        runCurrent()
        assertEquals(1, repository.activeCollections)
        first.cancelAndJoin()
        runCurrent()
        assertEquals(1, repository.activeCollections)
        second.cancelAndJoin()
        runCurrent()
        assertEquals(0, repository.activeCollections)

        val restarted = observe(model)
        runCurrent()
        assertEquals(2, repository.startedCollections)
        restarted.cancelAndJoin()
        runCurrent()
        val recreated = observe(createModel(repository))
        runCurrent()
        assertEquals(3, repository.startedCollections)
        assertTrue(repository.targets.isEmpty())
        recreated.cancelAndJoin()
        runCurrent()
        assertEquals(0, repository.activeCollections)
    }

    @Test
    fun `previous explicit command is not replayed when observation restarts`() = runTest {
        val repository = RecordingRepository()
        val model = createModel(repository)
        model.onSpeedStepChanged(3)
        model.commitControls()
        runCurrent()
        val first = observe(model)
        runCurrent()
        first.cancelAndJoin()
        runCurrent()
        val restarted = observe(model)
        runCurrent()

        assertEquals(1, repository.targets.size)
        assertEquals(3, model.uiState.value.controls.speedStep)
        restarted.cancelAndJoin()
    }

    @Test
    fun `control tab changes are visual only including EPB`() = runTest {
        val repository = RecordingRepository()
        val model = createModel(repository)

        ChassisControlTab.values().forEach {
            model.onControlTabSelected(it)
            assertEquals(it, model.uiState.value.selectedControl)
        }
        runCurrent()
        assertEquals(ChassisControlState(), model.uiState.value.controls)
        assertTrue(repository.targets.isEmpty())
    }

    @Test
    fun `history is timestamp limited point capped and snapshot immutable`() = runTest {
        val repository = RecordingRepository()
        val model = createModel(repository)
        val collection = observe(model)
        runCurrent()
        repository.samples.emit(sample(0L))
        runCurrent()
        val initialHistory = model.uiState.value.history

        for (index in 1..400) {
            repository.samples.emit(sample(index * 100L))
        }
        runCurrent()
        val history = model.uiState.value.history
        assertEquals(ChassisViewModel.HISTORY_MAX_POINTS, history.size)
        assertEquals(10_100L, history.first().timestampMillis)
        assertEquals(40_000L, history.last().timestampMillis)
        assertTrue(history.all { 40_000L - it.timestampMillis <= 30_000L })
        assertEquals(listOf(sample(0L)), initialHistory)
        assertNotEquals(initialHistory, history)

        repository.samples.emit(sample(100_000L))
        runCurrent()
        assertEquals(listOf(sample(100_000L)), model.uiState.value.history)
        collection.cancelAndJoin()
    }

    @Test
    fun `history ignores older timestamps and replaces duplicate timestamp`() = runTest {
        val repository = RecordingRepository()
        val model = createModel(repository)
        val collection = observe(model)
        runCurrent()
        repository.samples.emit(sample(5_000L))
        repository.samples.emit(sample(4_000L))
        repository.samples.emit(sample(5_000L, speed = 2f))
        runCurrent()

        assertEquals(listOf(sample(5_000L, speed = 2f)), model.uiState.value.history)
        assertEquals(sample(5_000L, speed = 2f), model.uiState.value.telemetry)
        collection.cancelAndJoin()
    }

    @Test
    fun `pending targets are bounded and coalesce to latest explicit intent`() = runTest {
        val repository = RecordingRepository().apply { submissionDelayMillis = 1_000L }
        val model = createModel(repository)
        model.onSpeedStepChanged(1)
        model.commitControls()
        runCurrent()
        repeat(100) { index ->
            model.onSpeedStepChanged(index % 6)
            model.commitControls()
        }
        model.onSpeedStepChanged(-5)
        model.commitControls()
        model.onCurrentOffsetChanged(true)
        runCurrent()
        assertEquals(1, repository.targets.size)
        advanceTimeBy(1_000L)
        runCurrent()

        assertEquals(2, repository.targets.size)
        assertEquals(
            ChassisControlState(speedStep = -5, currentOffsetEnabled = true),
            repository.targets.last(),
        )
        advanceTimeBy(5_000L)
        runCurrent()
        assertEquals(2, repository.targets.size)
    }

    @Test
    fun `command failure is surfaced and dismissible without invented feedback`() = runTest {
        val repository = RecordingRepository()
        val model = createModel(repository)
        repository.result = ChassisCommandResult.Rejected(ChassisError.CommandFailed)
        model.onSpeedStepChanged(2)
        model.commitControls()
        runCurrent()

        assertEquals(ChassisError.CommandFailed, model.uiState.value.error)
        assertEquals(2, model.uiState.value.controls.speedStep)
        assertNull(model.uiState.value.telemetry)
        model.dismissError()
        assertNull(model.uiState.value.error)
        repository.result = ChassisCommandResult.PreviewOnly
        model.commitControls()
        runCurrent()
        assertEquals(2, repository.targets.size)
        assertTrue(model.uiState.value.isDemo)
    }

    @Test
    fun `expected telemetry failure is surfaced and collection can restart`() = runTest {
        val repository = RecordingRepository().apply { telemetryError = ChassisError.NotReady }
        val model = createModel(repository)
        val collection = observe(model)
        runCurrent()
        assertEquals(ChassisError.NotReady, model.uiState.value.error)
        assertEquals(0, repository.activeCollections)
        collection.cancelAndJoin()
        runCurrent()
        repository.telemetryError = null
        model.dismissError()
        val restarted = observe(model)
        runCurrent()
        repository.samples.emit(sample(2_000L))
        runCurrent()

        assertNull(model.uiState.value.error)
        assertEquals(sample(2_000L), model.uiState.value.telemetry)
        assertTrue(repository.targets.isEmpty())
        restarted.cancelAndJoin()
    }

    @Test
    fun `clearing model cancels in-flight work and discards pending intent`() = runTest {
        val repository = RecordingRepository().apply { submissionDelayMillis = 60_000L }
        val model = createModel(repository)
        val collection = observe(model)
        runCurrent()
        model.commitControls()
        runCurrent()
        model.onSpeedStepChanged(4)
        model.commitControls()
        stores.single().clear()
        runCurrent()

        assertEquals(0, repository.activeCollections)
        assertEquals(0, repository.activeSubmissions)
        advanceTimeBy(120_000L)
        runCurrent()
        assertEquals(1, repository.targets.size)
        assertNull(model.uiState.value.error)
        collection.cancelAndJoin()
    }

    private fun createModel(repository: ChassisRepository): ChassisViewModel {
        val store = ViewModelStore().also { stores += it }
        return ViewModelProvider(store, ChassisViewModel.provideFactory(repository))
            .get(ChassisViewModel::class.java)
    }

    private fun TestScope.observe(model: ChassisViewModel): Job =
        launch(UnconfinedTestDispatcher(testScheduler)) { model.uiState.collect {} }

    private fun sample(timestamp: Long, speed: Float = 0f) =
        ChassisTelemetry(timestamp, speed, 0f, 0f, 0f)

    private class RecordingRepository(
        override val config: ChassisControlConfig = ChassisControlConfig(),
    ) : ChassisRepository {
        override val isDemo: Boolean = true
        val samples = MutableSharedFlow<ChassisTelemetry>()
        val targets = mutableListOf<ChassisControlState>()
        var startedCollections = 0
        var activeCollections = 0
        var activeSubmissions = 0
        var submissionDelayMillis = 0L
        var result: ChassisCommandResult = ChassisCommandResult.PreviewOnly
        var telemetryError: ChassisError? = null

        override val telemetry: Flow<ChassisTelemetry> = flow {
            startedCollections++
            activeCollections++
            try {
                telemetryError?.let { throw ChassisTelemetryException(it) }
                emitAll(samples)
            } finally {
                activeCollections--
            }
        }

        override suspend fun submitControls(controls: ChassisControlState): ChassisCommandResult {
            targets += controls
            activeSubmissions++
            try {
                delay(submissionDelayMillis)
                return result
            } finally {
                activeSubmissions--
            }
        }
    }
}

package com.longkai.stcarcontrol.st_exp.compose.ui.chassis

import android.graphics.Bitmap
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.test.platform.app.InstrumentationRegistry
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDChassisList.CMDChassisReport
import com.longkai.stcarcontrol.st_exp.communication.utils.byteArrayToInt
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.*
import com.longkai.stcarcontrol.st_exp.compose.ui.theme.STCarTheme
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File

class ChassisScreenTest {
    @get:Rule val compose = createComposeRule()
    private val store = ViewModelStore()
    private lateinit var service: ChassisServiceFixture
    private lateinit var model: ChassisViewModel

    @Before fun setUp() { service = ChassisServiceFixture() }

    @After fun close() {
        try {
            compose.runOnIdle { store.clear() }
            compose.waitForIdle()
        } finally {
            if (::service.isInitialized) service.close()
        }
    }

    @Test
    fun pageReceivesImmediatelyWithSlidersLockedAndIndependentSwitchesOff() {
        showScreen()
        compose.onNodeWithText("EHB Braking Force").assertIsDisplayed()
        compose.onNodeWithText("EMB Braking Force").assertIsDisplayed()
        compose.onNodeWithTag("chassis-angle-reading").assertTextEquals("+12.34")
        compose.onNodeWithTag("chassis-ehb-reading").assertTextEquals("6800")
        for (name in listOf("speed", "angle", "ehb", "emb")) {
            slider(name).performScrollTo().assertIsNotEnabled()
        }
        offsetSwitch().assertIsEnabled().assertIsOff()
        mode("Epb").assertIsEnabled().assertIsNotSelected()
        compose.runOnIdle {
            assertEquals(1, service.registeredCommandCount)
            assertTrue(service.writes.isEmpty())
        }
    }

    @Test
    fun controlsCanBeEnabledAndUsedBeforeAnyReportArrives() {
        showScreen(withInitialReport = false)
        mode("Vehicle").assertIsEnabled().performClick()
        slider("speed").assertIsEnabled()
            .performSemanticsAction(SemanticsActions.SetProgress) { it(5f) }
        compose.runOnIdle {
            assertNull(model.uiState.value.telemetry)
            assertNull(model.uiState.value.error)
            assertEquals(1, service.writes.size)
            assertEquals(5, byteArrayToInt(service.writes.single(), 8))
        }
        mode("Steering").assertIsEnabled().performClick()
        slider("angle").assertIsEnabled()
        slider("speed").assertIsNotEnabled()
        mode("BrakePedal").assertIsEnabled().performClick()
        slider("ehb").assertIsEnabled()
        slider("emb").assertIsEnabled()
        slider("angle").assertIsNotEnabled()
    }

    @Test
    fun independentSwitchesSendOnAndOffWithoutSelectingAControlGroup() {
        showScreen(withInitialReport = false)
        mode("Epb").performClick().assertIsSelected()
        offsetSwitch().performScrollTo().performClick().assertIsOn()
        mode("Epb").assertIsSelected()
        compose.runOnIdle {
            assertNull(model.uiState.value.selectedControl)
            assertNull(model.uiState.value.telemetry)
            assertEquals(listOf(0x20, 0x10), service.writes.map { byteArrayToInt(it, 4) })
            assertEquals(0x55, service.writes[0][25].toInt())
            assertEquals(0, service.writes[0][24].toInt())
            assertEquals(0x55, service.writes[1][24].toInt())
            assertEquals(0, service.writes[1][25].toInt())
            model.onCurrentOffsetChanged(true)
        }
        mode("Epb").performClick().assertIsNotSelected()
        offsetSwitch().assertIsOn().performClick().assertIsOff()
        compose.runOnIdle {
            assertEquals(listOf(0x20, 0x10, 0x20, 0x10), service.writes.map { byteArrayToInt(it, 4) })
            assertEquals(0, service.writes[2][25].toInt())
            assertEquals(0, service.writes[3][24].toInt())
            assertEquals(1, service.registeredCommandCount)
        }
        for (name in listOf("speed", "angle", "ehb", "emb")) slider(name).assertIsNotEnabled()
    }

    @Test
    fun switchesCoexistWithAllGroupsAndDoNotDiscardSliderEdits() {
        showScreen()
        mode("Epb").performClick()
        offsetSwitch().performScrollTo().performClick()
        for (name in listOf("Vehicle", "Steering", "BrakePedal")) {
            mode(name).performClick().assertIsSelected()
            mode("Epb").assertIsSelected()
            offsetSwitch().assertIsOn()
        }
        mode("BrakePedal").performClick().assertIsNotSelected()
        mode("Epb").assertIsSelected()
        offsetSwitch().assertIsOn()
        compose.runOnIdle {
            assertEquals(2, service.writes.size)
            service.receiveReport(CMDChassisReport.Response(5, 100, 200, 300))
        }
        compose.waitForIdle()
        compose.onNodeWithTag("chassis-angle-reading").assertTextEquals("+1.00")
        mode("Vehicle").performClick()
        var generation = 0L
        compose.runOnIdle {
            generation = model.uiState.value.controlGeneration
            model.onControlChanged(ChassisControlField.Speed, 10, generation)
        }
        mode("Epb").performClick().assertIsNotSelected()
        offsetSwitch().performClick().assertIsOff()
        mode("Vehicle").assertIsSelected()
        slider("speed").assertIsEnabled()
        compose.runOnIdle {
            assertEquals(generation, model.uiState.value.controlGeneration)
            model.onControlCommitted(ChassisControlField.Speed, generation)
        }
        compose.waitForIdle()
        compose.runOnIdle {
            assertEquals(listOf(0x20, 0x10, 0x20, 0x10, 0x01), service.writes.map { byteArrayToInt(it, 4) })
            assertEquals(10, byteArrayToInt(service.writes.last(), 8))
            assertEquals(1, service.registeredCommandCount)
        }
    }

    @Test
    fun pageExitDoesNotResetSwitchTargetsOrReplayTheirCommands() {
        showScreen()
        mode("Epb").performClick()
        offsetSwitch().performScrollTo().performClick()
        compose.runOnIdle {
            model.onPageExited()
            model.onEpbToggled()
            model.onCurrentOffsetChanged(false)
        }
        compose.waitForIdle()
        compose.runOnIdle {
            assertEquals(0, service.registeredCommandCount)
            assertTrue(model.uiState.value.epbEnabled)
            assertTrue(model.uiState.value.currentOffsetEnabled)
            assertEquals(2, service.writes.size)
            model.onPageEntered()
        }
        compose.waitForIdle()
        mode("Epb").assertIsSelected()
        offsetSwitch().assertIsOn()
        compose.runOnIdle {
            assertEquals(1, service.registeredCommandCount)
            assertEquals(2, service.writes.size)
        }
    }

    @Test
    fun rejectedSwitchChangesPreservePreviousTargetsAndTheSelectedGroup() {
        val liveRepository = DefaultChassisRepository(service.manager)
        var reject = true
        val repository = object : ChassisRepository by liveRepository {
            override suspend fun setEpb(enabled: Boolean): ChassisCommandResult =
                if (reject) ChassisCommandResult.Rejected(ChassisError.NotReady) else liveRepository.setEpb(enabled)

            override suspend fun setCurrentOffset(enabled: Boolean): ChassisCommandResult =
                if (reject) ChassisCommandResult.Rejected(ChassisError.NotReady) else liveRepository.setCurrentOffset(enabled)
        }
        showScreen(repository = repository)
        mode("Vehicle").performClick()
        mode("Epb").performClick()
        compose.onNodeWithText("Dismiss").performClick()
        mode("Epb").assertIsNotSelected()
        offsetSwitch().performScrollTo().performClick()
        compose.onNodeWithText("Dismiss").performClick()
        offsetSwitch().assertIsOff()
        compose.runOnIdle {
            assertTrue(service.writes.isEmpty())
            reject = false
        }
        mode("Epb").performClick().assertIsSelected()
        offsetSwitch().performClick().assertIsOn()
        compose.runOnIdle { reject = true }
        mode("Epb").performClick()
        compose.onNodeWithText("Dismiss").performClick()
        mode("Epb").assertIsSelected()
        offsetSwitch().performClick()
        compose.onNodeWithText("Dismiss").performClick()
        offsetSwitch().assertIsOn()
        mode("Vehicle").assertIsSelected()
        slider("speed").assertIsEnabled()
        compose.runOnIdle { assertEquals(2, service.writes.size) }
    }

    @Test
    fun modesAreMutuallyExclusiveAndClickingAgainLocksWithoutSending() {
        showScreen()
        mode("Vehicle").performClick().assertIsSelected()
        slider("speed").assertIsEnabled()
        slider("angle").assertIsNotEnabled()
        mode("Steering").performClick().assertIsSelected()
        mode("Vehicle").assertIsNotSelected()
        slider("speed").assertIsNotEnabled()
        slider("angle").assertIsEnabled()
        mode("BrakePedal").performClick().assertIsSelected()
        slider("ehb").assertIsEnabled()
        slider("emb").assertIsEnabled()
        slider("angle").assertIsNotEnabled()
        mode("BrakePedal").performClick().assertIsNotSelected()
        slider("ehb").assertIsNotEnabled()
        slider("emb").assertIsNotEnabled()
        compose.runOnIdle {
            assertTrue(service.writes.isEmpty())
            service.receiveReport(CMDChassisReport.Response(7, -12345, 1234, 5678))
        }
        compose.waitForIdle()
        compose.onNodeWithTag("chassis-angle-reading").assertTextEquals("-123.45")
        compose.onNodeWithTag("chassis-emb-reading").assertTextEquals("5678")
        compose.runOnIdle { assertEquals(1, service.registeredCommandCount) }
    }

    @Test
    fun vehicleDragWritesOnceOnReleaseAndLockingDoesNotSendZero() {
        showScreen()
        mode("Vehicle").performClick()
        slider("speed").performTouchInput { swipe(centerLeft, Offset(width * .75f, center.y), 200) }
        compose.runOnIdle {
            assertEquals(1, service.writes.size)
            val frame = service.writes.single()
            assertEquals(1, byteArrayToInt(frame, 4))
            assertTrue(byteArrayToInt(frame, 8) in 1..20)
            assertEquals(12f, model.uiState.value.telemetry!!.speedKph, 0f)
        }
        mode("Vehicle").performClick()
        slider("speed").performTouchInput { swipe(centerRight, centerLeft, 200) }
        compose.runOnIdle { assertEquals(1, service.writes.size) }
    }

    @Test
    fun slidersUseConfirmedPhysicalRangesAndEachSendsOnlyItsOwnField() {
        showScreen()
        data class Case(val mode: String, val slider: String, val range: ClosedFloatingPointRange<Float>, val flag: Int, val offset: Int, val scale: Int)
        val cases = listOf(
            Case("Vehicle", "speed", 0f..20f, 1, 8, 1),
            Case("Steering", "angle", -540f..540f, 2, 12, 100),
            Case("BrakePedal", "ehb", 0f..20_000f, 4, 16, 1),
            Case("BrakePedal", "emb", 0f..20_000f, 8, 20, 1),
        )
        var lastMode = ""
        for (case in cases) {
            if (lastMode != case.mode) mode(case.mode).performClick()
            lastMode = case.mode
            val node = slider(case.slider).performScrollTo()
            val info = node.fetchSemanticsNode().config[SemanticsProperties.ProgressBarRangeInfo]
            assertEquals(case.range, info.range)
            assertEquals((case.range.endInclusive - case.range.start).toInt() - 1, info.steps)
            for (value in listOf(case.range.endInclusive, case.range.start)) {
                node.performSemanticsAction(SemanticsActions.SetProgress) { it(value) }
                compose.runOnIdle {
                    val raw = service.writes.last()
                    assertEquals(case.flag, byteArrayToInt(raw, 4))
                    for (offset in listOf(8, 12, 16, 20)) {
                        assertEquals(if (offset == case.offset) value.toInt() * case.scale else 0, byteArrayToInt(raw, offset))
                    }
                }
            }
        }
    }

    @Test
    fun chartsRemainReadableAndReceiverIsReleasedOnPageExit() {
        showScreen(Modifier.height(560.dp))
        for (name in listOf("angle", "ehb", "emb")) {
            compose.onNodeWithTag("chassis-$name-chart").performScrollTo().assertIsDisplayed().assertHeightIsAtLeast(70.dp)
        }
        compose.onNodeWithTag("chassis-current-offset").performScrollTo().assertIsDisplayed()
        mode("Epb").performScrollTo().assertIsDisplayed()
        if (InstrumentationRegistry.getArguments().getString("chassisScreenshot") == "true") {
            val file = File(InstrumentationRegistry.getInstrumentation().targetContext.cacheDir, "chassis-live-ui.png")
            file.outputStream().use {
                check(compose.onRoot().captureToImage().asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, it))
            }
        }
        compose.runOnIdle { model.onPageExited() }
        compose.waitForIdle()
        compose.runOnIdle { assertEquals(0, service.registeredCommandCount) }
    }

    private fun mode(name: String) = compose.onNodeWithTag("chassis-control-$name")
    private fun slider(name: String) = compose.onNodeWithTag("chassis-$name-slider")
    private fun offsetSwitch() = compose.onNodeWithTag("chassis-current-offset")

    private fun showScreen(
        modifier: Modifier = Modifier,
        withInitialReport: Boolean = true,
        repository: ChassisRepository = DefaultChassisRepository(service.manager)
    ) {
        compose.runOnIdle {
            model = ViewModelProvider(
                store, ChassisViewModel.provideFactory(repository)
            )[ChassisViewModel::class.java]
            model.onPageEntered()
        }
        compose.setContent { STCarTheme { ChassisRoute(model, modifier) } }
        compose.waitForIdle()
        compose.runOnIdle {
            assertEquals(1, service.registeredCommandCount)
            if (withInitialReport) service.receiveReport(CMDChassisReport.Response(12, 1234, 6800, 18600))
        }
        compose.waitForIdle()
    }
}

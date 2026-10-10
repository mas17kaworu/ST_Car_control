package com.longkai.stcarcontrol.st_exp.compose.ui.chassis

import android.graphics.Bitmap
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.test.platform.app.InstrumentationRegistry
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDChassisList.CMDChassisReport
import com.longkai.stcarcontrol.st_exp.communication.utils.CheckSumBit
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
        compose.onNodeWithText("EHB / EMB Braking Force").assertIsDisplayed()
        compose.onNodeWithTag("chassis-angle-reading").assertTextEquals("+12.34")
        compose.onNodeWithTag("chassis-ehb-reading").assertTextEquals("6800")
        compose.onNodeWithTag("chassis-ramp-reading").assertIsDisplayed().assertTextEquals("ramp: 12%")
        compose.onNodeWithTag("chassis-combined-brakes-input").assertIsNotEnabled()
        for (name in listOf("speed", "angle", "ehb", "emb")) {
            slider(name).assertIsNotEnabled()
        }
        offsetSwitch().assertIsEnabled().assertIsOff()
        mode("Epb").assertIsEnabled().assertIsNotSelected()
        compose.onNodeWithTag("chassis-emergency-stop")
            .assertIsDisplayed().assertIsEnabled().assertIsNotSelected()
            .assertContentDescriptionEquals("Emergency stop")
        compose.onNodeWithText("Not connected").assertDoesNotExist()
        compose.runOnIdle {
            assertEquals(1, service.registeredCommandCount)
            assertTrue(service.writes.isEmpty())
        }
    }

    @Test
    fun compactControlsAreCenteredWithStopAtTheRightEdge() {
        showScreen()
        val bar = compose.onNodeWithTag("chassis-control-bar")
            .fetchSemanticsNode().boundsInRoot
        val controls = listOf("Vehicle", "Steering", "BrakePedal", "Epb").map { name ->
            mode(name).assertIsDisplayed().assertWidthIsAtLeast(88.dp)
                .assertHeightIsAtLeast(48.dp).fetchSemanticsNode().boundsInRoot
        }
        val stop = compose.onNodeWithTag("chassis-emergency-stop")
            .assertIsDisplayed().assertWidthIsEqualTo(56.dp).assertHeightIsEqualTo(56.dp)
            .fetchSemanticsNode().boundsInRoot
        val density = InstrumentationRegistry.getInstrumentation().targetContext.resources.displayMetrics.density
        assertEquals(bar.center.x, (controls.first().left + controls.last().right) / 2, 1f)
        assertEquals(bar.right - 8 * density, stop.right, 1f)
        assertTrue(controls.last().right < stop.left)
        for ((left, right) in controls.zipWithNext()) {
            assertEquals(8 * density, right.left - left.right, 1f)
        }
        mode("Vehicle").performClick()
        mode("Epb").performClick()
        compose.onNodeWithTag("chassis-emergency-stop").performTouchInput { click() }
            .assertIsSelected()
        mode("Vehicle").assertIsSelected()
        mode("Epb").assertIsSelected()
        compose.runOnIdle {
            assertEquals(2, service.writes.size)
            assertEquals(0x3F, service.writes.last()[3].toInt())
        }
    }

    @Test
    fun stopPressFeedbackResetsAndOnlyCompletedClicksToggleEmergencyStop() {
        showScreen(withInitialReport = false)
        compose.onNodeWithTag("chassis-control-bar")
        mode("Vehicle").performClick()
        mode("Epb").performClick()
        val stop = compose.onNodeWithTag("chassis-emergency-stop")
        val initialBounds = stop.fetchSemanticsNode().boundsInRoot
        fun fillColor() = stop.captureToImage().toPixelMap().let { it[it.width / 2, it.height / 4] }
        fun assertStopColors(enabled: Boolean) {
            val pixels = stop.captureToImage().toPixelMap()
            val accent = if (enabled) Color.White else Color(0xFFEF5350)
            val fill = if (enabled) Color(0xFFEF5350) else Color(0xFF303D49)
            assertEquals(fill.toArgb(), pixels[pixels.width / 2, pixels.height / 4].toArgb())
            assertEquals(accent.toArgb(), pixels[pixels.width / 2, pixels.height / 56].toArgb())
            assertTrue(
                (pixels.width / 4 until pixels.width * 3 / 4).any { x ->
                    (pixels.height * 2 / 5 until pixels.height * 3 / 5).any { y ->
                        pixels[x, y].toArgb() == accent.toArgb()
                    }
                }
            )
        }
        repeat(2) { index ->
            assertStopColors(enabled = index == 1)
            val normalColor = fillColor()
            stop.performTouchInput { down(center) }
            compose.waitUntil(2_000) { fillColor().red < normalColor.red - .03f }
            assertEquals(initialBounds, stop.fetchSemanticsNode().boundsInRoot)
            compose.runOnIdle { assertEquals(1 + index, service.writes.size) }
            stop.performTouchInput { cancel() }
            compose.waitUntil(2_000) { fillColor() == normalColor }
            compose.runOnIdle { assertEquals(1 + index, service.writes.size) }
            stop.performTouchInput { down(center) }
            compose.waitUntil(2_000) { fillColor().red < normalColor.red - .03f }
            stop.performTouchInput { up() }
            compose.waitForIdle()
            if (index == 0) stop.assertIsSelected() else stop.assertIsNotSelected()
            assertStopColors(enabled = index == 0)
            compose.runOnIdle {
                assertEquals(2 + index, service.writes.size)
                val frame = service.writes.last()
                assertEquals(0x3F, frame[3].toInt())
                assertEquals(if (index == 0) 0x55 else 0, byteArrayToInt(frame, 4))
            }
        }
        mode("Vehicle").assertIsSelected()
        mode("Epb").assertIsSelected()
        compose.runOnIdle {
            assertEquals(3, service.writes.size)
            assertNull(model.uiState.value.error)
        }
    }

    @Test
    fun controlsCanBeEnabledAndUsedBeforeAnyReportArrives() {
        showScreen(withInitialReport = false)
        compose.onNodeWithTag("chassis-ramp-reading").assertTextEquals("ramp: --%")
        mode("Vehicle").assertIsEnabled().performClick()
        slider("speed").assertIsEnabled()
        val input = compose.onNodeWithTag("chassis-speed-input")
        input.performTextReplacement("-7")
        mode("Steering").assertIsEnabled().performClick()
        compose.runOnIdle { assertTrue(service.writes.isEmpty()) }
        input.performImeAction()
        compose.runOnIdle {
            assertNull(model.uiState.value.telemetry)
            assertNull(model.uiState.value.error)
            assertEquals(1, service.writes.size)
            assertEquals(-7, byteArrayToInt(service.writes.single(), 8))
        }
        input.performTextReplacement("21")
        input.performImeAction()
        compose.onNodeWithText("Enter a whole number within the supported range.").assertIsDisplayed()
        compose.onNodeWithText("Dismiss").performClick()
        compose.runOnIdle { assertEquals(1, service.writes.size) }
        slider("angle").assertIsEnabled()
        slider("speed").assertIsEnabled()
        mode("BrakePedal").assertIsEnabled().performClick()
        slider("ehb").assertIsEnabled()
        slider("emb").assertIsEnabled()
        slider("angle").assertIsEnabled()
        val combined = compose.onNodeWithTag("chassis-combined-brakes-input")
        combined.assertIsEnabled().performTextReplacement("8000")
        mode("Vehicle").performClick()
        compose.runOnIdle {
            assertEquals(1, service.writes.size)
            assertEquals(8000, model.uiState.value.controls.ehbForceN)
            assertEquals(8000, model.uiState.value.controls.embForceN)
        }
        combined.performImeAction()
        compose.runOnIdle {
            assertEquals(2, service.writes.size)
            val frame = service.writes.last()
            assertEquals(27, frame.size)
            assertEquals(0x0C, byteArrayToInt(frame, 4))
            assertEquals(8000, byteArrayToInt(frame, 16))
            assertEquals(8000, byteArrayToInt(frame, 20))
            assertEquals(0, byteArrayToInt(frame, 8))
            assertEquals(0, byteArrayToInt(frame, 12))
            assertEquals(CheckSumBit.checkSum(frame.copyOfRange(2, 26), 24), frame[26])
            assertNull(model.uiState.value.telemetry)
        }
        compose.onNodeWithTag("chassis-ehb-input").performTextReplacement("6000")
        compose.onNodeWithTag("chassis-ehb-input").performImeAction()
        assertEquals("", combined.fetchSemanticsNode().config[SemanticsProperties.EditableText].text)
        assertEquals("8000", compose.onNodeWithTag("chassis-emb-input")
            .fetchSemanticsNode().config[SemanticsProperties.EditableText].text)
        compose.runOnIdle {
            assertEquals(3, service.writes.size)
            assertEquals(4, byteArrayToInt(service.writes.last(), 4))
            assertEquals(6000, byteArrayToInt(service.writes.last(), 16))
            assertEquals(0, byteArrayToInt(service.writes.last(), 20))
        }
        combined.performTextReplacement("20001")
        combined.performImeAction()
        compose.onNodeWithText("Enter a whole number within the supported range.").assertIsDisplayed()
        compose.onNodeWithText("Dismiss").performClick()
        mode("BrakePedal").performClick()
        combined.assertIsNotEnabled()
        compose.runOnIdle {
            model.onCombinedBrakesInputSubmitted("9000", 1)
            assertEquals(3, service.writes.size)
        }
    }

    @Test
    fun independentSwitchesSendOnAndOffWithoutSelectingAControlGroup() {
        showScreen(withInitialReport = false)
        mode("Epb").performClick().assertIsSelected()
        offsetSwitch().performClick().assertIsOn()
        mode("Epb").assertIsSelected()
        compose.runOnIdle {
            assertTrue(model.uiState.value.enabledControls.isEmpty())
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
        offsetSwitch().performClick()
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
            service.receiveReport(CMDChassisReport.Response(5, 100, 200, 300, rampRaw = 0))
        }
        compose.waitForIdle()
        compose.onNodeWithTag("chassis-angle-reading").assertTextEquals("+1.00")
        mode("Vehicle").assertIsSelected()
        var generation = 0L
        compose.runOnIdle {
            generation = model.uiState.value.generation(ChassisControlField.Speed)
            model.onControlChanged(ChassisControlField.Speed, 10, generation)
        }
        mode("Epb").performClick().assertIsNotSelected()
        offsetSwitch().performClick().assertIsOff()
        mode("Vehicle").assertIsSelected()
        slider("speed").assertIsEnabled()
        compose.runOnIdle {
            assertEquals(generation, model.uiState.value.generation(ChassisControlField.Speed))
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
        offsetSwitch().performClick()
        val stop = compose.onNodeWithTag("chassis-emergency-stop")
        stop.performClick().assertIsSelected()
        for (name in listOf("Vehicle", "Steering", "BrakePedal")) {
            mode(name).performClick().assertIsSelected()
            stop.assertIsSelected()
        }
        compose.runOnIdle { service.receiveReport(CMDChassisReport.Response(5, 100, 200, 300, rampRaw = 0)) }
        compose.waitForIdle()
        stop.assertIsSelected()
        compose.runOnIdle {
            model.onPageExited()
            model.onEpbToggled()
            model.onCurrentOffsetChanged(false)
            model.onEmergencyStopToggled()
        }
        compose.waitForIdle()
        compose.runOnIdle {
            assertEquals(0, service.registeredCommandCount)
            assertTrue(model.uiState.value.epbEnabled)
            assertTrue(model.uiState.value.currentOffsetEnabled)
            assertTrue(model.uiState.value.emergencyStopEnabled)
            assertEquals(3, service.writes.size)
            model.onPageEntered()
        }
        compose.waitForIdle()
        mode("Epb").assertIsSelected()
        offsetSwitch().assertIsOn()
        stop.assertIsSelected()
        compose.runOnIdle {
            assertEquals(1, service.registeredCommandCount)
            assertEquals(3, service.writes.size)
        }
    }

    @Test
    fun rejectedEmergencyStopChangesKeepTheLastTargetAndOtherControls() {
        val liveRepository = DefaultChassisRepository(service.manager)
        var reject = true
        val repository = object : ChassisRepository by liveRepository {
            override suspend fun setEmergencyStop(enabled: Boolean): ChassisCommandResult =
                if (reject) ChassisCommandResult.Rejected(ChassisError.NotReady)
                else liveRepository.setEmergencyStop(enabled)
        }
        showScreen(repository = repository)
        mode("Vehicle").performClick()
        mode("Epb").performClick()
        val stop = compose.onNodeWithTag("chassis-emergency-stop")
        stop.performClick()
        compose.onNodeWithText("Dismiss").performClick()
        stop.assertIsNotSelected()
        compose.runOnIdle {
            assertEquals(1, service.writes.size)
            reject = false
        }
        stop.performClick().assertIsSelected()
        compose.runOnIdle { reject = true }
        stop.performClick()
        compose.onNodeWithText("Dismiss").performClick()
        stop.assertIsSelected()
        mode("Vehicle").assertIsSelected()
        mode("Epb").assertIsSelected()
        slider("speed").assertIsEnabled()
        compose.runOnIdle { assertEquals(2, service.writes.size) }
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
        offsetSwitch().performClick()
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
    fun modesAreIndependentAndClickingAgainLocksOnlyThatGroupWithoutSending() {
        showScreen()
        mode("Vehicle").performClick().assertIsSelected()
        slider("speed").assertIsEnabled()
        slider("angle").assertIsNotEnabled()
        mode("Steering").performClick().assertIsSelected()
        mode("Vehicle").assertIsSelected()
        slider("speed").assertIsEnabled()
        slider("angle").assertIsEnabled()
        mode("BrakePedal").performClick().assertIsSelected()
        slider("ehb").assertIsEnabled()
        slider("emb").assertIsEnabled()
        slider("angle").assertIsEnabled()
        mode("BrakePedal").performClick().assertIsNotSelected()
        slider("ehb").assertIsNotEnabled()
        slider("emb").assertIsNotEnabled()
        slider("speed").assertIsEnabled()
        slider("angle").assertIsEnabled()
        compose.runOnIdle {
            assertTrue(service.writes.isEmpty())
            service.receiveReport(CMDChassisReport.Response(7, -12345, 1234, 5678, rampRaw = 0))
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
            Case("Vehicle", "speed", -20f..20f, 1, 8, 1),
            Case("Steering", "angle", -32f..32f, 2, 12, 100),
            Case("BrakePedal", "ehb", 0f..20_000f, 4, 16, 1),
            Case("BrakePedal", "emb", 0f..20_000f, 8, 20, 1),
        )
        var lastMode = ""
        for (case in cases) {
            if (lastMode != case.mode) mode(case.mode).performClick()
            lastMode = case.mode
            val node = slider(case.slider)
            val info = node.fetchSemanticsNode().config[SemanticsProperties.ProgressBarRangeInfo]
            assertEquals(case.range, info.range)
            assertEquals((case.range.endInclusive - case.range.start).toInt() - 1, info.steps)
            for (value in listOf(case.range.endInclusive, case.range.start, 0f)) {
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
    fun speedSliderRemainsSignedWhileGaugeShowsNonnegativeFeedback() {
        showScreen(withInitialReport = false)
        val info = slider("speed").fetchSemanticsNode().config[SemanticsProperties.ProgressBarRangeInfo]
        assertEquals(-20f..20f, info.range)
        assertEquals(0f, info.current, 0f)
        assertEquals(39, info.steps)
        val gauge = compose.onNodeWithTag("chassis-speed-gauge")
        for (speed in listOf(0, 10, 20)) {
            compose.runOnIdle { service.receiveReport(CMDChassisReport.Response(speed.toLong(), 0, 0, 0, rampRaw = 0)) }
            compose.waitForIdle()
            gauge.assertContentDescriptionEquals("Vehicle Speed: $speed km/h")
            val pixels = gauge.captureToImage().toPixelMap()
            var left = 0
            var right = 0
            for (y in 0 until pixels.height) {
                for (x in 0 until pixels.width) {
                    if (pixels[x, y].toArgb() == Color(0xFF3ABEE5).toArgb()) {
                        if (x < pixels.width / 2) left++ else right++
                    }
                }
            }
            when {
                speed == 10 -> assertTrue(left > 10 && left > right * 3)
                speed == 20 -> assertTrue(left > 10 && right > 10)
                else -> assertEquals(0, left + right)
            }
        }
        compose.runOnIdle { assertTrue(service.writes.isEmpty()) }
    }

    @Test
    fun chartsRemainReadableAndReceiverIsReleasedOnPageExit() {
        var timestamp = 0L
        showScreen(Modifier.height(560.dp), withInitialReport = false,
            repository = DefaultChassisRepository(service.manager, clockMillis = { timestamp }))
        compose.runOnIdle { service.receiveReport(CMDChassisReport.Response(12, 1234, 19000, 18000, rampRaw = 0)) }
        compose.waitForIdle()
        for (second in 1..4) {
            compose.runOnIdle {
                timestamp = second * 1_000L
                service.receiveReport(CMDChassisReport.Response(12, 1234, 6000, 12000, rampRaw = 12))
            }
            compose.waitForIdle()
        }
        for (name in listOf("angle", "braking")) {
            compose.onNodeWithTag("chassis-$name-chart").assertIsDisplayed().assertHeightIsAtLeast(70.dp)
        }
        val speed = compose.onNodeWithTag("chassis-speed-card").fetchSemanticsNode().boundsInRoot
        val angle = compose.onNodeWithTag("chassis-angle-card").fetchSemanticsNode().boundsInRoot
        val braking = compose.onNodeWithTag("chassis-braking-card").fetchSemanticsNode().boundsInRoot
        assertTrue(speed.bottom < angle.top)
        assertTrue(speed.right < braking.left)
        assertEquals(speed.top, braking.top, 1f)
        assertEquals(angle.bottom, braking.bottom, 1f)
        val chart = compose.onNodeWithTag("chassis-braking-chart")
        chart.assertTextEquals("-3s -2s -1s now")
        val pixels = chart.captureToImage().toPixelMap()
        for (color in listOf(Color(0xFF45D6FF), Color(0xFFFFBC5C))) {
            val points = mutableListOf<Pair<Int, Int>>()
            for (y in 0 until pixels.height) for (x in 0 until pixels.width) {
                if (pixels[x, y].toArgb() == color.toArgb()) points.add(x to y)
            }
            assertTrue(points.isNotEmpty())
            assertTrue(points.maxOf { it.first } - points.minOf { it.first } > pixels.width * .75f)
            assertTrue(points.maxOf { it.second } - points.minOf { it.second } < pixels.height * .05f)
        }
        compose.onNodeWithTag("chassis-current-offset").assertIsDisplayed()
        compose.onNodeWithTag("chassis-control-bar").assertIsDisplayed()
        compose.onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsProperties.VerticalScrollAxisRange))
            .assertCountEquals(0)
        mode("Epb").assertIsDisplayed()
        compose.onNodeWithTag("chassis-emergency-stop").assertIsDisplayed()
        compose.onNodeWithText("Not connected").assertDoesNotExist()
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
            if (withInitialReport) service.receiveReport(CMDChassisReport.Response(12, 1234, 6800, 18600, rampRaw = 12))
        }
        compose.waitForIdle()
    }
}

package com.longkai.stcarcontrol.st_exp.compose.ui.chassis

import android.graphics.Bitmap
import android.os.Looper
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
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.test.platform.app.InstrumentationRegistry
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDChassisList.CMDChassisReport
import com.longkai.stcarcontrol.st_exp.communication.utils.CheckSumBit
import com.longkai.stcarcontrol.st_exp.communication.utils.byteArrayToInt
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.*
import com.longkai.stcarcontrol.st_exp.compose.ui.theme.STCarTheme
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.util.concurrent.atomic.AtomicInteger

class ChassisScreenTest {
    @get:Rule val compose = createComposeRule()
    private val store = ViewModelStore()
    private lateinit var service: ChassisServiceFixture
    private lateinit var model: ChassisViewModel

    @Before fun setUp() { service = ChassisServiceFixture() }

    @After fun close() {
        try {
            compose.runOnIdle { store.clear() }
            compose.waitUntil(5_000) { service.registeredCommandCount == 0 }
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
        for (name in listOf("angle", "ehb", "emb")) {
            val reading = compose.onNodeWithTag("chassis-$name-reading")
            val layouts = mutableListOf<TextLayoutResult>()
            reading.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
            val layout = layouts.single()
            assertFalse("$name reading overflows its text layout", layout.hasVisualOverflow)
            assertTrue("$name reading is clipped by its container",
                reading.fetchSemanticsNode().boundsInRoot.height >= layout.size.height - 1f)
        }
        val ramp = compose.onNodeWithTag("chassis-ramp-reading")
            .assertIsDisplayed().assertTextEquals("ramp: 12.34%").fetchSemanticsNode().boundsInRoot
        val voltage = compose.onNodeWithTag("chassis-voltage-reading")
            .assertIsDisplayed().assertTextEquals("voltage: 483.60 V").fetchSemanticsNode().boundsInRoot
        assertTrue(ramp.right < voltage.left)
        assertEquals(ramp.center.y, voltage.center.y, 1f)
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
        val maximum = compose.onNodeWithTag("chassis-brake-chart-max-input")
            .assertIsDisplayed().assertIsEnabled().assertContentDescriptionEquals("Y Max (N)")
        assertEquals("20000", maximum.fetchSemanticsNode().config[SemanticsProperties.EditableText].text)
        maximum.performClick().performTextReplacement("40000")
        for ((raw, rampText, voltageText) in listOf(
            Triple(0, "0.00", "0.00"), Triple(1, "0.01", "0.10"),
            Triple(1200, "12.00", "120.00"), Triple(65535, "655.35", "6553.50")
        )) {
            compose.runOnIdle {
                service.receiveReport(CMDChassisReport.Response(12, 1234, 6800, 18600, rampRaw = raw, voltageRaw = raw))
            }
            compose.waitUntil(5_000) { model.telemetryState.value.telemetry?.rampPercent == raw / 100f }
            compose.onNodeWithTag("chassis-ramp-reading").assertTextEquals("ramp: $rampText%")
            compose.onNodeWithTag("chassis-voltage-reading").assertTextEquals("voltage: $voltageText V")
            assertEquals("40000", maximum.fetchSemanticsNode().config[SemanticsProperties.EditableText].text)
            compose.runOnIdle { assertEquals(20000, model.uiState.value.brakeChartMaxN) }
        }
        mode("Vehicle").performClick()
        mode("Vehicle").performClick()
        compose.runOnIdle { assertEquals("Focus loss must not apply the scale", 20000, model.uiState.value.brakeChartMaxN) }
        maximum.performImeAction()
        compose.runOnIdle { assertEquals(40000, model.uiState.value.brakeChartMaxN) }
        for (invalid in listOf("", "0", "-1", "1.5", "2147483648")) {
            maximum.performTextReplacement(invalid)
            maximum.performImeAction()
            compose.onNodeWithText("Enter a whole number within the supported range.").assertIsDisplayed()
            compose.onNodeWithText("Dismiss").performClick()
            compose.runOnIdle { assertEquals(40000, model.uiState.value.brakeChartMaxN) }
        }
        for (valid in listOf(1, Int.MAX_VALUE, 20000)) {
            maximum.performTextReplacement(valid.toString())
            maximum.performImeAction()
            compose.runOnIdle {
                assertEquals(valid, model.uiState.value.brakeChartMaxN)
                assertTrue(service.writes.isEmpty())
                assertTrue(model.uiState.value.enabledControls.isEmpty())
                assertEquals(ChassisControlState(), model.uiState.value.controls)
                assertNull(model.uiState.value.brakeResponse)
            }
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
        val times = listOf("ehb", "emb").map { name ->
            compose.onNodeWithTag("chassis-$name-time-input").assertIsDisplayed().assertIsEnabled()
                .assertWidthIsEqualTo(72.dp).assertHeightIsEqualTo(48.dp)
                .fetchSemanticsNode().boundsInRoot
        }
        val density = InstrumentationRegistry.getInstrumentation().targetContext.resources.displayMetrics.density
        assertEquals(bar.center.x, (controls.first().left + times.last().right) / 2, 1f)
        assertEquals(bar.right - 8 * density, stop.right, 1f)
        assertTrue(controls.last().right < times.first().left)
        assertTrue(times.first().right < times.last().left)
        assertTrue(times.last().right < stop.left)
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
    fun brakeTimesSendOnEnterWithoutEnableBitsOrSendingTheOtherDraft() {
        val liveRepository = DefaultChassisRepository(service.manager)
        var rejectTimes = false
        val repository = object : ChassisRepository by liveRepository {
            override suspend fun submitBrakeTimes(times: ChassisBrakeTimes): ChassisCommandResult =
                if (rejectTimes) ChassisCommandResult.Rejected(ChassisError.NotReady)
                else liveRepository.submitBrakeTimes(times)
        }
        showScreen(withInitialReport = false, repository = repository)
        val ehb = compose.onNodeWithTag("chassis-ehb-time-input")
            .assertIsEnabled().assertContentDescriptionEquals("EHB time (s)")
        val emb = compose.onNodeWithTag("chassis-emb-time-input")
            .assertIsEnabled().assertContentDescriptionEquals("EMB time (s)")
        fun assertTimes(ehbSeconds: Int, embSeconds: Int, writes: Int, mask: Int = 0) {
            compose.runOnIdle {
                assertEquals(writes, service.writes.size)
                val frame = service.writes.last()
                assertEquals(29, frame.size)
                assertEquals(0x1A, frame[2].toInt())
                assertEquals(0x3D, frame[3].toInt())
                assertEquals(mask, byteArrayToInt(frame, 4))
                assertEquals(ehbSeconds, frame[26].toInt() and 0xFF)
                assertEquals(embSeconds, frame[27].toInt() and 0xFF)
                assertEquals(CheckSumBit.checkSum(frame.copyOfRange(2, 28), 26), frame[28])
                if (mask == 0) assertTrue(frame.copyOfRange(8, 26).all { it == 0.toByte() })
                assertEquals(ChassisBrakeTimes(ehbSeconds, embSeconds), model.uiState.value.brakeTimes)
                assertEquals(model.uiState.value.brakeTimes, repository.brakeTimes)
                assertNull(model.uiState.value.brakeResponse)
                assertFalse(model.uiState.value.comparisonOpen)
            }
        }
        assertEquals("0", ehb.fetchSemanticsNode().config[SemanticsProperties.EditableText].text)
        assertEquals("0", emb.fetchSemanticsNode().config[SemanticsProperties.EditableText].text)
        ehb.performTextReplacement("12")
        emb.performTextReplacement("34")
        compose.runOnIdle { assertTrue(service.writes.isEmpty()) }
        ehb.performImeAction()
        assertTimes(12, 0, 1)
        assertEquals("34", emb.fetchSemanticsNode().config[SemanticsProperties.EditableText].text)
        compose.runOnIdle {
            assertTrue(model.uiState.value.enabledControls.isEmpty())
            service.receiveReport(CMDChassisReport.Response(12, 0, 6000, 7000, 0, 480))
        }
        compose.waitUntil(5_000) { model.telemetryState.value.telemetry != null }
        assertEquals("34", emb.fetchSemanticsNode().config[SemanticsProperties.EditableText].text)
        emb.performImeAction()
        assertTimes(12, 34, 2)
        ehb.performTextReplacement("60")
        emb.performTextReplacement("255")
        emb.performImeAction()
        assertTimes(12, 255, 3)
        assertEquals("60", ehb.fetchSemanticsNode().config[SemanticsProperties.EditableText].text)
        for (invalid in listOf("", "-1", "256", "1.5")) {
            ehb.performTextReplacement(invalid)
            ehb.performImeAction()
            compose.onNodeWithText("Enter a whole number within the supported range.").assertIsDisplayed()
            compose.onNodeWithText("Dismiss").performClick()
            assertTimes(12, 255, 3)
        }
        ehb.performTextReplacement("0")
        ehb.performImeAction()
        assertTimes(0, 255, 4)
        ehb.performImeAction()
        assertTimes(0, 255, 5)
        for (mode in listOf("Vehicle", "Steering", "BrakePedal")) {
            mode(mode).performClick()
            ehb.assertIsEnabled()
            emb.assertIsEnabled()
            mode(mode).performClick()
        }
        ehb.performClick().performTextReplacement("42")
        InstrumentationRegistry.getInstrumentation().sendKeyDownUpSync(android.view.KeyEvent.KEYCODE_ENTER)
        compose.waitForIdle()
        assertTimes(42, 255, 6)
        mode("Vehicle").performClick()
        slider("speed").performSemanticsAction(SemanticsActions.SetProgress) { it(5f) }
        assertTimes(42, 255, 7, mask = 1)
        mode("Epb").performClick()
        assertTimes(42, 255, 8, mask = 32)
        offsetSwitch().performClick()
        assertTimes(42, 255, 9, mask = 16)
        compose.runOnIdle { rejectTimes = true }
        emb.performTextReplacement("5")
        emb.performImeAction()
        compose.onNodeWithText("Dismiss").performClick()
        assertTimes(42, 255, 9, mask = 16)
        mode("Vehicle").assertIsSelected()
        mode("Epb").assertIsSelected()
        offsetSwitch().assertIsOn()
        compose.runOnIdle { rejectTimes = false }
        emb.performImeAction()
        assertTimes(42, 5, 10)
        compose.runOnIdle {
            model.onPageExited()
            model.onEhbTimeSubmitted("99")
            model.onEmbTimeSubmitted("99")
        }
        compose.waitUntil(5_000) { service.registeredCommandCount == 0 }
        compose.runOnIdle { model.onPageEntered() }
        compose.waitUntil(5_000) { service.registeredCommandCount == 1 }
        assertTimes(42, 5, 10)
        compose.runOnIdle {
            assertEquals(ChassisBrakeTimes(42, 5), ChassisViewModel(repository).uiState.value.brakeTimes)
        }
        ehb.assertIsEnabled()
        emb.assertIsEnabled()
        if (InstrumentationRegistry.getArguments().getString("chassisScreenshot") == "true") {
            File(InstrumentationRegistry.getInstrumentation().targetContext.cacheDir, "chassis-times-ui.png")
                .outputStream().use {
                    check(compose.onRoot().captureToImage().asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, it))
                }
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
        compose.onNodeWithTag("chassis-voltage-reading").assertTextEquals("voltage: -- V")
        mode("Vehicle").assertIsEnabled().performClick()
        slider("speed").assertIsEnabled()
        val input = compose.onNodeWithTag("chassis-speed-input")
        input.performTextReplacement("-7")
        mode("Steering").assertIsEnabled().performClick()
        compose.runOnIdle { assertTrue(service.writes.isEmpty()) }
        input.performImeAction()
        compose.runOnIdle {
            assertNull(model.telemetryState.value.telemetry)
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
            assertEquals(29, frame.size)
            assertEquals(0x0C, byteArrayToInt(frame, 4))
            assertEquals(8000, byteArrayToInt(frame, 16))
            assertEquals(8000, byteArrayToInt(frame, 20))
            assertEquals(0, byteArrayToInt(frame, 8))
            assertEquals(0, byteArrayToInt(frame, 12))
            assertEquals(0, frame[26].toInt())
            assertEquals(0, frame[27].toInt())
            assertEquals(CheckSumBit.checkSum(frame.copyOfRange(2, 28), 26), frame[28])
            assertNull(model.telemetryState.value.telemetry)
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
            assertNull(model.telemetryState.value.telemetry)
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
            service.receiveReport(CMDChassisReport.Response(5, 100, 200, 300, rampRaw = 0, voltageRaw = 0))
        }
        compose.waitUntil(5_000) { model.telemetryState.value.telemetry?.steeringAngleDegrees == 1f }
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
        compose.runOnIdle { service.receiveReport(CMDChassisReport.Response(5, 100, 200, 300, rampRaw = 0, voltageRaw = 0)) }
        compose.waitForIdle()
        stop.assertIsSelected()
        compose.runOnIdle {
            model.onPageExited()
            model.onEpbToggled()
            model.onCurrentOffsetChanged(false)
            model.onEmergencyStopToggled()
        }
        compose.waitUntil(5_000) { service.registeredCommandCount == 0 }
        compose.runOnIdle {
            assertEquals(0, service.registeredCommandCount)
            assertTrue(model.uiState.value.epbEnabled)
            assertTrue(model.uiState.value.currentOffsetEnabled)
            assertTrue(model.uiState.value.emergencyStopEnabled)
            assertEquals(3, service.writes.size)
            model.onPageEntered()
            repeat(20) {
                model.onPageExited()
                model.onPageEntered()
            }
        }
        compose.waitUntil(5_000) { service.registeredCommandCount == 1 }
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
            service.receiveReport(CMDChassisReport.Response(7, -12345, 1234, 5678, rampRaw = 0, voltageRaw = 0))
        }
        compose.waitUntil(5_000) { model.telemetryState.value.telemetry?.steeringAngleDegrees == -123.45f }
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
            assertEquals(12f, model.telemetryState.value.telemetry!!.speedKph, 0f)
        }
        mode("Vehicle").performClick()
        slider("speed").performTouchInput { swipe(centerRight, centerLeft, 200) }
        compose.runOnIdle { assertEquals(1, service.writes.size) }
        mode("BrakePedal").performClick()
        for ((index, name) in listOf("ehb", "emb").withIndex()) {
            val brake = slider(name)
            brake.performTouchInput {
                down(centerLeft)
                moveTo(Offset(width * .63f, center.y))
            }
            compose.runOnIdle { assertEquals(1 + index, service.writes.size) }
            val value = brake.fetchSemanticsNode().config[SemanticsProperties.ProgressBarRangeInfo].current
            assertEquals(value.toInt().toFloat(), value, 0f)
            brake.performTouchInput { up() }
            compose.runOnIdle {
                assertEquals(2 + index, service.writes.size)
                assertEquals(value.toInt(), byteArrayToInt(service.writes.last(), if (name == "ehb") 16 else 20))
            }
        }
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
            for (value in listOf(case.range.endInclusive, case.range.start, 0f, 1f, 2f)) {
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
            compose.runOnIdle { service.receiveReport(CMDChassisReport.Response(speed.toLong(), 0, 0, 0, rampRaw = 0, voltageRaw = 0)) }
            compose.waitUntil(5_000) { model.telemetryState.value.telemetry?.speedKph == speed.toFloat() }
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
        val liveRepository = DefaultChassisRepository(service.manager, clockMillis = { timestamp })
        val repository = object : ChassisRepository by liveRepository {
            override val telemetry = liveRepository.telemetry.onEach {
                assertNotEquals("Telemetry collection must not run on the UI thread",
                    Looper.getMainLooper(), Looper.myLooper())
            }
        }
        showScreen(Modifier.height(560.dp), withInitialReport = false,
            repository = repository)
        val initialControlState = model.uiState.value
        compose.runOnIdle { service.receiveReport(CMDChassisReport.Response(12, 1234, 19000, 18000, rampRaw = 0, voltageRaw = 4836)) }
        compose.waitUntil(5_000) { model.telemetryState.value.telemetry?.timestampMillis == timestamp }
        for (second in 1..4) {
            compose.runOnIdle {
                timestamp = second * 1_000L
                service.receiveReport(CMDChassisReport.Response(12, 1234,
                    if (second <= 2) 19000 else 6000, if (second <= 2) 18000 else 12000,
                    rampRaw = 1234, voltageRaw = 4836))
            }
            compose.waitUntil(5_000) { model.telemetryState.value.telemetry?.timestampMillis == timestamp }
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
        chart.assertTextEquals("-1s now")
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
        val receivedTimes = (0..4).map { it * 1_000L }.toMutableList()
        for (intervalMillis in listOf(10L, 5L, 20L)) {
            repeat(6) {
                compose.runOnIdle {
                    repeat(100) {
                        timestamp += intervalMillis
                        receivedTimes += timestamp
                        service.receiveReport(CMDChassisReport.Response(12, 1234, 6000, 12000,
                            rampRaw = 1234, voltageRaw = 4836))
                    }
                }
                compose.waitUntil(5_000) { model.telemetryState.value.telemetry?.timestampMillis == timestamp }
            }
            compose.runOnIdle {
                val cutoff = timestamp - 10_000L
                val expectedTimes = listOfNotNull(receivedTimes.lastOrNull { it < cutoff }) +
                    receivedTimes.filter { it >= cutoff }
                val feedback = model.telemetryState.value
                assertEquals(expectedTimes, feedback.history.map { it.timestampMillis })
                assertEquals(expectedTimes, feedback.steeringPoints.map { it.timestampMillis })
                val brakeCutoff = timestamp - 1_000L
                val brakeTimes = listOfNotNull(receivedTimes.lastOrNull { it < brakeCutoff }) +
                    receivedTimes.filter { it >= brakeCutoff }
                assertEquals(brakeTimes, feedback.ehbPoints.map { it.timestampMillis })
                assertEquals(brakeTimes, feedback.embPoints.map { it.timestampMillis })
                assertSame("Reports must not update control/input state", initialControlState, model.uiState.value)
            }
        }
        val angleChart = compose.onNodeWithTag("chassis-angle-chart")
        angleChart.assertTextEquals("-10s -5s now")
        val anglePixels = angleChart.captureToImage().toPixelMap()
        var firstCurveX = anglePixels.width
        var lastCurveX = -1
        for (y in 0 until anglePixels.height) for (x in 0 until anglePixels.width) {
            if (anglePixels[x, y].toArgb() == Color(0xFF3ABEE5).toArgb()) {
                firstCurveX = minOf(firstCurveX, x)
                lastCurveX = maxOf(lastCurveX, x)
            }
        }
        assertTrue("Steering history should span the plot after receiving more than 10 seconds of reports",
            lastCurveX - firstCurveX > anglePixels.width * .8f)
        val ehbColor = Color(0xFF45D6FF)
        val embColor = Color(0xFFFFBC5C)
        val beforeEhb = curvePixels(chart, ehbColor)
        val beforeEmb = curvePixels(chart, embColor)
        val feedbackBeforeScale = model.telemetryState.value
        val maximum = compose.onNodeWithTag("chassis-brake-chart-max-input")
        maximum.performTextReplacement("40000")
        assertEquals(beforeEhb, curvePixels(chart, ehbColor))
        maximum.performImeAction()
        val afterEhb = curvePixels(chart, ehbColor)
        val afterEmb = curvePixels(chart, embColor)
        val originalSeparation = beforeEhb.map { it.second }.average() - beforeEmb.map { it.second }.average()
        val scaledSeparation = afterEhb.map { it.second }.average() - afterEmb.map { it.second }.average()
        assertEquals("Doubling Y Max must halve the vertical separation", originalSeparation / 2, scaledSeparation, 2.0)
        maximum.performTextReplacement("5000")
        maximum.performImeAction()
        assertTrue("EHB above Y Max must be clipped, not flattened", curvePixels(chart, ehbColor).isEmpty())
        assertTrue("EMB above Y Max must be clipped, not flattened", curvePixels(chart, embColor).isEmpty())
        compose.onNodeWithTag("chassis-ehb-reading").assertTextEquals("6000")
        compose.onNodeWithTag("chassis-emb-reading").assertTextEquals("12000")
        maximum.performTextReplacement("20000")
        maximum.performImeAction()
        assertEquals(beforeEhb, curvePixels(chart, ehbColor))
        assertEquals(beforeEmb, curvePixels(chart, embColor))
        compose.runOnIdle {
            assertSame(feedbackBeforeScale, model.telemetryState.value)
            assertTrue(service.writes.isEmpty())
            assertEquals(0..20000, model.uiState.value.config.ehbForceN)
            assertEquals(0..20000, model.uiState.value.config.embForceN)
        }
        mode("BrakePedal").performClick()
        val input = compose.onNodeWithTag("chassis-ehb-input").performClick()
        runBlocking {
            val updates = AtomicInteger()
            val startTime = System.nanoTime()
            val monitor = launch(Dispatchers.Default, start = CoroutineStart.UNDISPATCHED) {
                model.telemetryState.collect { updates.incrementAndGet() }
            }
            val traffic = launch(Dispatchers.Default) {
                repeat(200) {
                    InstrumentationRegistry.getInstrumentation().runOnMainSync {
                        timestamp += 10L
                        service.receiveReport(CMDChassisReport.Response(12, 1234, 6000, 12000,
                            rampRaw = 1234, voltageRaw = 4836))
                    }
                    delay(10)
                }
            }
            try {
                input.performTextReplacement("1234")
                input.performTextInput("5")
                traffic.join()
                compose.waitUntil(5_000) { model.telemetryState.value.telemetry?.timestampMillis == timestamp }
                input.assertIsFocused()
                assertEquals("12345", input.fetchSemanticsNode().config[SemanticsProperties.EditableText].text)
                compose.runOnIdle {
                    assertEquals(12345, model.uiState.value.controls.ehbForceN)
                    assertTrue("Typing and reports must not send commands", service.writes.isEmpty())
                }
                val elapsedMillis = (System.nanoTime() - startTime) / 1_000_000L
                assertTrue("Display updates must be capped independently of report traffic",
                    updates.get() <= elapsedMillis / ChassisViewModel.DISPLAY_INTERVAL_MILLIS + 2)
                assertTrue("Feedback should keep updating while typing", updates.get() > 1)
            } finally {
                traffic.cancelAndJoin()
                monitor.cancelAndJoin()
            }
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
        val compare = compose.onNodeWithTag("chassis-compare-button").assertIsDisplayed().assertIsEnabled()
        val compareBounds = compare.fetchSemanticsNode().boundsInRoot
        assertTrue(compareBounds.left >= offsetSwitch().fetchSemanticsNode().boundsInRoot.right)
        assertTrue(compareBounds.right <= braking.right)
        compare.performClick()
        compose.onNodeWithTag("chassis-compare-dialog").assertIsDisplayed()
        compose.onNodeWithTag("chassis-compare-chart").assertIsDisplayed()
            .assertTextEquals("0 100 200 300 400 500 ms")
        compose.onNodeWithText("Without current offset").assertIsDisplayed()
        compose.onNodeWithText("With current offset").assertIsDisplayed()
        compose.onNodeWithText("Waiting for comparison data…").assertIsDisplayed()
        compose.onNodeWithText("Preview data").assertDoesNotExist()
        val withoutOffset = List(50) { it * 1000 }
        val withOffset = List(50) { 65535 - it * 700 }
        val comparisonFrame = service.comparisonFrame(withoutOffset, withOffset)
        val requestFrame = byteArrayOf(0x5A, 0x3C, 0x02, 0x40, 0xBD.toByte())
        compose.runOnIdle {
            assertEquals(2, service.registeredCommandCount)
            assertArrayEquals(requestFrame, service.writes.single())
            service.receiveFrame(comparisonFrame)
        }
        compose.waitForIdle()
        compose.onNodeWithTag("chassis-compare-status").assertDoesNotExist()
        compose.runOnIdle {
            assertEquals(ChassisComparisonResult.Success(ChassisComparison(withoutOffset, withOffset)),
                model.uiState.value.comparisonResult)
            assertEquals(1, service.registeredCommandCount)
            assertEquals(6000f, model.telemetryState.value.telemetry!!.ehbForceN, 0f)
        }
        val comparisonChart = compose.onNodeWithTag("chassis-compare-chart")
        assertTrue(curvePixels(comparisonChart, ehbColor).isNotEmpty())
        assertTrue("Compare must use the shared maximum without automatic expansion",
            curvePixels(comparisonChart, embColor).isEmpty())
        val comparisonBeforeScale = model.uiState.value.comparisonResult
        compose.runOnIdle { model.onBrakeChartMaxSubmitted("70000") }
        val comparisonPixels = comparisonChart.captureToImage().toPixelMap()
        for (color in listOf(Color(0xFF45D6FF), Color(0xFFFFBC5C))) {
            var count = 0
            var top = comparisonPixels.height
            var bottom = 0
            for (y in 0 until comparisonPixels.height) for (x in 0 until comparisonPixels.width) {
                if (comparisonPixels[x, y].toArgb() == color.toArgb()) {
                    count++
                    top = minOf(top, y)
                    bottom = maxOf(bottom, y)
                }
            }
            assertTrue(count > 50)
            assertTrue(bottom - top > comparisonPixels.height * .4f)
        }
        compose.runOnIdle {
            assertSame(comparisonBeforeScale, model.uiState.value.comparisonResult)
            assertEquals(1, service.writes.size)
        }
        if (InstrumentationRegistry.getArguments().getString("chassisScreenshot") == "true") {
            val file = File(InstrumentationRegistry.getInstrumentation().targetContext.cacheDir, "chassis-compare-ui.png")
            file.outputStream().use {
                check(compose.onNodeWithTag("chassis-compare-dialog").captureToImage()
                    .asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, it))
            }
        }
        compose.onNodeWithTag("chassis-compare-close").performClick()
        compose.onNodeWithTag("chassis-compare-dialog").assertDoesNotExist()
        assertEquals("70000", maximum.fetchSemanticsNode().config[SemanticsProperties.EditableText].text)
        compose.runOnIdle {
            model.onPageExited()
            model.onPageEntered()
            assertEquals(70000, model.uiState.value.brakeChartMaxN)
        }
        compose.waitUntil(5_000) { service.registeredCommandCount == 1 }
        compare.performClick()
        compose.runOnIdle {
            service.receiveFrame(comparisonFrame.copyOf().apply { this[204] = (this[204].toInt() xor 1).toByte() })
        }
        compose.onNodeWithTag("chassis-compare-status")
            .assertTextEquals("Invalid comparison response. Close and reopen to request again.")
        compose.onNodeWithTag("chassis-compare-close").performClick()
        compare.performClick()
        compose.waitUntil(7_000) {
            model.uiState.value.comparisonResult == ChassisComparisonResult.Failed(ChassisComparisonError.Timeout)
        }
        compose.onNodeWithTag("chassis-compare-status")
            .assertTextEquals("Comparison timed out. Close and reopen to request again.")
        compose.onNodeWithTag("chassis-compare-close").performClick()
        compare.performClick()
        compose.runOnIdle {
            model.onCompareClosed()
            model.onCompareOpened()
        }
        compose.waitForIdle()
        compose.runOnIdle {
            assertEquals(2, service.registeredCommandCount)
            service.receiveFrame(comparisonFrame)
        }
        compose.waitForIdle()
        compose.onNodeWithTag("chassis-compare-status").assertDoesNotExist()
        compose.onNodeWithTag("chassis-compare-close").performClick()
        compare.performClick()
        androidx.test.espresso.Espresso.pressBack()
        compose.onNodeWithTag("chassis-compare-dialog").assertDoesNotExist()
        offsetSwitch().assertIsOff()
        compose.runOnIdle {
            assertEquals(1, service.registeredCommandCount)
            service.receiveFrame(comparisonFrame)
            assertNull(model.uiState.value.comparisonResult)
            assertFalse(model.uiState.value.comparisonOpen)
        }
        compare.performClick()
        compose.runOnIdle { model.onPageExited() }
        compose.waitUntil(5_000) { service.registeredCommandCount == 0 }
        compose.onNodeWithTag("chassis-compare-dialog").assertDoesNotExist()
        compose.runOnIdle {
            assertEquals(0, service.registeredCommandCount)
            assertEquals(7, service.writes.size)
            service.writes.forEach { assertArrayEquals(requestFrame, it) }
        }
    }

    @Test
    fun combinedInputAutomaticallyShowsOnlyItsRecordedBrakeResponse() {
        showScreen()
        val maximum = compose.onNodeWithTag("chassis-brake-chart-max-input")
        maximum.performTextReplacement("50000")
        maximum.performImeAction()
        mode("BrakePedal").performClick()
        val combined = compose.onNodeWithTag("chassis-combined-brakes-input")
        combined.performTextReplacement("8000")
        compose.onNodeWithTag("chassis-brake-response-dialog").assertDoesNotExist()
        compose.runOnIdle { assertTrue(service.writes.isEmpty()) }
        combined.performImeAction()
        compose.onNodeWithTag("chassis-brake-response-dialog").assertDoesNotExist()
        compose.runOnIdle {
            service.receiveReport(CMDChassisReport.Response(0, 0, 25000, 30000, rampRaw = 0, voltageRaw = 480))
            model.onBrakeChartMaxSubmitted("60000")
        }
        compose.waitForIdle()
        compose.runOnIdle {
            service.receiveReport(CMDChassisReport.Response(0, 0, 35000, 40000, rampRaw = 0, voltageRaw = 480))
        }
        compose.waitUntil(4_000) { model.uiState.value.brakeResponse != null }
        compose.onNodeWithTag("chassis-brake-response-dialog").assertIsDisplayed()
        compose.onNodeWithText("EMB / EHB Response").assertIsDisplayed()
        compose.onNode(hasText("EMB") and hasAnyAncestor(hasTestTag("chassis-brake-response-dialog")))
            .assertIsDisplayed()
        compose.onNode(hasText("EHB") and hasAnyAncestor(hasTestTag("chassis-brake-response-dialog")))
            .assertIsDisplayed()
        val chart = compose.onNodeWithTag("chassis-brake-response-chart")
            .assertIsDisplayed().assertTextEquals("0 500 1000 1500 2000 ms")
        compose.onNodeWithTag("chassis-brake-response-status").assertDoesNotExist()
        val pixels = chart.captureToImage().toPixelMap()
        for (color in listOf(Color(0xFF45D6FF), Color(0xFFFFBC5C))) {
            assertTrue("Forces below the chosen maximum must remain visible above the control range",
                (0 until pixels.height).any { y ->
                    (0 until pixels.width).any { x -> pixels[x, y].toArgb() == color.toArgb() }
                })
        }
        val responseBeforeScale = model.uiState.value.brakeResponse
        compose.runOnIdle { model.onBrakeChartMaxSubmitted("20000") }
        for (color in listOf(Color(0xFF45D6FF), Color(0xFFFFBC5C))) {
            assertTrue("Recorded forces above Y Max must be clipped without automatic expansion",
                curvePixels(chart, color).isEmpty())
        }
        compose.runOnIdle {
            model.onBrakeChartMaxSubmitted("50000")
            assertSame(responseBeforeScale, model.uiState.value.brakeResponse)
            assertEquals(1, service.writes.size)
        }
        for (color in listOf(Color(0xFF45D6FF), Color(0xFFFFBC5C))) {
            assertTrue("Changing the scale must redraw the same recorded data", curvePixels(chart, color).isNotEmpty())
        }
        if (InstrumentationRegistry.getArguments().getString("chassisScreenshot") == "true") {
            val file = File(InstrumentationRegistry.getInstrumentation().targetContext.cacheDir, "chassis-brake-response-ui.png")
            file.outputStream().use {
                check(compose.onNodeWithTag("chassis-brake-response-dialog").captureToImage()
                    .asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, it))
            }
        }
        compose.runOnIdle {
            val response = model.uiState.value.brakeResponse!!
            assertEquals(listOf(25000f, 35000f), response.samples.map { it.ehbForceN })
            assertEquals(listOf(30000f, 40000f), response.samples.map { it.embForceN })
            assertEquals(1, service.writes.size)
            assertEquals(0x3D, service.writes.single()[3].toInt())
            assertEquals(1, service.registeredCommandCount)
            service.receiveReport(CMDChassisReport.Response(0, 0, 1, 2, rampRaw = 0, voltageRaw = 480))
        }
        compose.waitForIdle()
        compose.runOnIdle { assertEquals(2, model.uiState.value.brakeResponse!!.samples.size) }
        compose.onNodeWithTag("chassis-brake-response-close")
            .assertContentDescriptionEquals("Close response").performClick()
        compose.onNodeWithTag("chassis-brake-response-dialog").assertDoesNotExist()
        compose.runOnIdle { assertEquals(1, service.writes.size) }
        compose.onNodeWithTag("chassis-compare-button").performClick()
        compose.onNodeWithTag("chassis-compare-dialog").assertIsDisplayed()
        compose.runOnIdle {
            assertEquals(2, service.writes.size)
            assertEquals(0x40, service.writes.last()[3].toInt())
        }
        compose.onNodeWithTag("chassis-compare-close").performClick()
    }

    @Test
    fun emptyResponseWindowIsExplicitAndWaitsForAnOpenComparisonToClose() {
        showScreen(withInitialReport = false)
        mode("BrakePedal").performClick()
        val combined = compose.onNodeWithTag("chassis-combined-brakes-input")
        combined.performTextReplacement("8000")
        combined.performImeAction()
        compose.onNodeWithTag("chassis-compare-button").performClick()
        compose.waitUntil(4_000) { model.uiState.value.brakeResponse != null }
        compose.onNodeWithTag("chassis-compare-dialog").assertIsDisplayed()
        compose.onNodeWithTag("chassis-brake-response-dialog").assertDoesNotExist()
        compose.onNodeWithTag("chassis-compare-close").performClick()
        compose.onNodeWithTag("chassis-brake-response-status")
            .assertIsDisplayed().assertTextEquals("No reports received in this 2-second window.")
        compose.onNodeWithTag("chassis-brake-response-chart")
            .assertTextEquals("0 500 1000 1500 2000 ms")
        androidx.test.espresso.Espresso.pressBackUnconditionally()
        compose.onNodeWithTag("chassis-brake-response-dialog").assertDoesNotExist()
        compose.runOnIdle {
            assertNull(model.uiState.value.brakeResponse)
            assertEquals(2, service.writes.size)
            assertEquals(1, service.registeredCommandCount)
        }
    }

    private fun mode(name: String) = compose.onNodeWithTag("chassis-control-$name")
    private fun slider(name: String) = compose.onNodeWithTag("chassis-$name-slider")
    private fun offsetSwitch() = compose.onNodeWithTag("chassis-current-offset")

    private fun curvePixels(chart: SemanticsNodeInteraction, color: Color): List<Pair<Int, Int>> {
        val pixels = chart.captureToImage().toPixelMap()
        return buildList {
            for (y in 0 until pixels.height) for (x in 0 until pixels.width) {
                if (pixels[x, y].toArgb() == color.toArgb()) add(x to y)
            }
        }
    }

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
        compose.waitUntil(5_000) { service.registeredCommandCount == 1 }
        compose.runOnIdle {
            assertEquals(1, service.registeredCommandCount)
            if (withInitialReport) service.receiveReport(CMDChassisReport.Response(12, 1234, 6800, 18600, rampRaw = 1234, voltageRaw = 4836))
        }
        if (withInitialReport) {
            compose.waitUntil(5_000) { model.telemetryState.value.telemetry != null }
        }
        compose.waitForIdle()
    }
}

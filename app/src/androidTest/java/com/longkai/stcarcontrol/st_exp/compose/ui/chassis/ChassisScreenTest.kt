package com.longkai.stcarcontrol.st_exp.compose.ui.chassis

import android.graphics.Bitmap
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.ChassisTelemetry
import com.longkai.stcarcontrol.st_exp.compose.ui.theme.STCarTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.File

class ChassisScreenTest {
    @get:Rule
    val compose = createComposeRule()

    private val feedback = ChassisTelemetry(
        timestampMillis = 30_000,
        speedKph = 68f,
        steeringAngleDegrees = 12.4f,
        ehbPressureMpa = 6.8f,
        embValue = 18.6f
    )

    @Test
    fun englishSingleSignalCardsAndControlsArePresent() {
        showScreen()

        compose.onNodeWithText("Tire / Motor Angle").assertIsDisplayed()
        compose.onNodeWithTag("chassis-angle-reading").assertTextEquals("+12.4")
        compose.onNodeWithTag("chassis-angle-chart").assertIsDisplayed()
        compose.onNodeWithTag("chassis-ehb-chart").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("chassis-emb-chart").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("chassis-current-offset").assertIsDisplayed()
        compose.onNodeWithTag("chassis-control-Epb").performScrollTo().assertIsDisplayed()
        if (InstrumentationRegistry.getArguments().getString("chassisScreenshot") == "true") {
            val file = File(InstrumentationRegistry.getInstrumentation().targetContext.cacheDir, "chassis-ui.png")
            file.outputStream().use {
                check(compose.onRoot().captureToImage().asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, it))
            }
        }
    }

    @Test
    fun controlTargetsDoNotOverwriteMeasuredFeedback() {
        val state = mutableStateOf(ChassisUiState(telemetry = feedback))
        showScreen(state)

        compose.onNodeWithTag("chassis-angle-slider").performSemanticsAction(SemanticsActions.SetProgress) { it(-3f) }
        compose.runOnIdle { assertEquals(-3, state.value.controls.steeringStep) }
        compose.onNodeWithTag("chassis-angle-reading").assertTextEquals("+12.4")

        compose.onNodeWithTag("chassis-ehb-slider").performScrollTo()
            .performSemanticsAction(SemanticsActions.SetProgress) { it(.75f) }
        compose.runOnIdle { assertEquals(.75f, state.value.controls.ehbLevel, .001f) }
        compose.onNodeWithTag("chassis-ehb-reading").assertTextEquals("6.8")

        compose.onNodeWithTag("chassis-current-offset").performClick().assertIsOn()
        compose.onNodeWithTag("chassis-control-Epb").performScrollTo().performClick().assertIsSelected()
    }

    @Test
    fun bipolarDragCommitsOnRelease() {
        val state = mutableStateOf(ChassisUiState(telemetry = feedback))
        var commits = 0
        showScreen(state) { commits++ }

        compose.onNodeWithTag("chassis-speed-slider").performTouchInput {
            swipe(center, Offset(width * .1f, center.y), 200)
        }
        compose.runOnIdle {
            assertTrue(state.value.controls.speedStep < 0)
            assertEquals(1, commits)
            assertEquals(68f, state.value.telemetry!!.speedKph)
        }
    }

    @Test
    fun chartsRemainReadableInReducedViewport() {
        showScreen(modifier = Modifier.height(560.dp))

        listOf("angle", "ehb", "emb").forEach {
            compose.onNodeWithTag("chassis-$it-chart").performScrollTo()
                .assertIsDisplayed().assertHeightIsAtLeast(70.dp)
        }
        compose.onNodeWithTag("chassis-current-offset").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("chassis-control-Epb").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun slidersExposeSignedStepsAndPositiveBrakeLevels() {
        val state = mutableStateOf(ChassisUiState(telemetry = feedback))
        showScreen(state)

        listOf("speed", "angle").forEach { name ->
            val slider = compose.onNodeWithTag("chassis-$name-slider")
            val info = slider.fetchSemanticsNode().config[SemanticsProperties.ProgressBarRangeInfo]
            assertEquals(-5f..5f, info.range)
            assertEquals(9, info.steps)
            for (value in listOf(-5f, 5f, 0f)) {
                slider.performSemanticsAction(SemanticsActions.SetProgress) { it(value) }
                compose.runOnIdle {
                    val actual = if (name == "speed") state.value.controls.speedStep else state.value.controls.steeringStep
                    assertEquals(value.toInt(), actual)
                }
            }
        }
        listOf("ehb", "emb").forEach { name ->
            val slider = compose.onNodeWithTag("chassis-$name-slider").performScrollTo()
            assertEquals(0f..1f, slider.fetchSemanticsNode().config[SemanticsProperties.ProgressBarRangeInfo].range)
            for (value in listOf(1f, 0f)) {
                slider.performSemanticsAction(SemanticsActions.SetProgress) { it(value) }
                compose.runOnIdle {
                    val actual = if (name == "ehb") state.value.controls.ehbLevel else state.value.controls.embLevel
                    assertEquals(value, actual, 0f)
                }
            }
        }
    }

    private fun showScreen(
        state: androidx.compose.runtime.MutableState<ChassisUiState> = mutableStateOf(
            ChassisUiState(
                telemetry = feedback,
                history = (0..300).map { feedback.copy(timestampMillis = it * 100L) }
            )
        ),
        modifier: Modifier = Modifier,
        onCommit: () -> Unit = {}
    ) {
        compose.setContent {
            STCarTheme {
                ChassisScreen(
                    state = state.value,
                    onSpeedStepChanged = { state.value = state.value.copy(controls = state.value.controls.copy(speedStep = it)) },
                    onSteeringStepChanged = { state.value = state.value.copy(controls = state.value.controls.copy(steeringStep = it)) },
                    onEhbLevelChanged = { state.value = state.value.copy(controls = state.value.controls.copy(ehbLevel = it)) },
                    onEmbLevelChanged = { state.value = state.value.copy(controls = state.value.controls.copy(embLevel = it)) },
                    onControlsCommitted = onCommit,
                    onCurrentOffsetChanged = { state.value = state.value.copy(controls = state.value.controls.copy(currentOffsetEnabled = it)) },
                    onControlTabSelected = { state.value = state.value.copy(selectedControl = it) },
                    onDismissError = { state.value = state.value.copy(error = null) },
                    modifier = modifier
                )
            }
        }
    }
}

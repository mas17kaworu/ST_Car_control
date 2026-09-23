package com.longkai.stcarcontrol.st_exp.compose.ui.chassis

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.ChassisTelemetry
import com.longkai.stcarcontrol.st_exp.compose.ui.theme.STCarTheme
import kotlin.math.sin

@Preview(name = "Chassis tablet", widthDp = 1280, heightDp = 700)
@Composable
private fun ChassisPreview() {
    val history = (0..150).map { index ->
        val wave = sin(index * .08).toFloat()
        ChassisTelemetry(
            timestampMillis = index * 200L,
            speedKph = 68f,
            steeringAngleDegrees = wave * 18f,
            ehbPressureMpa = 5f + wave * 3f,
            embValue = 15f + wave * 8f
        )
    }
    STCarTheme {
        ChassisScreen(
            state = ChassisUiState(telemetry = history.last(), history = history),
            onSpeedStepChanged = {},
            onSteeringStepChanged = {},
            onEhbLevelChanged = {},
            onEmbLevelChanged = {},
            onControlsCommitted = {},
            onCurrentOffsetChanged = {},
            onControlTabSelected = {},
            onDismissError = {}
        )
    }
}

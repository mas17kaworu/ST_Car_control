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
            speedKph = 12f,
            steeringAngleDegrees = wave * 360f,
            ehbForceN = 10_000f + wave * 8_000f,
            embForceN = 10_000f + wave * 8_000f
        )
    }
    STCarTheme {
        ChassisScreen(
            state = ChassisUiState(telemetry = history.last(), history = history),
            onControlChanged = { _, _, _ -> },
            onControlCommitted = { _, _ -> },
            onControlTabSelected = {},
            onEpbToggled = {},
            onCurrentOffsetChanged = {},
            onEmergencyStopToggled = {},
            onDismissError = {}
        )
    }
}

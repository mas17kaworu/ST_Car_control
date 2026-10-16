package com.longkai.stcarcontrol.st_exp.compose.ui.chassis

import com.longkai.stcarcontrol.st_exp.compose.data.chassis.ChassisControlConfig
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.ChassisControlState
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.ChassisControlTab
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.ChassisControlField
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.ChassisError
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.ChassisTelemetry
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.ChassisComparisonResult
import com.longkai.stcarcontrol.st_exp.compose.ui.chassis.components.ChartPoint
import java.util.Collections

data class ChassisBrakeResponse(
    val startedAtMillis: Long,
    val samples: List<ChassisTelemetry> = emptyList(),
)

data class ChassisUiState(
    val controls: ChassisControlState = ChassisControlState(),
    val config: ChassisControlConfig = ChassisControlConfig(),
    val enabledControls: Set<ChassisControlTab> = emptySet(),
    val controlGenerations: Map<ChassisControlTab, Long> = emptyMap(),
    /** Local targets; the report protocol does not include switch feedback. */
    val epbEnabled: Boolean = false,
    val currentOffsetEnabled: Boolean = false,
    val emergencyStopEnabled: Boolean = false,
    val isDemo: Boolean = true,
    val error: ChassisError? = null,
    val comparisonOpen: Boolean = false,
    val comparisonResult: ChassisComparisonResult? = null,
    val brakeResponse: ChassisBrakeResponse? = null,
) {
    fun canControl(field: ChassisControlField): Boolean = field.mode in enabledControls

    fun generation(field: ChassisControlField): Long = controlGenerations[field.mode] ?: 0L
}

data class ChassisTelemetryState(
    val telemetry: ChassisTelemetry? = null,
    val history: List<ChassisTelemetry> = emptyList(),
    val steeringPoints: List<ChartPoint> = emptyList(),
    val ehbPoints: List<ChartPoint> = emptyList(),
    val embPoints: List<ChartPoint> = emptyList(),
) {
    companion object {
        fun fromHistory(samples: List<ChassisTelemetry>): ChassisTelemetryState {
            val history = Collections.unmodifiableList(samples.toList())
            val latest = history.lastOrNull()
            val brakeStart = (latest?.timestampMillis ?: 0L) - ChassisViewModel.BRAKE_WINDOW_SECONDS * 1_000L
            val firstBrakePoint = history.indexOfLast { it.timestampMillis < brakeStart }.coerceAtLeast(0)
            val braking = history.subList(firstBrakePoint, history.size)
            return ChassisTelemetryState(
                telemetry = latest,
                history = history,
                steeringPoints = Collections.unmodifiableList(history.map {
                    ChartPoint(it.timestampMillis, it.steeringAngleDegrees)
                }),
                ehbPoints = Collections.unmodifiableList(braking.map { ChartPoint(it.timestampMillis, it.ehbForceN) }),
                embPoints = Collections.unmodifiableList(braking.map { ChartPoint(it.timestampMillis, it.embForceN) }),
            )
        }
    }
}

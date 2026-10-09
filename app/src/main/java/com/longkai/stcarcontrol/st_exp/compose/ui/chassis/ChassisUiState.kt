package com.longkai.stcarcontrol.st_exp.compose.ui.chassis

import com.longkai.stcarcontrol.st_exp.compose.data.chassis.ChassisControlConfig
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.ChassisControlState
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.ChassisControlTab
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.ChassisControlField
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.ChassisError
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.ChassisTelemetry

data class ChassisUiState(
    val controls: ChassisControlState = ChassisControlState(),
    val config: ChassisControlConfig = ChassisControlConfig(),
    val telemetry: ChassisTelemetry? = null,
    val history: List<ChassisTelemetry> = emptyList(),
    val enabledControls: Set<ChassisControlTab> = emptySet(),
    val controlGenerations: Map<ChassisControlTab, Long> = emptyMap(),
    /** Local targets; the report protocol does not include switch feedback. */
    val epbEnabled: Boolean = false,
    val currentOffsetEnabled: Boolean = false,
    val emergencyStopEnabled: Boolean = false,
    val isDemo: Boolean = true,
    val error: ChassisError? = null,
) {
    fun canControl(field: ChassisControlField): Boolean = field.mode in enabledControls

    fun generation(field: ChassisControlField): Long = controlGenerations[field.mode] ?: 0L
}

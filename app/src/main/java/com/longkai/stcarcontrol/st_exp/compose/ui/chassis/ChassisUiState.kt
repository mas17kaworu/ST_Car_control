package com.longkai.stcarcontrol.st_exp.compose.ui.chassis

import com.longkai.stcarcontrol.st_exp.compose.data.chassis.ChassisControlConfig
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.ChassisControlState
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.ChassisControlTab
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.ChassisError
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.ChassisTelemetry

data class ChassisUiState(
    val controls: ChassisControlState = ChassisControlState(),
    val config: ChassisControlConfig = ChassisControlConfig(),
    val telemetry: ChassisTelemetry? = null,
    val history: List<ChassisTelemetry> = emptyList(),
    val selectedControl: ChassisControlTab = ChassisControlTab.Vehicle,
    val isDemo: Boolean = true,
    val error: ChassisError? = null,
)

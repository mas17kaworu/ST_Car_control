package com.longkai.stcarcontrol.st_exp.compose.data.chassis

import kotlinx.coroutines.flow.Flow

/**
 * Main-safe adapter boundary for a future UDP/Bluetooth implementation.
 * Collecting telemetry must never submit controls. Collection cancellation must release resources.
 */
interface ChassisDeviceDataSource {
    val config: ChassisControlConfig
    val isDemo: Boolean
    /** Expected read failures use [ChassisTelemetryException]; cancellation must propagate. */
    val telemetry: Flow<ChassisTelemetry>

    /** Expected write failures return [ChassisCommandResult.Rejected], never a false acknowledgment. */
    suspend fun submitControls(controls: ChassisControlState): ChassisCommandResult
}

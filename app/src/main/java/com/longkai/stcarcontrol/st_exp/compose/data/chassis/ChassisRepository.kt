package com.longkai.stcarcontrol.st_exp.compose.data.chassis

import kotlinx.coroutines.flow.Flow

interface ChassisRepository {
    val config: ChassisControlConfig
    val isDemo: Boolean

    /** Cold, main-safe observations, independent of desired controls and command submission. */
    val telemetry: Flow<ChassisTelemetry>

    /** Submit an explicit target only; success must not be treated as measured feedback. */
    suspend fun submitControls(controls: ChassisControlState): ChassisCommandResult
}

class DefaultChassisRepository(
    private val device: ChassisDeviceDataSource,
) : ChassisRepository {
    override val config: ChassisControlConfig get() = device.config
    override val isDemo: Boolean get() = device.isDemo
    override val telemetry: Flow<ChassisTelemetry> get() = device.telemetry

    override suspend fun submitControls(controls: ChassisControlState): ChassisCommandResult {
        if (!config.isValid(controls)) {
            return ChassisCommandResult.Rejected(ChassisError.InvalidControl)
        }
        return device.submitControls(controls)
    }
}

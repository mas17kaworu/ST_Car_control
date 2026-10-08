package com.longkai.stcarcontrol.st_exp.compose.data.chassis

import android.util.Log
import com.longkai.stcarcontrol.st_exp.Utils.ByteUtils.bytes2hex
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDChassisList.CMDChassisControl
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDChassisList.CMDChassisEmergencyStop
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDChassisList.CMDChassisReport
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow

interface ChassisRepository {
    val config: ChassisControlConfig
    val isDemo: Boolean

    /** Cold, main-safe observations, independent of desired controls and command submission. */
    val telemetry: Flow<ChassisTelemetry>

    /** Submit an explicit target only; success must not be treated as measured feedback. */
    suspend fun submitControl(control: ChassisControl): ChassisCommandResult
    suspend fun setEpb(enabled: Boolean): ChassisCommandResult
    suspend fun setCurrentOffset(enabled: Boolean): ChassisCommandResult
    suspend fun setEmergencyStop(enabled: Boolean): ChassisCommandResult
}

class DefaultChassisRepository(
    private val service: ServiceManager = ServiceManager.getInstance(),
    override val config: ChassisControlConfig = ChassisControlConfig(),
    private val clockMillis: () -> Long = { System.nanoTime() / 1_000_000L },
) : ChassisRepository {
    override val isDemo = false

    override val telemetry: Flow<ChassisTelemetry> = callbackFlow {
        if (service.messageDispatcher == null) {
            Log.w("ChassisRepository", "Cannot register chassis reports: service is not initialized")
            close()
            return@callbackFlow
        }
        val command = CMDChassisReport()
        val listener = object : CommandListenerAdapter<CMDChassisReport.Response>() {
            override fun onSuccess(response: CMDChassisReport.Response) {
                val sample = ChassisTelemetry(
                    clockMillis(), response.speedRaw.toFloat(),
                    response.steeringRaw.toFloat() / ChassisControlConfig.STEERING_RAW_PER_DEGREE,
                    response.ehbForceRaw.toFloat(), response.embForceRaw.toFloat(),
                )
                val result = trySend(sample)
                if (result.isFailure && !result.isClosed) {
                    Log.w("ChassisRepository", "Chassis report buffer is full; report dropped")
                }
            }

            override fun onTimeout() {
                // The legacy dispatcher routes parse failures to onTimeout.
                Log.w("ChassisRepository", "Chassis report timed out or could not be parsed")
            }

            override fun onError(errorCode: Int) {
                Log.w("ChassisRepository", "Chassis report error: $errorCode")
            }
        }
        service.registerRegularlyCommand(command, listener)
        awaitClose { service.unregisterRegularlyCommand(command) }
    }.buffer(300)

    override suspend fun submitControl(control: ChassisControl): ChassisCommandResult {
        if (!config.isValid(control)) {
            return ChassisCommandResult.Rejected(ChassisError.InvalidControl)
        }
        val command = when (control.field) {
            ChassisControlField.Speed -> CMDChassisControl.setSpeed(control.value)
            ChassisControlField.Steering ->
                CMDChassisControl.setSteering(control.value * ChassisControlConfig.STEERING_RAW_PER_DEGREE)
            ChassisControlField.Ehb -> CMDChassisControl.setEhbForce(control.value.toLong())
            ChassisControlField.Emb -> CMDChassisControl.setEmbForce(control.value.toLong())
        }
        return sendCommand(command, "field=${control.field}, value=${control.value}")
    }

    override suspend fun setEpb(enabled: Boolean): ChassisCommandResult =
        sendCommand(CMDChassisControl.setEpb(enabled), "field=Epb, enabled=$enabled")

    override suspend fun setCurrentOffset(enabled: Boolean): ChassisCommandResult =
        sendCommand(CMDChassisControl.setCurrentOffset(enabled), "field=CurrentOffset, enabled=$enabled")

    override suspend fun setEmergencyStop(enabled: Boolean): ChassisCommandResult =
        sendCommand(CMDChassisEmergencyStop(enabled), "field=EmergencyStop, enabled=$enabled")

    private fun sendCommand(command: BaseCommand, description: String): ChassisCommandResult {
        if (service.messageDispatcher == null) {
            return ChassisCommandResult.Rejected(ChassisError.NotReady)
        }
        Log.i(
            "ChassisRepository",
            "Submit chassis: $description, frame=${bytes2hex(command.toRawData())}"
        )
        service.sendCommandToCar(command, null)
        return ChassisCommandResult.Submitted
    }
}

package com.longkai.stcarcontrol.st_exp.compose.data.chassis

import android.util.Log
import com.longkai.stcarcontrol.st_exp.Utils.ByteUtils.bytes2hex
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDChassisList.CMDChassisCompare
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDChassisList.CMDChassisControl
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDChassisList.CMDChassisEmergencyStop
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDChassisList.CMDChassisReport
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull

interface ChassisRepository {
    val config: ChassisControlConfig
    val isDemo: Boolean
    val brakeTimes: ChassisBrakeTimes

    /** Cold, main-safe observations, independent of desired controls and command submission. */
    val telemetry: Flow<ChassisTelemetry>

    /** Submit an explicit target only; success must not be treated as measured feedback. */
    suspend fun submitControl(control: ChassisControl): ChassisCommandResult
    suspend fun submitCombinedBrakes(forceN: Int): ChassisCommandResult
    suspend fun submitBrakeTimes(times: ChassisBrakeTimes): ChassisCommandResult
    suspend fun requestComparison(): ChassisComparisonResult
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
    override var brakeTimes = ChassisBrakeTimes()
        private set

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
                    rampPercent = response.rampRaw / 100f,
                    voltageV = response.voltageRaw / 10f,
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
        return sendControl(command, "field=${control.field}, value=${control.value}")
    }

    override suspend fun submitCombinedBrakes(forceN: Int): ChassisCommandResult {
        if (forceN !in config.combinedBrakeForceN) {
            return ChassisCommandResult.Rejected(ChassisError.InvalidControl)
        }
        return sendControl(CMDChassisControl.setCombinedBrakeForce(forceN.toLong()), "field=Ehb+Emb, value=$forceN")
    }

    override suspend fun submitBrakeTimes(times: ChassisBrakeTimes): ChassisCommandResult {
        if (times.ehbSeconds !in ChassisBrakeTimes.RANGE || times.embSeconds !in ChassisBrakeTimes.RANGE) {
            return ChassisCommandResult.Rejected(ChassisError.InvalidControl)
        }
        val result = sendCommand(
            CMDChassisControl.setBrakeTimes(times.ehbSeconds, times.embSeconds),
            "field=BrakeTimes, ehbSeconds=${times.ehbSeconds}, embSeconds=${times.embSeconds}"
        )
        if (result == ChassisCommandResult.Submitted) brakeTimes = times
        return result
    }

    override suspend fun requestComparison(): ChassisComparisonResult {
        if (service.messageDispatcher == null) {
            Log.w("ChassisRepository", "Cannot request chassis comparison: service is not initialized")
            return ChassisComparisonResult.Failed(ChassisComparisonError.NotReady)
        }
        // A regular telemetry subscription can prevent the legacy dispatcher's timeout scan.
        val result = withTimeoutOrNull(5_000L) {
            callbackFlow<ChassisComparisonResult> {
                val command = CMDChassisCompare()
                val listener = object : CommandListenerAdapter<CMDChassisCompare.Response>(Int.MAX_VALUE) {
                    override fun onSuccess(response: CMDChassisCompare.Response) {
                        trySend(ChassisComparisonResult.Success(
                            ChassisComparison(response.withoutOffsetN, response.withOffsetN)
                        ))
                        close()
                    }

                    override fun onTimeout() {
                        Log.w("ChassisRepository", "Invalid chassis comparison response")
                        trySend(ChassisComparisonResult.Failed(ChassisComparisonError.InvalidResponse))
                        close()
                    }

                    override fun onError(errorCode: Int) {
                        Log.w("ChassisRepository", "Chassis comparison error: $errorCode")
                        trySend(ChassisComparisonResult.Failed(ChassisComparisonError.CommandFailed))
                        close()
                    }
                }
                service.registerCommandOnce(command, listener)
                if (sendCommand(command, "field=EmbComparison") is ChassisCommandResult.Rejected) {
                    trySend(ChassisComparisonResult.Failed(ChassisComparisonError.NotReady))
                    close()
                }
                awaitClose {
                    // This existing API also removes one-shot registrations.
                    service.unregisterRegularlyCommand(command)
                }
            }.first()
        }
        if (result != null) return result
        Log.w("ChassisRepository", "Chassis comparison response timed out")
        return ChassisComparisonResult.Failed(ChassisComparisonError.Timeout)
    }

    override suspend fun setEpb(enabled: Boolean): ChassisCommandResult =
        sendControl(CMDChassisControl.setEpb(enabled), "field=Epb, enabled=$enabled")

    override suspend fun setCurrentOffset(enabled: Boolean): ChassisCommandResult =
        sendControl(CMDChassisControl.setCurrentOffset(enabled), "field=CurrentOffset, enabled=$enabled")

    override suspend fun setEmergencyStop(enabled: Boolean): ChassisCommandResult =
        sendCommand(CMDChassisEmergencyStop(enabled), "field=EmergencyStop, enabled=$enabled")

    private fun sendControl(command: CMDChassisControl, description: String): ChassisCommandResult =
        // Without validity bits, every 0x3D frame must retain both submitted times.
        sendCommand(command.withBrakeTimes(brakeTimes.ehbSeconds, brakeTimes.embSeconds), description)

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

package com.longkai.stcarcontrol.st_exp.compose.data.chassis.fake

import com.longkai.stcarcontrol.st_exp.compose.data.chassis.ChassisCommandResult
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.ChassisControlConfig
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.ChassisControlState
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.ChassisDeviceDataSource
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.ChassisError
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.ChassisTelemetry
import kotlin.math.exp
import kotlin.math.sin
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

/**
 * Isolated preview simulation. It never accesses a hardware service or a global debug flag.
 * All scaling and ripple below are illustrative, not a vehicle model or protocol conversion.
 */
class FakeChassisDeviceDataSource(
    override val config: ChassisControlConfig = ChassisControlConfig(),
    private val clockMillis: () -> Long = { System.nanoTime() / 1_000_000L },
    private val samplePeriodMillis: Long = 100L,
    private val waitForNextSample: suspend (Long) -> Unit = { delay(it) },
) : ChassisDeviceDataSource {
    init {
        require(samplePeriodMillis > 0L)
    }

    override val isDemo: Boolean = true
    private val previewTarget = MutableStateFlow(ChassisControlState())

    override val telemetry: Flow<ChassisTelemetry> = flow {
        var previousTimestamp = clockMillis()
        val startedAt = previousTimestamp
        var speed = 0f
        var angle = 0f
        var pressure = 0f
        var emb = 0f
        while (true) {
            currentCoroutineContext().ensureActive()
            val timestamp = clockMillis().coerceAtLeast(previousTimestamp)
            val elapsedSeconds = (timestamp - previousTimestamp) / 1_000.0
            val smoothing = (1.0 - exp(-elapsedSeconds / 0.6)).toFloat()
            val ripple = sin((timestamp - startedAt) / 1_000.0).toFloat()
            val target = previewTarget.value
            speed += (target.speedStep * 8f - speed) * smoothing
            angle += (target.steeringStep * 5f - angle) * smoothing
            pressure += (target.ehbLevel * 8f - pressure) * smoothing
            emb += (target.embLevel * 24f - emb) * smoothing
            emit(
                ChassisTelemetry(
                    timestampMillis = timestamp,
                    speedKph = speed * (1f + ripple * 0.015f),
                    steeringAngleDegrees = angle * (1f + ripple * 0.01f),
                    ehbPressureMpa = (pressure * (1f + ripple * 0.02f)).coerceAtLeast(0f),
                    embValue = (emb * (1f + ripple * 0.02f)).coerceAtLeast(0f),
                )
            )
            previousTimestamp = timestamp
            waitForNextSample(samplePeriodMillis)
        }
    }

    override suspend fun submitControls(controls: ChassisControlState): ChassisCommandResult {
        currentCoroutineContext().ensureActive()
        if (!config.isValid(controls)) {
            return ChassisCommandResult.Rejected(ChassisError.InvalidControl)
        }
        // The offset flag is retained as preview intent, without inventing a calibration effect.
        previewTarget.value = controls
        return ChassisCommandResult.PreviewOnly
    }
}

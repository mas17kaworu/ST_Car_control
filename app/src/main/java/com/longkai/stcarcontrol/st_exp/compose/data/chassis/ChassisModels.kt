package com.longkai.stcarcontrol.st_exp.compose.data.chassis

data class ChassisTelemetry(
    /** Monotonic sample time, not wall-clock time or a frame counter. */
    val timestampMillis: Long,
    val speedKph: Float,
    /** The single combined Tire/Motor angle signal. */
    val steeringAngleDegrees: Float,
    val ehbPressureMpa: Float,
    /** A single EMB signal; no current-to-force conversion is assumed. */
    val embValue: Float,
)

/** Desired controls, not acknowledged or measured device state. */
data class ChassisControlState(
    val speedStep: Int = 0,
    val steeringStep: Int = 0,
    /** DEMO normalized level; real protocol limits have not been established. */
    val ehbLevel: Float = 0f,
    /** DEMO normalized level, with no implied current/force unit conversion. */
    val embLevel: Float = 0f,
    val currentOffsetEnabled: Boolean = false,
)

/** DEMO ranges only. These are not vehicle or protocol specifications. */
data class ChassisControlConfig(
    val speedMinStep: Int = -5,
    val speedMaxStep: Int = 5,
    val steeringMinStep: Int = -5,
    val steeringMaxStep: Int = 5,
) {
    init {
        require(speedMinStep < 0 && speedMaxStep > 0)
        require(steeringMinStep < 0 && steeringMaxStep > 0)
    }

    fun isValid(controls: ChassisControlState): Boolean =
        controls.speedStep in speedMinStep..speedMaxStep &&
            controls.steeringStep in steeringMinStep..steeringMaxStep &&
            controls.ehbLevel.isFinite() && controls.ehbLevel in 0f..1f &&
            controls.embLevel.isFinite() && controls.embLevel in 0f..1f
}

/** Selects only the visible control deck; it has no device/EPB command semantics. */
enum class ChassisControlTab {
    Vehicle,
    Steering,
    BrakePedal,
    Epb,
}

enum class ChassisError {
    InvalidControl,
    NotReady,
    CommandFailed,
}

sealed class ChassisCommandResult {
    /** Only the isolated simulator received this target. Nothing was sent to hardware. */
    object PreviewOnly : ChassisCommandResult()

    /** A future adapter accepted the write; this is not a device acknowledgment. */
    object Submitted : ChassisCommandResult()

    data class Rejected(val error: ChassisError) : ChassisCommandResult()
}

/** Device adapters translate expected connection/read failures to this type. */
class ChassisTelemetryException(
    val error: ChassisError,
    cause: Throwable? = null,
) : Exception(error.name, cause)

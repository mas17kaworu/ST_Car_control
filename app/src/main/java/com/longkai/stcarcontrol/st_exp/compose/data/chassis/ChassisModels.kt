package com.longkai.stcarcontrol.st_exp.compose.data.chassis

data class ChassisTelemetry(
    val timestampMillis: Long,
    val speedKph: Float,
    val steeringAngleDegrees: Float,
    val ehbForceN: Float,
    val embForceN: Float,
)

/** Targets in physical units, never substituted for measured feedback. */
data class ChassisControlState(
    val speedKph: Int = 0,
    val steeringAngleDegrees: Int = 0,
    val ehbForceN: Int = 0,
    val embForceN: Int = 0,
) {
    fun value(field: ChassisControlField): Int = when (field) {
        ChassisControlField.Speed -> speedKph
        ChassisControlField.Steering -> steeringAngleDegrees
        ChassisControlField.Ehb -> ehbForceN
        ChassisControlField.Emb -> embForceN
    }

    fun withValue(field: ChassisControlField, value: Int): ChassisControlState = when (field) {
        ChassisControlField.Speed -> copy(speedKph = value)
        ChassisControlField.Steering -> copy(steeringAngleDegrees = value)
        ChassisControlField.Ehb -> copy(ehbForceN = value)
        ChassisControlField.Emb -> copy(embForceN = value)
    }
}

/** Shared display and control limits; sliders advance in one-unit increments. */
data class ChassisControlConfig(
    val speedKph: IntRange = 0..20,
    val steeringDegrees: IntRange = -540..540,
    val ehbForceN: IntRange = 0..20_000,
    val embForceN: IntRange = 0..20_000,
) {
    init {
        require(speedKph.first == 0 && speedKph.last > 0)
        require(steeringDegrees.first < 0 && steeringDegrees.last > 0)
        require(steeringDegrees.first.toLong() * STEERING_RAW_PER_DEGREE >= Int.MIN_VALUE)
        require(steeringDegrees.last.toLong() * STEERING_RAW_PER_DEGREE <= Int.MAX_VALUE)
        require(ehbForceN.first == 0 && ehbForceN.last > 0)
        require(embForceN.first == 0 && embForceN.last > 0)
    }

    fun range(field: ChassisControlField): IntRange = when (field) {
        ChassisControlField.Speed -> speedKph
        ChassisControlField.Steering -> steeringDegrees
        ChassisControlField.Ehb -> ehbForceN
        ChassisControlField.Emb -> embForceN
    }

    fun isValid(control: ChassisControl): Boolean = control.value in range(control.field)

    companion object {
        const val STEERING_RAW_PER_DEGREE = 100
    }
}

enum class ChassisControlTab { Vehicle, Steering, BrakePedal, Epb }

enum class ChassisControlField(val mode: ChassisControlTab) {
    Speed(ChassisControlTab.Vehicle),
    Steering(ChassisControlTab.Steering),
    Ehb(ChassisControlTab.BrakePedal),
    Emb(ChassisControlTab.BrakePedal),
}

data class ChassisControl(val field: ChassisControlField, val value: Int)

enum class ChassisError { InvalidControl, NotReady, CommandFailed }

sealed class ChassisCommandResult {
    object PreviewOnly : ChassisCommandResult()
    /** Passed to the existing send API; neither transport completion nor device acknowledgment. */
    object Submitted : ChassisCommandResult()
    data class Rejected(val error: ChassisError) : ChassisCommandResult()
}

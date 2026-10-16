package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDChassisList

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse
import java.nio.ByteBuffer
import java.nio.ByteOrder

/** Factories select the valid fields using raw, unscaled protocol values. */
class CMDChassisControl private constructor(
    private val validFields: Int,
    private val speedRaw: Int = 0,
    private val steeringRaw: Int = 0,
    private val ehbForceRaw: Long = 0L,
    private val embForceRaw: Long = 0L,
    private val currentOffsetEnabled: Boolean = false,
    private val epbEnabled: Boolean = false,
    ehbTimeSeconds: Int = 0,
    embTimeSeconds: Int = 0,
) : BaseCommand() {
    init {
        require(ehbForceRaw in 0L..0xFFFF_FFFFL) { "EHB force must fit uint32" }
        require(embForceRaw in 0L..0xFFFF_FFFFL) { "EMB force must fit uint32" }
        require(ehbTimeSeconds in 0..255) { "EHB time must fit uint8 seconds" }
        require(embTimeSeconds in 0..255) { "EMB time must fit uint8 seconds" }

        dataLength = 0x1A
        data = ByteBuffer.allocate(dataLength).order(ByteOrder.LITTLE_ENDIAN)
            .put(dataLength.toByte())
            .put(COMMAND_CHASSIS_CONTROL)
            .putInt(validFields)
            .putInt(speedRaw)
            .putInt(steeringRaw)
            .putInt(ehbForceRaw.toInt())
            .putInt(embForceRaw.toInt())
            .put(if (currentOffsetEnabled) 0x55.toByte() else 0x00.toByte())
            .put(if (epbEnabled) 0x55.toByte() else 0x00.toByte())
            .put(ehbTimeSeconds.toByte())
            .put(embTimeSeconds.toByte())
            .array()
    }

    fun withBrakeTimes(ehbTimeSeconds: Int, embTimeSeconds: Int): CMDChassisControl =
        CMDChassisControl(
            validFields, speedRaw, steeringRaw, ehbForceRaw, embForceRaw,
            currentOffsetEnabled, epbEnabled, ehbTimeSeconds, embTimeSeconds
        )

    override fun getCommandId(): Byte = COMMAND_CHASSIS_CONTROL

    override fun toResponse(data: ByteArray?): BaseResponse =
        throw UnsupportedOperationException(
            "No chassis control acknowledgment is defined; subscribe to CMDChassisReport separately"
        )

    companion object {
        const val FIELD_SPEED = 1 shl 0
        const val FIELD_STEERING = 1 shl 1
        const val FIELD_EHB = 1 shl 2
        const val FIELD_EMB = 1 shl 3
        const val FIELD_CURRENT_OFFSET = 1 shl 4
        const val FIELD_EPB = 1 shl 5

        @JvmStatic
        fun setSpeed(speedRaw: Int): CMDChassisControl =
            CMDChassisControl(FIELD_SPEED, speedRaw = speedRaw)

        @JvmStatic
        fun setSteering(steeringRaw: Int): CMDChassisControl =
            CMDChassisControl(FIELD_STEERING, steeringRaw = steeringRaw)

        @JvmStatic
        fun setEhbForce(ehbForceRaw: Long): CMDChassisControl =
            CMDChassisControl(FIELD_EHB, ehbForceRaw = ehbForceRaw)

        @JvmStatic
        fun setEmbForce(embForceRaw: Long): CMDChassisControl =
            CMDChassisControl(FIELD_EMB, embForceRaw = embForceRaw)

        @JvmStatic
        fun setCombinedBrakeForce(forceRaw: Long): CMDChassisControl =
            CMDChassisControl(FIELD_EHB or FIELD_EMB, ehbForceRaw = forceRaw, embForceRaw = forceRaw)

        @JvmStatic
        fun setCurrentOffset(enabled: Boolean): CMDChassisControl =
            CMDChassisControl(FIELD_CURRENT_OFFSET, currentOffsetEnabled = enabled)

        @JvmStatic
        fun setEpb(enabled: Boolean): CMDChassisControl =
            CMDChassisControl(FIELD_EPB, epbEnabled = enabled)

        /** Time bytes are unconditional; no legacy control field is selected. */
        @JvmStatic
        fun setBrakeTimes(ehbTimeSeconds: Int, embTimeSeconds: Int): CMDChassisControl =
            CMDChassisControl(0, ehbTimeSeconds = ehbTimeSeconds, embTimeSeconds = embTimeSeconds)
    }
}

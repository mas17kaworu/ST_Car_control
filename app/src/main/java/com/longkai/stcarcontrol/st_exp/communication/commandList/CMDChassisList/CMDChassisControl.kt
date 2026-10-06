package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDChassisList

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse
import java.nio.ByteBuffer
import java.nio.ByteOrder

/** Each factory creates a single-field command using raw, unscaled protocol values. */
class CMDChassisControl private constructor(
    validFields: Int,
    speedRaw: Int = 0,
    steeringRaw: Int = 0,
    ehbForceRaw: Long = 0L,
    embForceRaw: Long = 0L,
    currentOffsetEnabled: Boolean = false,
    epbEnabled: Boolean = false,
) : BaseCommand() {
    init {
        require(ehbForceRaw in 0L..0xFFFF_FFFFL) { "EHB force must fit uint32" }
        require(embForceRaw in 0L..0xFFFF_FFFFL) { "EMB force must fit uint32" }

        dataLength = 0x18
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
            .array()
    }

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
        fun setCurrentOffset(enabled: Boolean): CMDChassisControl =
            CMDChassisControl(FIELD_CURRENT_OFFSET, currentOffsetEnabled = enabled)

        @JvmStatic
        fun setEpb(enabled: Boolean): CMDChassisControl =
            CMDChassisControl(FIELD_EPB, epbEnabled = enabled)
    }
}

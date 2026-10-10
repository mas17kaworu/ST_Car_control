package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDChassisList

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse
import com.longkai.stcarcontrol.st_exp.communication.utils.CheckSumBit
import com.longkai.stcarcontrol.st_exp.communication.utils.byteArrayToInt
import java.nio.ByteBuffer
import java.nio.ByteOrder

/** Receive-only telemetry, registered through ServiceManager.registerRegularlyCommand. */
class CMDChassisReport : BaseCommand() {
    override fun getCommandId(): Byte = COMMAND_CHASSIS_REPORT

    override fun toRawData(): ByteArray =
        throw UnsupportedOperationException("No chassis telemetry query frame is defined")

    override fun toResponse(data: ByteArray?): Response {
        requireNotNull(data) { "Chassis report must not be null" }
        // Bluetooth supplies a reusable buffer; only the declared frame belongs to this report.
        require(data.size >= FRAME_SIZE) { "Chassis report requires $FRAME_SIZE bytes" }
        require(data[0] == COMMAND_HEAD0 && data[1] == COMMAND_HEAD1) { "Invalid chassis report header" }
        require(data[2] == FRAME_LENGTH.toByte()) { "Invalid chassis report length" }
        require(data[3].toInt() and 0x7F == COMMAND_CHASSIS_REPORT.toInt()) {
            "Invalid chassis report command"
        }
        // Existing receive-frame checksum conventions include the header, unlike BaseCommand sends.
        require(data[FRAME_SIZE - 1] == CheckSumBit.checkSum(data, FRAME_SIZE - 1)) {
            "Invalid chassis report checksum"
        }

        return Response(
            speedRaw = byteArrayToInt(data, 4).toLong(),
            steeringRaw = byteArrayToInt(data, 8),
            ehbForceRaw = byteArrayToInt(data, 12).toLong() and 0xFFFF_FFFFL,
            embForceRaw = byteArrayToInt(data, 16).toLong() and 0xFFFF_FFFFL,
            rampRaw = (data[20].toInt() and 0xFF) or ((data[21].toInt() and 0xFF) shl 8),
        )
    }

    class Response(
        val speedRaw: Long,
        val steeringRaw: Int,
        val ehbForceRaw: Long,
        val embForceRaw: Long,
        val rampRaw: Int,
    ) : BaseResponse(COMMAND_CHASSIS_REPORT) {
        init {
            require(rampRaw in 0..0xFFFF) { "Ramp must fit uint16" }
        }

        override fun mockResponse(): ByteArray =
            ByteBuffer.allocate(FRAME_SIZE).order(ByteOrder.LITTLE_ENDIAN)
                .put(COMMAND_HEAD0).put(COMMAND_HEAD1)
                .put(FRAME_LENGTH.toByte()).put(COMMAND_CHASSIS_REPORT)
                .putInt(speedRaw.toInt()).putInt(steeringRaw)
                .putInt(ehbForceRaw.toInt()).putInt(embForceRaw.toInt())
                .putShort(rampRaw.toShort())
                .array().apply {
                    this[FRAME_SIZE - 1] = CheckSumBit.checkSum(this, FRAME_SIZE - 1)
                }
    }

    private companion object {
        const val FRAME_LENGTH = 0x14
        const val FRAME_SIZE = FRAME_LENGTH + 3
    }
}

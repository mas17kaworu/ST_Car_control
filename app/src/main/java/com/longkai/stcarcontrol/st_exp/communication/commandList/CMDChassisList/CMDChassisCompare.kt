package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDChassisList

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse
import com.longkai.stcarcontrol.st_exp.communication.utils.CheckSumBit
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.Collections

class CMDChassisCompare : BaseCommand() {
    init {
        dataLength = 2
        data = byteArrayOf(dataLength.toByte(), COMMAND_CHASSIS_COMPARE)
    }

    override fun getCommandId(): Byte = COMMAND_CHASSIS_COMPARE

    override fun toResponse(data: ByteArray?): Response {
        requireNotNull(data) { "Chassis comparison must not be null" }
        require(data.size == FRAME_SIZE) { "Chassis comparison requires exactly $FRAME_SIZE bytes" }
        require(data[0] == COMMAND_HEAD0 && data[1] == COMMAND_HEAD1) { "Invalid chassis comparison header" }
        require(data[2] == FRAME_LENGTH.toByte()) { "Invalid chassis comparison length" }
        require(data[3].toInt() and 0x7F == COMMAND_CHASSIS_COMPARE.toInt()) { "Invalid chassis comparison command" }
        require(data[FRAME_SIZE - 1] == CheckSumBit.checkSum(data, FRAME_SIZE - 1)) {
            "Invalid chassis comparison checksum"
        }
        val buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN)
        buffer.position(4)
        return Response(
            withoutOffsetN = List(SAMPLES_PER_SERIES) { buffer.short.toInt() and 0xFFFF },
            withOffsetN = List(SAMPLES_PER_SERIES) { buffer.short.toInt() and 0xFFFF }
        )
    }

    class Response(withoutOffsetN: List<Int>, withOffsetN: List<Int>) : BaseResponse(COMMAND_CHASSIS_COMPARE) {
        val withoutOffsetN: List<Int> = Collections.unmodifiableList(withoutOffsetN.toList())
        val withOffsetN: List<Int> = Collections.unmodifiableList(withOffsetN.toList())

        init {
            require(this.withoutOffsetN.size == SAMPLES_PER_SERIES && this.withOffsetN.size == SAMPLES_PER_SERIES) {
                "Chassis comparison requires 50 samples per series"
            }
            require(this.withoutOffsetN.all { it in 0..0xFFFF } && this.withOffsetN.all { it in 0..0xFFFF }) {
                "Chassis comparison forces must fit uint16"
            }
        }

        override fun mockResponse(): ByteArray {
            val buffer = ByteBuffer.allocate(FRAME_SIZE).order(ByteOrder.LITTLE_ENDIAN)
                .put(COMMAND_HEAD0).put(COMMAND_HEAD1)
                .put(FRAME_LENGTH.toByte()).put(COMMAND_CHASSIS_COMPARE)
            withoutOffsetN.forEach { buffer.putShort(it.toShort()) }
            withOffsetN.forEach { buffer.putShort(it.toShort()) }
            return buffer.array().apply {
                this[FRAME_SIZE - 1] = CheckSumBit.checkSum(this, FRAME_SIZE - 1)
            }
        }
    }

    private companion object {
        const val SAMPLES_PER_SERIES = 50
        const val FRAME_LENGTH = 2 + SAMPLES_PER_SERIES * 2 * 2
        const val FRAME_SIZE = FRAME_LENGTH + 3
    }
}

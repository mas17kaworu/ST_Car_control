package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDSound

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse

class CMDSoundsInfo(
    val frequency: Int,
    val amptitude: Int
) : BaseCommand() {

    init {
        data = ByteArray(7) { 0x00 }
        dataLength = 6
        data[0] = 6
        data[1] = COMMAND_SOUNDS_LED
        data[2] = amptitude.toByte()
        data[3] = frequency.toByte()
        data[4] = 0
        data[5] = 0
    }

    override fun toResponse(data: ByteArray): BaseResponse {
        return Response(
            commandId
        )
    }

    override fun getCommandId(): Byte {
        return COMMAND_SOUNDS_LED
    }

    class Response(commandId: Byte) : BaseResponse(commandId) {

        companion object {
        }

        override fun mockResponse(): ByteArray {
            val array = ByteArray(8)
            // array[0] = COMMAND_HEAD0
            // array[1] = COMMAND_HEAD1
            // array[2] = 0x05
            // array[3] = getCommandId().toByte()
            // array[4] = 0x02
            // array[5] = 0x00
            // array[6] = status
            // array[7] = CheckSumBit.checkSum(array, array.size - 1)
            return array
        }
    }

}
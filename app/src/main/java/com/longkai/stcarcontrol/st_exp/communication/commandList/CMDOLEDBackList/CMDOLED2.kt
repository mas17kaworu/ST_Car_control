package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDOLEDBackList

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse
import com.longkai.stcarcontrol.st_exp.communication.utils.CheckSumBit
import kotlin.experimental.and

class CMDOLED2() : BaseCommand() {

    override fun toResponse(data: ByteArray): BaseResponse {
        return Response(data[4])
    }

    override fun getCommandId(): Byte {
        return COMMAND_OLED2
    }

    class Response(val payload: Byte) : BaseResponse(COMMAND_OLED2) {
        override fun mockResponse(): ByteArray {
            val array = ByteArray(13)
            array[0] = COMMAND_HEAD0
            array[1] = COMMAND_HEAD1
            array[2] = 0x10
            array[3] = getCommandId().toByte()
            array[4] = payload
            array[5] = 0x00
            array[6] = 0x00
            array[7] = 0x00
            array[8] = 0x00
            array[9] = 0x00
            array[10] = 0x00
            array[11] = 0x00
            array[12] = CheckSumBit.checkSum(array, array.size - 1)
            return array
        }
    }

    companion object {
        val EMPTY_RESPONSE = Response(0x00)
    }
}
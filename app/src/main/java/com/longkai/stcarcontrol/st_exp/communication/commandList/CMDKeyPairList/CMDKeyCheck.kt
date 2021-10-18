package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDKeyPairList

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDKeyPairList.CMDKeyCheck.Response.Companion.STATUS_FOUND
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDKeyPairList.CMDKeyCheck.Response.Companion.STATUS_NOT_FOUND
import com.longkai.stcarcontrol.st_exp.communication.utils.CheckSumBit

class CMDKeyCheck() : BaseCommand() {

    override fun toResponse(data: ByteArray): BaseResponse {
        if (data[2] == 0x05.toByte() && data[6] == STATUS_FOUND) {
            return Response(commandId, STATUS_FOUND)
        } else {
            return Response(commandId, STATUS_NOT_FOUND)
        }
    }

    override fun getCommandId(): Byte {
        return COMMAND_KEY_CHECK
    }

    class Response(commandId: Byte, val status: Byte) : BaseResponse(commandId) {

        companion object {
            const val STATUS_NOT_FOUND : Byte = 0x00
            const val STATUS_FOUND : Byte = 0x01
        }

        constructor(status: Byte): this(COMMAND_KEY_CHECK, status)

        override fun mockResponse(): ByteArray {
            val array = ByteArray(8)
            array[0] = COMMAND_HEAD0
            array[1] = COMMAND_HEAD1
            array[2] = 0x05
            array[3] = getCommandId().toByte()
            array[4] = 0x02
            array[5] = 0x00
            array[6] = status
            array[7] = CheckSumBit.checkSum(array, array.size - 1)
            return array
        }
    }

}
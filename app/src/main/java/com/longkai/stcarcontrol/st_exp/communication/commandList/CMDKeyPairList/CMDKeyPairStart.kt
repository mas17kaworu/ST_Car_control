package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDKeyPairList

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDKeyPairList.CMDKeyPairStart.Response.Companion.STATUS_PAIR_FAILED
import com.longkai.stcarcontrol.st_exp.communication.utils.CheckSumBit

class CMDKeyPairStart(keys: List<Int>) : BaseCommand() {

    init {
        data = ByteArray(7) { 0x00 }
        dataLength = 7
        data[0] = 7
        data[1] = COMMAND_KEY_PAIR
        data[2] = CMD_TYPE_SEND
        data[3] = keys[0].shl(4).plus(keys[1]).toByte()
        data[4] = keys[2].shl(4).plus(keys[3]).toByte()
        data[5] = keys[4].shl(4).plus(keys[5]).toByte()
        data[6] = keys[6].shl(4).plus(keys[7]).toByte()
    }

    override fun toResponse(data: ByteArray): BaseResponse {
        if (data[2] == 0x04.toByte() && data[4] == CMD_TYPE_RECV) {
            return Response(commandId, data[5])
        } else {
            return Response(commandId, STATUS_PAIR_FAILED)
        }
    }

    override fun getCommandId(): Byte {
        return COMMAND_KEY_PAIR
    }

    class Response(commandId: Byte, val status: Byte) : BaseResponse(commandId) {

        companion object {
            const val STATUS_PAIR_IN_PROGRESS : Byte = 0x01
            const val STATUS_PAIR_SUCCESS : Byte = 0x02
            const val STATUS_PAIR_FAILED : Byte = 0x03
        }

        constructor(status: Byte): this(COMMAND_KEY_PAIR, status)

        override fun mockResponse(): ByteArray {
            val array = ByteArray(7)
            array[0] = COMMAND_HEAD0
            array[1] = COMMAND_HEAD1
            array[2] = 0x04
            array[3] = getCommandId().toByte()
            array[4] = CMD_TYPE_RECV
            array[5] = status
            array[6] = CheckSumBit.checkSum(array, array.size - 1)
            return array
        }
    }
}
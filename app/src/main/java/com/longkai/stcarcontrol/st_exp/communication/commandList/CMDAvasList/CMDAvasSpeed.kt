package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDAvasList

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse

class CMDAvasSpeed(speed: Int) : BaseCommand() {

    init {
        data = ByteArray(10) { 0x00 }
        dataLength = 10
        data[0] = 10
        data[1] = COMMAND_AVAS
        data[2+6] = 0xE7.toUByte().toByte()
        data[2+7] = 0xAB.toUByte().toByte()

        data[2+0] = speed.toByte()
        data[2+1] = speed.shr(8).toByte()

    }

    override fun toResponse(data: ByteArray?): BaseResponse {
        return Response(commandId)
    }

    override fun getCommandId(): Byte {
        return COMMAND_AVAS
    }

    class Response(commandId: Byte) : BaseResponse(commandId)

}
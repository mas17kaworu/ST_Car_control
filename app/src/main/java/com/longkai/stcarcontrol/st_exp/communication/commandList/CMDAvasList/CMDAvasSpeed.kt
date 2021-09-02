package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDAvasList

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse

class CMDAvasSpeed(speed: Int) : BaseCommand() {

    init {
        data = ByteArray(10) { 0x00 }
        dataLength = 8
        data[0] = 0x08
        data[1] = COMMAND_AVAS
        data[2] = 0xE7.toUByte().toByte()
        data[3] = 0xAB.toUByte().toByte()

        data[8] = speed.toByte()
        data[9] = speed.shr(8).toByte()

    }

    override fun toResponse(data: ByteArray?): BaseResponse {
        return Response(commandId)
    }

    override fun getCommandId(): Byte {
        return COMMAND_AVAS
    }

    class Response(commandId: Byte) : BaseResponse(commandId)

}
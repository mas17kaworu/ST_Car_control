package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDAvasList

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse

class CMDAvasSoundSwitch(mode: Mode, play: Boolean) : BaseCommand() {

    init {
        data = ByteArray(10) { 0x00 }
        dataLength = 8
        data[0] = 0x08
        data[1] = COMMAND_AVAS
        data[6] = when (mode) {
            Mode.Mode1 -> 0x04
            Mode.Mode2 -> 0x08
        }
        data[7] = when(play) {
            true -> 0x22
            false -> 0x11
        }
    }

    override fun toResponse(data: ByteArray?): BaseResponse {
        return Response(commandId)
    }

    override fun getCommandId(): Byte {
        return COMMAND_AVAS
    }

    class Response(commandId: Byte) : BaseResponse(commandId)

}
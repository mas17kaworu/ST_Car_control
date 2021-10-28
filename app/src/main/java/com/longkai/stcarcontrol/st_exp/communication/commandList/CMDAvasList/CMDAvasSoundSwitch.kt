package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDAvasList

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse

class CMDAvasSoundSwitch(mode: Mode, play: Boolean) : BaseCommand() {

    init {
        data = ByteArray(10) { 0x00 }
        dataLength = 10
        data[0] = 10
        data[1] = COMMAND_AVAS
        data[2+0] = when(play) {
            true -> 0x22
            false -> 0x11
        }
        data[2+1] = when (mode) {
            Mode.Mode1 -> 0x04
            Mode.Mode2 -> 0x08
            Mode.Mode3 -> 0x09
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
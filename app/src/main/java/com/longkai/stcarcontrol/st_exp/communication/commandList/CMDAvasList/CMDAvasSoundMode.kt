package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDAvasList

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDAvasList.CMDAvasSoundMode.Mode.Mode1
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDAvasList.CMDAvasSoundMode.Mode.Mode2

class CMDAvasSoundMode(mode: Mode) : BaseCommand() {

    init {
        data = byteArrayOf(8)
        data[1] = when (mode) {
            Mode1 -> 0x04
            Mode2 -> 0x08
            else -> 0x04
        }
    }

    override fun toResponse(data: ByteArray?): BaseResponse {
        return Response(commandId)
    }

    override fun getCommandId(): Byte {
        TODO("Not yet implemented")
    }

    class Response(commandId: Byte) : BaseResponse(commandId)

    enum class Mode {
        Mode1, Mode2
    }

    companion object {

    }
}
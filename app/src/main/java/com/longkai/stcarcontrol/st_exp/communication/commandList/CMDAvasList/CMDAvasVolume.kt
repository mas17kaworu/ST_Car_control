package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDAvasList

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse


class CMDAvasVolume(direction: AvasVolumeDirection, step: Int) : BaseCommand() {

    enum class AvasVolumeDirection {
        Up, Down
    }

    init {
        data = ByteArray(10) { 0x00 }
        dataLength = 10
        data[0] = 10
        data[1] = COMMAND_AVAS
        data[2+0] = step.toByte()
        data[2+1] = when (direction) {
            AvasVolumeDirection.Up -> 0x10
            AvasVolumeDirection.Down -> 0x20
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
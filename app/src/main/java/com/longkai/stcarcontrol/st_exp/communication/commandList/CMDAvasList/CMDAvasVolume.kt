package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDAvasList

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse


class CMDAvasVolume(direction: AvasVolumeDirection, step: Int) : BaseCommand() {

    enum class AvasVolumeDirection {
        Up, Down
    }

    init {
        data = ByteArray(10) { 0x00 }
        dataLength = 8
        data[0] = 0x08
        data[1] = COMMAND_AVAS
        data[6] = when (direction) {
            AvasVolumeDirection.Up -> 0x10
            AvasVolumeDirection.Down -> 0x20
        }
        data[7] = step.toByte()
    }

    override fun toResponse(data: ByteArray?): BaseResponse {
        return Response(commandId)
    }

    override fun getCommandId(): Byte {
        return COMMAND_AVAS
    }

    class Response(commandId: Byte) : BaseResponse(commandId)

}
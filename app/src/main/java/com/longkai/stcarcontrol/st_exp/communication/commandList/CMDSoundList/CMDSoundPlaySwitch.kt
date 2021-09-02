package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDSoundList

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse

class CMDSoundPlaySwitch(play: Boolean) : BaseCommand() {

    init {
        data = ByteArray(10) { 0x00 }
        dataLength = 8
        data[0] = 0x08
        data[1] = COMMAND_SOUND
        val value = when(play) {
            true -> 0x2
            false -> 0x1
        }
        data[6] = value.shl(6).toByte()
    }

    override fun toResponse(data: ByteArray?): BaseResponse {
        return Response(commandId)
    }

    override fun getCommandId(): Byte {
        return COMMAND_SOUND
    }

    class Response(commandId: Byte) : BaseResponse(commandId)

}
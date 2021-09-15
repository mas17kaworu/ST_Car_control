package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDSoundList

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse

class CMDSoundFieldSwitch(isOn: Boolean) : BaseCommand() {

    init {
        data = ByteArray(10) { 0x00 }
        dataLength = 8
        data[0] = 0x08
        data[1] = COMMAND_SOUND
        data[2+1] = when(isOn) {
            true -> 0x22
            false -> 0x11
        }
    }

    override fun toResponse(data: ByteArray?): BaseResponse {
        return Response(commandId)
    }

    override fun getCommandId(): Byte {
        return COMMAND_SOUND
    }

    class Response(commandId: Byte) : BaseResponse(commandId)

}
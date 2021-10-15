package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDSoundList

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse

/**
 * isOn: true - 全车; false - 前排
 */
class CMDSoundFieldSwitch(isOn: Boolean) : BaseCommand() {

    init {
        data = ByteArray(10) { 0x00 }
        dataLength = 10
        data[0] = 10
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
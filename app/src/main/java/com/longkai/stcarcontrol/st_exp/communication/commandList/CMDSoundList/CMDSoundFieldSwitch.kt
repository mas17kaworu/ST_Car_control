package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDSoundList

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse

enum class SoundField {
    Quality, Focus
}

class CMDSoundFieldSwitch(soundField: SoundField) : BaseCommand() {

    init {
        data = ByteArray(10) { 0x00 }
        dataLength = 10
        data[0] = 10
        data[1] = COMMAND_SOUND
        data[2+1] = when(soundField) {
            SoundField.Quality -> 0x11
            SoundField.Focus -> 0x22
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
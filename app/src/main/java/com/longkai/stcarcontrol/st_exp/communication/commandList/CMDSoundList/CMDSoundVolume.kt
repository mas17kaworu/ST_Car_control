package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDSoundList

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse

class CMDSoundVolume(direction: SoundVolumeDirection, step: Int) : BaseCommand() {

    enum class SoundVolumeDirection {
        Up, Down
    }

    init {
        data = ByteArray(10) { 0x00 }
        dataLength = 8
        data[0] = 0x08
        data[1] = COMMAND_SOUND
        val upBit = when(direction) {
            SoundVolumeDirection.Up -> 0x1
            SoundVolumeDirection.Down -> 0x2
        }
        data[6] = upBit.shl(4).plus(step).toByte()
    }

    override fun toResponse(data: ByteArray?): BaseResponse {
        return Response(commandId)
    }

    override fun getCommandId(): Byte {
        return COMMAND_SOUND
    }

    class Response(commandId: Byte) : BaseResponse(commandId)

}
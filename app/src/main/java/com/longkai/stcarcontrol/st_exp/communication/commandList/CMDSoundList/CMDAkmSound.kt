package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDSoundList

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse


class CMDAkmSound(
    soundEffect: SoundEffect? = null,
    soundField: SoundField? = null,
    immersionEffect: ImmersionEffect? = null,
    soundMode: SoundMode? = null,
    play: Boolean? = null,
    direction: CMDSoundVolume.SoundVolumeDirection? = null,
    step: Int? = null,
) : BaseCommand() {

    init {
        data = ByteArray(10) { 0x00 }
        dataLength = 10
        data[0] = 10
        data[1] = COMMAND_SOUND

        if (soundEffect != null) {
            data[2+0] = when(soundEffect) {
                SoundEffect.Cozy -> 0x11
                SoundEffect.Dynamic -> 0x22
            }
        }
        if (soundField != null) {
            data[2+1] = when(soundField) {
                SoundField.Quality -> 0x11
                SoundField.Focus -> 0x22
            }
        }
        if (immersionEffect != null) {
            data[2+2] = when(immersionEffect) {
                ImmersionEffect.Natural -> 0x11
                ImmersionEffect.Surround -> 0x22
            }
        }
        if (play != null) {
            val value = when(play) {
                true -> 0x2
                false -> 0x1
            }
            data[2+3] = value.shl(6).toByte()
        }
        if (direction != null && step != null) {
            val upBit = when(direction) {
                CMDSoundVolume.SoundVolumeDirection.Up -> 0x1
                CMDSoundVolume.SoundVolumeDirection.Down -> 0x2
            }
            data[2+3] = upBit.shl(4).plus(step).toByte()
        }
        if (soundMode != null) {
            data[2+4] = when(soundMode) {
                SoundMode.On -> 0x11
                SoundMode.Off -> 0x22
                null -> 0x00
            }
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
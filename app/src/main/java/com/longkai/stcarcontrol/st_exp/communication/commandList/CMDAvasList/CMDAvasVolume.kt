package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDAvasList

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse

class CMDAvasVolume : BaseCommand() {
    override fun toResponse(data: ByteArray?): BaseResponse {
        TODO("Not yet implemented")
    }

    override fun getCommandId(): Byte {
        TODO("Not yet implemented")
    }

    class Response(commandId: Byte) : BaseResponse(commandId)

}
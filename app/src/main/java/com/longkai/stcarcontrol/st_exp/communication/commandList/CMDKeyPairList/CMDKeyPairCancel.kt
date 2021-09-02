package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDKeyPairList

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse

class CMDKeyPairCancel() : BaseCommand() {

    init {
        data = ByteArray(7) { 0x00 }
        dataLength = 5
        data[0] = 0x05
        data[1] = COMMAND_KEY_PAIR
        data[2] = CMD_TYPE_CANCEL
    }

    override fun toResponse(data: ByteArray): BaseResponse {
        return Response(commandId)
    }

    override fun getCommandId(): Byte {
        return COMMAND_KEY_PAIR
    }

    class Response(commandId: Byte) : BaseResponse(commandId)

}
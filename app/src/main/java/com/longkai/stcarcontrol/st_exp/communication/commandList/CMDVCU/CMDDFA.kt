package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCU

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse


class CMDDFAResponse( command: Byte) : BaseResponse(command) {
    var crash: Boolean = false
    var dc_c: Boolean = false
    var ac_c: Boolean = false
    override fun mockResponse(): ByteArray {
        var byteArray = ByteArray(3)
        byteArray[2] = 0x03
        byteArray[3] = 0x37
        return byteArray
    }


}

class CMDDFA : BaseCommand() {
    private var commandId = COMMAND_XPDC2
    override fun toResponse(data: ByteArray?): BaseResponse {
        val response = CMDDFAResponse(commandId)
        data?.let {
            response.crash = data[4].toInt() and 0x01 != 0
            response.dc_c = data[4].toInt() and 0x02 != 0
            response.ac_c = data[4].toInt() and 0x04 != 0
        }
        return response
    }

    override fun getCommandId(): Byte {
        return commandId.toByte()
    }

    init {
        try {
            data = ByteArray(3)
            dataLength = 3
            data[0] = 0x03
            data[1] = COMMAND_XPDC2
            data[2] = 0x00
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun turnOff() {
        super.turnOff()
    }

    override fun turnOn() {
        super.turnOn()
    }


}


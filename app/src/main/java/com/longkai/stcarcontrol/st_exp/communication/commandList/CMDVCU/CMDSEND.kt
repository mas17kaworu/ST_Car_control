package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCU

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse
class CMDResponse(var data: ByteArray?):BaseResponse(){
    override fun mockResponse(): ByteArray {
        var byteArray = ByteArray(3)
        byteArray[0] = BaseCommand.COMMAND_HEAD0
        byteArray[1] = BaseCommand.COMMAND_HEAD1
        byteArray[2] = 0x03
        byteArray[3] = 0x37
        return byteArray
    }
}
class CMDSEND: BaseCommand() {
    var state = false
    private var commandId = 0x37
    override fun toResponse(data: ByteArray?): BaseResponse {
        return CMDResponse(data)
    }
    override fun getCommandId(): Byte {
        return commandId.toByte()
    }

    private fun changeState(){
        try {
            data = ByteArray(5)
            dataLength = 5
            data[0] = COMMAND_HEAD0
            data[1] = COMMAND_HEAD1
            data[2] = 0x03
            data[3] = 0x037
            if (state) {
                data[4] = 0x01
            } else {
                data[4] = 0x00
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun turnOff() {
        super.turnOff()
        state = false
        changeState()
    }

    override fun turnOn() {
        super.turnOn()
        state = true
        changeState()
    }


}


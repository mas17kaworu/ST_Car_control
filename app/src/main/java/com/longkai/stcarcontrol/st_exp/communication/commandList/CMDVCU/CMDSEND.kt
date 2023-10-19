package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCU

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse

object CMDSTATUS{
    var sPower:Boolean = false
    var sMotor:Boolean = false
}
class CMDResponse(var data: ByteArray?):BaseResponse(){
    override fun mockResponse(): ByteArray {
        var byteArray = ByteArray(3)
        byteArray[2] = 0x03
        byteArray[3] = 0x37
        return byteArray
    }
}
class CMDSEND(power: Boolean, motor: Boolean,commandIndex:Int) : BaseCommand() {
    private var commandId = 0x37
    override fun toResponse(data: ByteArray?): BaseResponse {
        return CMDResponse(data)
    }
    override fun getCommandId(): Byte {
        return commandId.toByte()
    }

    init {
        try {
            data = ByteArray(3)
            dataLength = 3
            data[0] = 0x03
            data[1] = if (commandIndex == 1) COMMAND_XPDC1 else COMMAND_XPDC2
            var powerDate = if(power) 0x01 else 0x00
            var motorDate = if(motor) 0x01 else 0x00
            data[2] = ((powerDate or (motorDate shl 1)).toByte());
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


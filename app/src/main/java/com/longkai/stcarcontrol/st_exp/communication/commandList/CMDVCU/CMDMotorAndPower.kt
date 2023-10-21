package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCU

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse

object CMDSTATUS{
    var sPower:Boolean = false
    var sMotor:Boolean = false
}
class CMDResponse(var command: Byte):BaseResponse(command){
    var success = false
    override fun mockResponse(): ByteArray {
        var byteArray = ByteArray(3)
        byteArray[2] = 0x03
        byteArray[3] = 0x37
        return byteArray
    }
}
class CMDMotorPower(power: Boolean, motor: Boolean) : BaseCommand() {
    private var commandId = COMMAND_XPDC1
    override fun toResponse(data: ByteArray?): BaseResponse {
        var response = CMDResponse(COMMAND_XPDC1)
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
            data[1] =  COMMAND_XPDC1
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


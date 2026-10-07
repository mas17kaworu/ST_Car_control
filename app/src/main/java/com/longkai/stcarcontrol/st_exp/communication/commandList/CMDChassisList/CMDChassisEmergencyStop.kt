package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDChassisList

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse
import java.nio.ByteBuffer
import java.nio.ByteOrder

class CMDChassisEmergencyStop(enabled: Boolean) : BaseCommand() {
    init {
        dataLength = 0x06
        data = ByteBuffer.allocate(dataLength).order(ByteOrder.LITTLE_ENDIAN)
            .put(dataLength.toByte())
            .put(COMMAND_CHASSIS_EMERGENCY_STOP)
            .putInt(if (enabled) 0x55 else 0x00)
            .array()
    }

    override fun getCommandId(): Byte = COMMAND_CHASSIS_EMERGENCY_STOP

    override fun toResponse(data: ByteArray?): BaseResponse =
        throw UnsupportedOperationException("No chassis emergency-stop acknowledgment is defined")
}

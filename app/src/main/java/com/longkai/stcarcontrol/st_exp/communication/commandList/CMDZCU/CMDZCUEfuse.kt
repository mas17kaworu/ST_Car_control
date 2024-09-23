package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDZCU

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse
import com.longkai.stcarcontrol.st_exp.communication.utils.CheckSumBit
import com.longkai.stcarcontrol.st_exp.communication.utils.byteArrayToInt

class CMDZCUEfuse: BaseCommand() {
    override fun toResponse(data: ByteArray?): Response {
        val response = data?.let {
            val current = byteArrayToInt( data, 4) / 100f
            val voltage = byteArrayToInt( data, 8) / 100f
            val tempDevice = byteArrayToInt( data, 12) / 1f
            val tempMos = byteArrayToInt( data, 16) / 1f
            Response(
                current = current,
                voltage = voltage,
                tempDevice = tempDevice,
                tempMos = tempMos,
            )
        } ?: Response(
            current = 0f,
            voltage = 0f,
            tempDevice = 0f,
            tempMos = 0f,
        )

        return response
    }

    override fun getCommandId(): Byte {
        return COMMAND_ZCU_EFUSE
    }

    class Response(
        val current: Float,
        val voltage:Float,
        val tempDevice: Float,
        val tempMos: Float
    ): BaseResponse(COMMAND_ZCU_EFUSE) {
        override fun mockResponse(): ByteArray {
            val array = ByteArray(21)
            array[0] = COMMAND_HEAD0
            array[1] = COMMAND_HEAD1
            array[2] = 0x10
            array[3] = getCommandId().toByte()
            val cBytes = intToBytes((current * 100).toInt())
            array[4] = cBytes[0]
            array[5] = cBytes[1]
            array[6] = cBytes[2]
            array[7] = cBytes[3]
            val vBytes = intToBytes((voltage * 100).toInt())
            array[8] = vBytes[0]
            array[9] = vBytes[1]
            array[10] = vBytes[2]
            array[11] = vBytes[3]
            val tDBytes = intToBytes(tempDevice.toInt())
            array[12] = tDBytes[0]
            array[13] = tDBytes[1]
            array[14] = tDBytes[2]
            array[15] = tDBytes[3]
            val tMBytes = intToBytes(tempMos.toInt())
            array[16] = tMBytes[0]
            array[17] = tMBytes[1]
            array[18] = tMBytes[2]
            array[19] = tMBytes[3]
            array[20] = CheckSumBit.checkSum(array, array.size - 1)
            return array
        }

        private fun intToBytes(value: Int): ByteArray {
            return ByteArray(4) {
                (value shr (it * 8) and 0xFF).toByte()
            }
        }
    }
}
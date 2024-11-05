package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDZCU

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDZCU.CMDZCU.LinkStatus
import com.longkai.stcarcontrol.st_exp.communication.utils.CheckSumBit
import com.longkai.stcarcontrol.st_exp.communication.utils.byteArrayToInt

class CMDZCUEfuse: BaseCommand() {
    override fun toResponse(data: ByteArray?): Response {
        val response = data?.let {
            var current: Float? = byteArrayToInt( data, 4) / 100f
            var voltage: Float? = byteArrayToInt( data, 8) / 100f
            var tempDevice: Float? = byteArrayToInt( data, 12) / 1f
            var tempMos: Float? = byteArrayToInt( data, 16) / 1f
            val loadStatus = data[20].toLinkStatus()

            val currentInvalid = data[4] == (0xff).toByte() && data[5] == (0xff).toByte() &&
                    data[6] == (0xff).toByte() && data[7] == (0xff).toByte()
            val voltageInvalid = data[8] == (0xff).toByte() && data[9] == (0xff).toByte() &&
                    data[10] == (0xff).toByte() && data[11] == (0xff).toByte()
            val tempDeviceInvalid = data[12] == (0xff).toByte() && data[13] == (0xff).toByte() &&
                    data[14] == (0xff).toByte() && data[15] == (0xff).toByte()
            val tempMosInvalid = data[16] == (0xff).toByte() && data[17] == (0xff).toByte() &&
                    data[18] == (0xff).toByte() && data[19] == (0xff).toByte()

            Response(
                current = if (!currentInvalid) current else null,
                voltage = if (!currentInvalid) voltage else null,
                tempDevice = if (!currentInvalid) tempDevice else null,
                tempMos = if (!currentInvalid) tempMos else null,
                loadStatus = loadStatus,
                currentInvalid = currentInvalid,
                voltageInvalid = voltageInvalid,
                tempDeviceInvalid = tempDeviceInvalid,
                tempMosInvalid = tempMosInvalid,
            )
        } ?: Response(
            current = 0f,
            voltage = 0f,
            tempDevice = 0f,
            tempMos = 0f,
            loadStatus = CMDZCU.LinkStatus.Invalid,
            currentInvalid = true,
            voltageInvalid = true,
            tempDeviceInvalid = true,
            tempMosInvalid = true,
        )

        return response
    }

    override fun getCommandId(): Byte {
        return COMMAND_ZCU_EFUSE
    }

    class Response(
        val current: Float?,
        val voltage: Float?,
        val tempDevice: Float?,
        val tempMos: Float?,
        val loadStatus: CMDZCU.LinkStatus,
        val currentInvalid: Boolean = false,
        val voltageInvalid: Boolean = false,
        val tempDeviceInvalid: Boolean = false,
        val tempMosInvalid: Boolean = false,
    ): BaseResponse(COMMAND_ZCU_EFUSE) {
        override fun mockResponse(): ByteArray {
            val array = ByteArray(22)
            array[0] = COMMAND_HEAD0
            array[1] = COMMAND_HEAD1
            array[2] = 0x14
            array[3] = getCommandId().toByte()
            val cBytes = intToBytes(((current ?: 0f) * 100).toInt())
            array[4] = cBytes[0]
            array[5] = cBytes[1]
            array[6] = cBytes[2]
            array[7] = cBytes[3]
            val vBytes = intToBytes(((voltage ?: 0f) * 100).toInt())
            array[8] = vBytes[0]
            array[9] = vBytes[1]
            array[10] = vBytes[2]
            array[11] = vBytes[3]
            val tDBytes = intToBytes((tempDevice ?: 0f).toInt())
            array[12] = tDBytes[0]
            array[13] = tDBytes[1]
            array[14] = tDBytes[2]
            array[15] = tDBytes[3]
            val tMBytes = intToBytes((tempMos ?: 0f).toInt())
            array[16] = tMBytes[0]
            array[17] = tMBytes[1]
            array[18] = tMBytes[2]
            array[19] = tMBytes[3]
            array[20] = when (loadStatus) {
                CMDZCU.LinkStatus.Fail -> 0x55.toByte()
                CMDZCU.LinkStatus.OK -> 0xAA.toByte()
                else -> 0x00.toByte()
            }
            if (current == -1f) {
                array[4] = (0xFF).toByte()
                array[5] = (0xFF).toByte()
                array[6] = (0xFF).toByte()
                array[7] = (0xFF).toByte()

                array[12] = (0xFF).toByte()
                array[13] = (0xFF).toByte()
                array[14] = (0xFF).toByte()
                array[15] = (0xFF).toByte()
            }

            array[21] = CheckSumBit.checkSum(array, array.size - 1)
            return array
        }

        private fun intToBytes(value: Int): ByteArray {
            return ByteArray(4) {
                (value shr (it * 8) and 0xFF).toByte()
            }
        }
    }
}

private fun Byte.toLinkStatus(): LinkStatus {
    return if (this == 0x55.toByte()) LinkStatus.OK
    else if (this == 0xAA.toByte()) LinkStatus.Fail
    else LinkStatus.Invalid
}

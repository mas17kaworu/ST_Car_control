package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPBox

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDKeyPairList.CMD_TYPE_SEND
import com.longkai.stcarcontrol.st_exp.communication.utils.CheckSumBit

class CMDPBox : BaseCommand() {

    init {
        dataLength = 2
        data = ByteArray(dataLength) { 0x00 }
        data[0] = dataLength.toByte()
        data[1] = COMMAND_PBOX
    }

    enum class DataType(val uartNumber: Int) {
        Real(0), PBox(1), Reserved1(2), Reserved2(3);

        companion object {
            fun from(uartNumber: Int): DataType {
                return DataType.values().first { it.uartNumber == uartNumber }
            }
        }
    }

    override fun toResponse(data: ByteArray): BaseResponse {
        val contentLength = data[2] - 2
        val uartNumber = data[4].toInt()
        val content = String(data.sliceArray(5 until(5 + contentLength)))
        return Response(DataType.from(uartNumber), content)
    }

    override fun getCommandId(): Byte = COMMAND_PBOX

    class Response(
        val dataType: DataType,
        val content: String
    ) : BaseResponse(COMMAND_PBOX) {

        override fun mockResponse(): ByteArray {
            val contentBytes = content.toByteArray()
            val array = ByteArray(5 + contentBytes.size + 1)
            array[0] = COMMAND_HEAD0
            array[1] = COMMAND_HEAD1
            array[2] = (contentBytes.size + 2).toByte()
            array[3] = getCommandId().toByte()
            array[4] = dataType.uartNumber.toByte()
            System.arraycopy(contentBytes, 0, array, 5, contentBytes.size)
            array[array.size - 1] = CheckSumBit.checkSum(array, array.size - 1)
            return array
        }
    }
}
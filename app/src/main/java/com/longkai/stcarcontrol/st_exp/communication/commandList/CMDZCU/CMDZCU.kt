package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDZCU

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse
import com.longkai.stcarcontrol.st_exp.communication.utils.CheckSumBit
import kotlin.experimental.and

class CMDZCU : BaseCommand() {
    override fun toResponse(data: ByteArray?): BaseResponse {
        return data?.let {
            Response(
                link1Status = data[4].toLinkStatus() ?: LinkStatus.Invalid,
                link2Status = data[5].toLinkStatus(),
                link3Status = data[6].toLinkStatus(),
                link4Status = data[7].toLinkStatus(),
            )
        } ?: Response(
            LinkStatus.Invalid,
            LinkStatus.Invalid,
            LinkStatus.Invalid,
            LinkStatus.Invalid,
        )
    }

    override fun getCommandId(): Byte {
        return COMMAND_ZCU
    }

    class Response(
        val link1Status: LinkStatus,
        val link2Status: LinkStatus,
        val link3Status: LinkStatus,
        val link4Status: LinkStatus,
    ) : BaseResponse(COMMAND_ZCU) {
        override fun mockResponse(): ByteArray {
            val array = ByteArray(13)
            array[0] = COMMAND_HEAD0
            array[1] = COMMAND_HEAD1
            array[2] = 0x08
            array[3] = getCommandId().toByte()
            array[4] = (if (link1Status == LinkStatus.OK) 0x55 else 0xAA).toByte()
            array[5] = (if (link2Status == LinkStatus.OK) 0x55 else 0xAA).toByte()
            array[6] = (if (link3Status == LinkStatus.OK) 0x55 else 0xAA).toByte()
            array[7] = (if (link4Status == LinkStatus.OK) 0x55 else 0xAA).toByte()
            array[12] = CheckSumBit.checkSum(array, array.size - 1)
            return array
        }
    }

    enum class LinkStatus {
        OK, Fail, Invalid
    }

    private fun Byte.toLinkStatus(): LinkStatus {
        return if (this.toInt() == 0x55) LinkStatus.OK
        else if (this.toInt() == 0xAA) LinkStatus.Fail
        else LinkStatus.Invalid
    }
}


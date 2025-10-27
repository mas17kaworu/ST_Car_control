package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDFangjia

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDZCU.CMDZCU.LinkStatus
import com.longkai.stcarcontrol.st_exp.communication.utils.CheckSumBit

class CMDFangjia: BaseCommand() {
  override fun toResponse(data: ByteArray?): BaseResponse? {
    val fangjiaStatus = (data?.get(4)?.toInt() ?: 0) and 0xFF
    return Response(
      status = fangjiaStatus,
    )
  }

  override fun getCommandId(): Byte {
    return COMMAND_FANGJIA
  }

  class Response(
    val status: Int = 0,
  ): BaseResponse(COMMAND_FANGJIA) {
    override fun mockResponse(): ByteArray {
      val array = ByteArray(6)
      array[0] = COMMAND_HEAD0
      array[1] = COMMAND_HEAD1
      array[2] = 0x06
      array[3] = getCommandId().toByte()
      array[4] = (status and 0xFF).toByte()
      array[5] = CheckSumBit.checkSum(array, array.size - 1)
      return array
    }
  }
}
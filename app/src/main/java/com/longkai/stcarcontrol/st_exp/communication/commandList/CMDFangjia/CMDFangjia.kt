package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDFangjia

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse

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

  }
}
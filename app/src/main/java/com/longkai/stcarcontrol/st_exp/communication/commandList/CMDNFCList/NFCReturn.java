package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDNFCList;

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;

public class NFCReturn extends BaseCommand {
  

  @Override public BaseResponse toResponse(byte[] data) throws Exception {
    return null;
  }

  @Override public byte getCommandId() {
    return 0;
  }
}

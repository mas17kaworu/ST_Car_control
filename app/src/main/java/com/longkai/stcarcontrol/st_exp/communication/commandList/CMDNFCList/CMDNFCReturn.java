package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDNFCList;

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;

public class CMDNFCReturn extends BaseCommand {

  public CMDNFCReturn() {
    try {
      data = new byte[2];
      dataLength = 2;
      data[0] = 0x02;
      data[1] = COMMAND_NFC_RETURN;
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override public BaseResponse toResponse(byte[] data) throws Exception {
    Response response = new Response(getCommandId());
    if (data[2] == 0x03) {
      response.key_info = (data[4] & 0x03);
      response.filter_info = (data[4] & 0x04);
    }
    return response;
  }

  @Override public byte getCommandId() {
    return COMMAND_NFC_RETURN;
  }

  public static class Response extends BaseResponse {

    /*description：
    1: B[1], B[0] combination:
    0x0	Key not available (Default)
		0x1	Valid key detected
		0x2	Invalid key detected
		0x3	Invalid data, do nothing

    2:  B[3] description
    0x00	Filter not available or invalid filter. (Default)
    0x01	Valid filter detected, 90% remain

    3: The data is sent from Gateway to PAD only, triggered by event.*/

    public Integer key_info = null;

    public Integer filter_info = null;

    public Response() {
      setCommandId(COMMAND_NFC_RETURN);
    }

    public Response(byte commandId) {
      super(commandId);
    }
  }
}

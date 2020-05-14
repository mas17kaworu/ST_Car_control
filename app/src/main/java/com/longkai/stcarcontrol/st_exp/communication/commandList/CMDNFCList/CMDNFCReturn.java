package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDNFCList;

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;
import com.longkai.stcarcontrol.st_exp.communication.utils.CheckSumBit;

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
      response.filter_info = (data[4] & 0x04) >> 2;
      response.door_info = (data[4] & 0x18) >> 3;
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

    3: B[4], B[3] combination:
      0x0	Invalid data,   (door not display) default
			0x1	NFC Lock Door
			0x2	NFC Unlock Door
			0x3	Invalid data,   (door not display)

    4: The data is sent from Gateway to PAD only, triggered by event.*/

    public Integer key_info = null;

    public Integer filter_info = null;

    public Integer door_info = null;

    public Response() {
      setCommandId(COMMAND_NFC_RETURN);
    }

    public Response(byte commandId) {
      super(commandId);
    }

    @Override public byte[] mockResponse() {
      byte[] array = new byte[6];
      array[0] = BaseCommand.COMMAND_HEAD0;
      array[1] = BaseCommand.COMMAND_HEAD1;
      array[2] = 0x03;
      array[3] = (byte)getCommandId();

      int tmpInt = (this.key_info);
      array[4] = (byte)(((tmpInt) & 0xff) | array[4]) ;

      tmpInt = (this.filter_info) << 2;
      array[4] = (byte)(((tmpInt) & 0xff) | array[4]);

      tmpInt = (this.door_info) << 3;
      array[4] = (byte)(((tmpInt) & 0xff) | array[4]);

      array[5] = CheckSumBit.checkSum(array, array.length - 1);
      return array;
    }

    @Override public boolean equals(Object o) {
      return this.key_info.equals(((Response) o).key_info) && this.door_info.equals(
          ((Response) o).door_info) && this.filter_info.equals(((Response) o).filter_info);
    }
  }
}

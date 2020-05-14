package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDOBCDemoList;

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;

public class CMDOBCReturn extends BaseCommand {

  public CMDOBCReturn() {
    try {
      data = new byte[2];
      dataLength = 2;
      data[0] = 0x02;
      data[1] = COMMAND_OBC_RETURN;
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * CANFD ID	0x1A8	comment
   * byte0	PFC_STATE(8bit)	Detail See slide 11
   * byte1	LLC_STATE(8bit)	Detail See slide 12
   * byte2	Vac_ADC_count[b15:b8]
   * byte3	Vac_ADC_count[b7:b0]
   * byte4	Vbus_ADC_count[b15:b8]
   * byte5	Vbus_ADC_count[b7:b0]
   * byte6	Vbat_ADC_count[b15:b8]
   * byte7	Vbat_ADC_count[b7:b0]
   * byte8	Ibat_ADC_count[b15:b8]
   * byte9	Ibat_ADC_count[b7:b0]
   * byte10 - byte15	--	Reserved
   *
   */
  @Override public BaseResponse toResponse(byte[] data) throws Exception {
    Response response = new Response(getCommandId());
    if (data[2] == 0x12) {
      response.PFCState = (data[4] & 0xff);
      response.LLCState = (data[5] & 0xff);
      response.Vac = (((data[6] & 0xff) << 8 | (data[7] & 0xff)) * 5.0f / 4096f - 2.612f) / 0.0053f;
      response.Vbus = (((data[8] & 0xff) << 8 | (data[9] & 0xff)) * 5.0f / 4096f) * 100.9559f + 0;
      response.Vbat = (((data[10] & 0xff) << 8 | (data[11] & 0xff)) * 5.0f / 4096f) * 104.7242f + 0;
      response.Ibat = (((data[12] & 0xff) << 8 | (data[13] & 0xff)) * 5.0f / 4096f) * 5.54631f -2.79922f;
    }
    return response;
  }

  @Override public byte getCommandId() {
    return COMMAND_OBC_RETURN;
  }

  public static class Response extends BaseResponse {
    public Integer PFCState = null;

    public Integer LLCState = null;

    public Float Vac;
    public Float Vbat;
    public Float Vbus;
    public Float Ibat;


    public Response() {
      setCommandId(COMMAND_OBC_RETURN);
    }

    public Response(byte commandId) {
      super(commandId);
    }

  }
}

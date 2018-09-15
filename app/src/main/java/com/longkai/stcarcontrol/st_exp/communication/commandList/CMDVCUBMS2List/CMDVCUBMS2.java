package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUBMS2List;

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;

/**
 * Created by Administrator on 2018/5/13.
 */

public class CMDVCUBMS2 extends BaseCommand {
    public CMDVCUBMS2(){
        try{
            data = new byte[2];
            dataLength = 2;
            data[0] = 0x02;
            data[1] = (byte) (0b101<<5 | COMMAND_VCU_BMS_2);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public BaseResponse toResponse(byte[] data) throws Exception {
        Response response = new Response(getCommandId());
        if (data[2] == 0x0E){
            response.U_HighVoltage_9 = (data[5] & 0xff)<<8 | (data[4] & 0xff);
            response.U_HighVoltage_8 = (data[7] & 0xff)<<8 | (data[6] & 0xff);
            response.U_HighVoltage_7 = (data[9] & 0xff)<<8 | (data[8] & 0xff);
            response.U_HighVoltage_6 = (data[11] & 0xff)<<8 | (data[10] & 0xff);
            response.U_HighVoltage_5 = (data[13] & 0xff)<<8 | (data[12] & 0xff);
            response.U_HighVoltage_4 = (data[15] & 0xff)<<8 | (data[14] & 0xff);
        }
        return response;
    }

    @Override
    public byte getCommandId() {
        return COMMAND_VCU_BMS_2;
    }

    public static class Response extends BaseResponse {
        public	int	U_HighVoltage_4	;
        public	int	U_HighVoltage_5	;
        public	int	U_HighVoltage_6	;
        public	int	U_HighVoltage_7	;
        public	int	U_HighVoltage_8	;
        public	int	U_HighVoltage_9	;
        public Response(byte commandId) {
            super(commandId);
        }
    }
}

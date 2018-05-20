package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUBMS1List;

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;

/**
 * Created by Administrator on 2018/5/13.
 */

public class CMDVCUBMS1 extends BaseCommand {
    public CMDVCUBMS1(){
        try{
            data = new byte[2];
            dataLength = 2;
            data[0] = 0x02;
            data[1] = (byte) (0b101<<5 | COMMAND_VCU_BMS_1);//10100000
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public BaseResponse toResponse(byte[] data) throws Exception {
        Response response = new Response(getCommandId());
        if (data[2] == 0x0C){
            response.R_Isolation_Minus = (data[5] & 0xff)<<8 | (data[4] & 0xff);
            response.R_Isolation_Plus = (data[7] & 0xff)<<8 | (data[6] & 0xff);
            response.U_HighVoltage_3 = (data[9] & 0xff)<<8 | (data[8] & 0xff);
            response.U_HighVoltage_2 = (data[11] & 0xff)<<8 | (data[10] & 0xff);
            response.U_HighVoltage_1 = (data[13] & 0xff)<<8 | (data[12] & 0xff);
        }
        return response;
    }

    @Override
    public byte getCommandId() {
        return COMMAND_VCU_BMS_1;
    }

    public static class Response extends BaseResponse {

        public  int	U_HighVoltage_1	;
        public	int	U_HighVoltage_2	;
        public	int	U_HighVoltage_3	;
        public	int	R_Isolation_Plus	;
        public	int	R_Isolation_Minus	;


        public Response(byte commandId) {
            super(commandId);
        }
    }
}

package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUBMS5List;

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;

/**
 * Created by Administrator on 2018/5/13.
 */

public class CMDVCUBMS5 extends BaseCommand {
    public CMDVCUBMS5(){
        try{
            data = new byte[2];
            dataLength = 2;
            data[0] = 0x02;
            data[1] = (byte) (0b101<<5 | COMMAND_VCU_BMS_5);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public BaseResponse toResponse(byte[] data) throws Exception {
        Response response = new Response(getCommandId());
        if (data[2] == 0x04){
            response.BMS_Fault_2 = (data[4] & 0x01);
            response.BMS_Fault_1 = (data[4] & 0x02)>>1;
            response.SOC = (data[5] & 0xff) * 0.392f;
        }
        return response;
    }

    @Override
    public byte getCommandId() {
        return COMMAND_VCU_BMS_5;
    }

    public static class Response extends BaseResponse {
        public	float	SOC	; // 0~100
        public	int	BMS_Fault_1; //Pack_Fault
        public	int	BMS_Fault_2; //Cell_Fualt

        public Response(byte commandId) {
            super(commandId);
        }
    }
}

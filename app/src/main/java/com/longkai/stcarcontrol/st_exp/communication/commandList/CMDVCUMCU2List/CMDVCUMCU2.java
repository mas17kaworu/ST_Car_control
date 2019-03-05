package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUMCU2List;

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;

/**
 * Created by Administrator on 2018/5/13.
 */

public class CMDVCUMCU2 extends BaseCommand {
    public CMDVCUMCU2(){
        try{
            data = new byte[2];
            dataLength = 2;
            data[0] = 0x02;
            data[1] = (byte) (0b000<<5 | COMMAND_VCU_MCU_2);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public BaseResponse toResponse(byte[] data) throws Exception {
        Response response = new Response(getCommandId());
        if (data[2] == 0x09){
            response.Status_of_DCDC = (data[4] & 0xff);
            response.Status_of_Motor = (data[5] & 0xff);
            response.Number_of_Motor = (data[6] & 0xff);
            response.MCU_Fault_x = (data[7] & 0xff);
        }
        return response;
    }

    @Override
    public byte getCommandId() {
        return COMMAND_VCU_MCU_2;
    }

    public static class Response extends BaseResponse {
        public	int	MCU_Fault_x	;
        public	int	Number_of_Motor	;
        public	int	Status_of_Motor	;
        public	int	Status_of_DCDC	;


        public Response(byte commandId) {
            super(commandId);
        }
    }
}

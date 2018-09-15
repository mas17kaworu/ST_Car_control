package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUBMS6List;

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;

/**
 * Created by Administrator on 2018/5/13.
 */

public class CMDVCUBMS6 extends BaseCommand {
    public CMDVCUBMS6(){
        try{
            data = new byte[2];
            dataLength = 2;
            data[0] = 0x02;
            data[1] = (byte) (0b101<<5 | COMMAND_VCU_BMS_6);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public BaseResponse toResponse(byte[] data) throws Exception {
        Response response = new Response(getCommandId());
        if (data[2] == 0x14){
            response.BMS_Fault_x_low = (long)(data[11] & 0xff)<<56 |(long)(data[10] & 0xff)<<48 |
                    (long)(data[9] & 0xff)<<40 |(long)(data[8] & 0xff)<<32 |
                    (long)(data[7] & 0xff)<<24 |(long)(data[6] & 0xff)<<16 |
                    (long)(data[5] & 0xff)<<8 |(long)(data[4] & 0xff);
            response.BMS_Fault_x_high = (long)(data[19] & 0xff)<<56 |(long)(data[18] & 0xff)<<48 |
                    (long)(data[17] & 0xff)<<40 |(long)(data[16] & 0xff)<<32 |
                    (long)(data[15] & 0xff)<<24 |(long)(data[14] & 0xff)<<16 |
                    (long)(data[13] & 0xff)<<8 |(long)(data[12] & 0xff);
        }
        return response;
    }

    @Override
    public byte getCommandId() {
        return COMMAND_VCU_BMS_6;
    }

    public static class Response extends BaseResponse {
        public	long	BMS_Fault_x_high;
        public	long	BMS_Fault_x_low;


        public Response(byte commandId) {
            super(commandId);
        }
    }
}

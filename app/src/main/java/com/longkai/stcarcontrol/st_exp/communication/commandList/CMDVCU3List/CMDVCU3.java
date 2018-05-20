package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCU3List;

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;

/**
 * Created by Administrator on 2018/5/13.
 */

public class CMDVCU3 extends BaseCommand {
    public CMDVCU3(){
        try{
            data = new byte[2];
            dataLength = 2;
            data[0] = 0x02;
            data[1] = (byte) (0b101<<5 | COMMAND_VCU3);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public BaseResponse toResponse(byte[] data) throws Exception {
        Response response = new Response(getCommandId());
        if (data[2] == 0x09){
            response.Speed_Car = (data[5] & 0xff)<<8 | (data[4] & 0xff);
            response.Shift_level = (data[6] & 0xff);
            response.Brake_Status = (data[8] & 0xff)<<8 | (data[7] & 0xff);
            response.Pedal_Status = (data[10] & 0xff)<<8 | (data[9] & 0xff);
        }
        return response;
    }

    @Override
    public byte getCommandId() {
        return COMMAND_VCU3;
    }

    public static class Response extends BaseResponse {

        public int Pedal_Status;
        public int Brake_Status;
        public int Shift_level;
        public int Speed_Car;

        public Response(byte commandId) {
            super(commandId);
        }
    }
}

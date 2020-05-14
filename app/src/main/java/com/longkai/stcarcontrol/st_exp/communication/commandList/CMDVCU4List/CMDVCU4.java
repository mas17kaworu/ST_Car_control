package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCU4List;

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;

/**
 * Created by Administrator on 2018/5/13.
 */

public class CMDVCU4 extends BaseCommand {
    public CMDVCU4(){
        try{
            data = new byte[2];
            dataLength = 2;
            data[0] = 0x02;
            data[1] = (byte) (0b000<<5 | COMMAND_VCU4);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public BaseResponse toResponse(byte[] data) throws Exception {
        Response response = new Response(getCommandId());
        if (data[2] == 0x06){
            response.Motor_Expected_Speed = (data[5] & 0xff)<<8 | (data[4] & 0xff);
            response.Motor_Expected_Torch = ((((data[7] & 0xff)<<8 | (data[6] & 0xff)) - 2000) * 0.1f) ;
        }
        return response;
    }

    @Override
    public byte getCommandId() {
        return COMMAND_VCU4;
    }

    public static class Response extends BaseResponse {

        public float Motor_Expected_Torch;
        public int Motor_Expected_Speed;

        public Response(byte commandId) {
            super(commandId);
        }
    }
}

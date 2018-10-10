package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCU7List;

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;

/**
 * Created by Administrator on 2018/5/13.
 */

public class CMDVCU7 extends BaseCommand {
    public CMDVCU7(){
        try{
            data = new byte[2];
            dataLength = 2;
            data[0] = 0x02;
            data[1] = (byte) (0b101<<5 | COMMAND_VCU7);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public BaseResponse toResponse(byte[] data) throws Exception {
        Response response = new Response(getCommandId());
        if (data[2] == 0x08){
            response.pedal_status = (data[4] & 0xff);
            response.break_status = (data[5] & 0xff);
            response.torch_expired = ((data[6] & 0xff)<<8 | (data[7] & 0xff) / 10);
            response.gaoya_jidianqi_status = (data[8] & 0xff);
            response.charging_status = (data[9] & 0xff);
        }
        return response;
    }

    @Override
    public byte getCommandId() {
        return COMMAND_VCU7;
    }

    public static class Response extends BaseResponse {

        public int pedal_status;
        public int break_status;
        public int torch_expired;
        public int gaoya_jidianqi_status; //In ppt, 继电器状态 (主正 主副。。。。）
        public int charging_status;

        public Response(byte commandId) {
            super(commandId);
        }
    }
}

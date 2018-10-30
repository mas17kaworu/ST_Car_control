package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDInfoteinment;

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDHVACList.CMDHVAC;

/**
 * Created by Administrator on 2018/9/23.
 */

public class CMDInfoteinment extends BaseCommand {

    protected static byte[] payload = {0x00,0x00,0x00,0x00};

    public CMDInfoteinment(){
        try{
            data = new byte[6];
            dataLength = 6;
            data[0] = 0x06;
            data[1] = (byte) (0b101<<5 | COMMAND_VCU_INFOTEINMENT);
            data[2] = payload[0];
            data[3] = payload[1];
            data[4] = payload[2];
            data[5] = payload[3];
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void refreshDataPayload(){
        data[2] = payload[0];
        data[3] = payload[1];
        data[4] = payload[2];
        data[5] = payload[3];
    }

    @Override
    public BaseResponse toResponse(byte[] data) throws Exception {
        return new Response(COMMAND_VCU_INFOTEINMENT);
    }

    @Override
    public byte getCommandId() {
        return COMMAND_VCU_INFOTEINMENT;
    }

    public static class Response extends BaseResponse {
        public Response(byte commandId) {
            super(commandId);
        }
    }
}

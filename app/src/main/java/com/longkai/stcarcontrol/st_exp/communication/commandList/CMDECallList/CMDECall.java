package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDECallList;

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;

/**
 * Created by Administrator on 2018/9/23.
 */

public class CMDECall extends BaseCommand {

    protected static byte[] payload = {0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};

    public CMDECall(){
        try{
            data = new byte[0x0A];
            dataLength = 10;
            data[0] = 0x0A;
            data[1] = (COMMAND_INFOTEINMENT_NEW);
            data[2] = payload[0];
            data[3] = payload[1];
            data[4] = payload[2];
            data[5] = payload[3];
            data[6] = payload[4];
            data[7] = payload[5];
            data[8] = payload[6];
            data[9] = payload[7];
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void refreshDataPayload(){
        data[2] = payload[0];
        data[3] = payload[1];
        data[4] = payload[2];
        data[5] = payload[3];
        data[6] = payload[4];
        data[7] = payload[5];
        data[8] = payload[6];
        data[9] = payload[7];
    }

    @Override
    public BaseResponse toResponse(byte[] data) throws Exception {
        return new Response(COMMAND_INFOTEINMENT_NEW);
    }

    @Override
    public byte getCommandId() {
        return COMMAND_INFOTEINMENT_NEW;
    }

    public static class Response extends BaseResponse {
        public Response(byte commandId) {
            super(commandId);
        }
    }
}

package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDHVACList;

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;

/**
 * Created by Administrator on 2017/8/29.
 */

public class CMDHVAC extends BaseCommand {


    protected static byte[] payload = {0x00,0x00};

    /**
     *
     * 空调 鼓风机
     *
     */
    public CMDHVAC(){
        try{
            data = new byte[4];
            dataLength = 4;
            data[0] = 0x04;
            data[1] = COMMAND_HAVC;
            data[2] = payload[0];
            data[3] = payload[1];
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public byte getCommandId() {
        return COMMAND_HAVC;
    }


    @Override
    public BaseResponse toResponse(byte[] data) throws Exception {
        return new Response(getCommandId());
    }

    public static class Response extends BaseResponse {
        public Response(byte commandId) {
            super(commandId);
        }
    }
}

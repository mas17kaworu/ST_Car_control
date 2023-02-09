package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDFrontC11LightList;

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;

/**
 *
 *
 * Created by Administrator on 2017/8/15.
 */

public class CMDFrontC11Light extends BaseCommand {
    protected static final byte Pattern8 = (byte)0x80;
    protected static final byte Pattern7 = (byte)0x40;
    protected static final byte Pattern6 = (byte)0x20;
    protected static final byte Pattern5 = (byte)0x10;
    protected static final byte Pattern4 = (byte)0x08;
    protected static final byte Pattern3 = (byte)0x04;
    protected static final byte Pattern2 = (byte)0x02;
    protected static final byte Pattern1 = (byte)0x01;

    protected static byte[] payload = new byte[256];

    public CMDFrontC11Light(){
        try{
            data = new byte[258];
            dataLength = 0xff;
            data[0] = (byte) 0xff;
            data[1] = COMMAND_C11_LIGHT;
            data[2] = payload[0];
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void refreshDataPayload(){
        for (int i = 0; i < payload.length; i++) {
            data[i+2] = payload[i];
        }
    }

    @Override
    public BaseResponse toResponse(byte[] data) throws Exception {
        Response response = new Response(getCommandId());
        return response;
    }

    @Override
    public byte getCommandId() {
        return COMMAND_C11_LIGHT;
    }


    public static class Response extends BaseResponse {
        public Response(byte commandId) {
            super(commandId);
        }
    }
}

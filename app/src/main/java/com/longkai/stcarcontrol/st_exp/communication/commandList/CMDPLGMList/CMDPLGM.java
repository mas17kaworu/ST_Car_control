package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPLGMList;

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;

/**
 * Created by Administrator on 2017/8/29.
 */

public class CMDPLGM extends BaseCommand {

    protected static final byte TrunkUp = (byte)0x02;
    protected static final byte TrunkDown = (byte)0x01;

    protected static byte[] payload = {0x00,0x00};

    public CMDPLGM(){
        try{
            data = new byte[4];
            dataLength = 4;
            data[0] = 0x04;
            data[1] = COMMAND_PLGM;
            data[2] = payload[0];
            data[3] = payload[1];
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public byte getCommandId() {
        return COMMAND_PLGM;
    }

    @Override
    public BaseResponse toResponse(byte[] data) throws Exception {
        Response response = new Response(getCommandId());
        response.setValue(data[3]);
        return response;
    }

    public static class Response extends BaseResponse {
        private int MotorStatus;
        private int AntiPinch;
        public Response(byte commandId) {
            super(commandId);
        }

        private void setValue(byte data){
            MotorStatus = (data&0x02) > 0 ? 1:0;
            AntiPinch = (data&0x01) > 0 ? 1:0;
        }

        public int getMotorStatus() {
            return MotorStatus;
        }

        public int getAntiPinch() {
            return AntiPinch;
        }
    }

}

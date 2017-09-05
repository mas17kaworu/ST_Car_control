package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDControlCenterList;

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;

/**
 * Created by Administrator on 2017/8/29.
 */

public class CMDControlCenter extends BaseCommand {

    protected static final byte DomeLight = (byte)0x80;
    protected static final byte FuelTankUnlock = (byte)0x40;
    protected static final byte SunshadeUp = (byte)0x20;
    protected static final byte SunshadeDown = (byte)0x10;
    protected static final byte Centrallock = (byte)0x08;
    protected static final byte CentralUnlock = (byte)0x04;
    protected static final byte WiperSlow = (byte)0x02;
    protected static final byte WiperFast = (byte)0x01;

    protected static final byte SafebeltSendout = (byte)0x08;
    protected static final byte SafebeltTakeback = (byte)0x04;
    protected static final byte TrunkCoverFold = (byte)0x02;
    protected static final byte TrunkCoberUnfold = (byte)0x01;

    protected static byte[] payload = {0x00,0x00};

    public CMDControlCenter(){
        try{
            data = new byte[4];
            dataLength = 4;
            data[0] = 0x04;
            data[1] = COMMAND_CENTRAL_CONTORL;
            data[2] = payload[0];
            data[3] = payload[1];
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void refreshDataPayload(){
        for (int i=0; i < payload.length; i++) {
            data[2+i] = payload[i];
        }
    }

    @Override
    public byte getCommandId() {
        return COMMAND_CENTRAL_CONTORL;
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

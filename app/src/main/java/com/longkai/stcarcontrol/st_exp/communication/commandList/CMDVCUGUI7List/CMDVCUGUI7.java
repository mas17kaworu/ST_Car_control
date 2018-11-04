package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUGUI7List;

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUGUIList.CMDVCUHVOff;

/**
 * Created by Administrator on 2018/10/30.
 */

public class CMDVCUGUI7 extends BaseCommand {
    //D0 D1 D2 D3 D4 OBCOn D5 OBCOff D6 DCDC
    protected static byte[] payload = {0x00,0x00,0x00,0x00,0x00,0x01,0x00,0x00};

    public CMDVCUGUI7(){
        try{
            data = new byte[10];
            dataLength = 10;
            data[0] = 0x0A;
            data[1] = (byte) (0b101<<5 | COMMAND_GUI7);
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
        Response response = new Response(getCommandId());
        return response;
    }

    public static class Response extends BaseResponse {
        public Response(byte commandId) {
            super(commandId);
        }
    }

    @Override
    public byte getCommandId() {
        return COMMAND_GUI7;
    }

    public byte[] getPayload(){
        return payload;
    }

    public void DCDCOn(){
        payload[6] = 1;
        refreshDataPayload();
    }

    public void DCDCOff(){
        payload[6] = 0;
        refreshDataPayload();
    }

    public void OBCOn(){
        payload[4] = 1;
        payload[5] = 0;
        refreshDataPayload();
    }

    public void OBCOff(){
        payload[4] = 0;
        payload[5] = 1;
        refreshDataPayload();
    }

    public boolean isDCDCOn(){
        if (payload[6] == 0){
            return false;
        } else
            return true;
    }

    public boolean isOBCOn(){
        if (payload[4] == 0)
            return false;
        else
            return true;
    }
}

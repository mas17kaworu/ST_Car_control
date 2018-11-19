package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUCarModeList;

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;

/**
 * Created by Administrator on 2018/5/18.
 */

public class CMDVCUCarMode extends BaseCommand {
    protected static byte[] payload = {0x01,0x00,0x00,0x00,0x00,0x00,0x00,0x00};

    public CMDVCUCarMode(){
        try{
            data = new byte[10];
            dataLength = 10;
            data[0] = 0x0A;
            data[1] = (byte) (0b101<<5 | COMMAND_VCU_CAR_MODE);
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
        return COMMAND_VCU_CAR_MODE;
    }

    public void change2Static(){
        payload[0] = 1;
        refreshDataPayload();
    }

    public void change2Driving(){
        payload[0] = 2;
        refreshDataPayload();
    }
}

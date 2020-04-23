package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDOLEDBackList;

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;



public class CMDOLEDBase extends BaseCommand {


    protected static final byte TurnLeft = (byte)0x10;
    protected static final byte TurnRight = (byte)0x08;
    protected static final byte Position = (byte)0x04;
    protected static final byte Brake    = (byte)0x02;
    protected static final byte Reversing = (byte)0x01;

    protected static final byte AutoRun1 = (byte)0x20;
    protected static final byte AutoRun2 = (byte)0x40;
    protected static final byte AutoRun3 = (byte)0x80;

    protected static byte[] payload = {0x00};

    public CMDOLEDBase(){
        try{
            data = new byte[3];
            dataLength = 3;
            data[0] = 0x03;
            data[1] = COMMAND_OLED_BACK;
            data[2] = payload[0];
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void refreshDataPayload(){
        data[2] = payload[0];
    }

    @Override
    public BaseResponse toResponse(byte[] data) throws Exception {
        Response response = new Response(getCommandId());
        return response;
    }

    public byte getPayload(){
      return payload[0];
    }


    @Override
    public byte getCommandId() {
        return COMMAND_OLED_BACK;
    }

    public static class Response extends BaseResponse {
        public Response(byte commandId) {
            super(commandId);
        }
    }
}

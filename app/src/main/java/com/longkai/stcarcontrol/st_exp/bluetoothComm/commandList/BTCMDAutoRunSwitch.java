package com.longkai.stcarcontrol.st_exp.bluetoothComm.commandList;

/**
 * Created by Administrator on 2017/8/5.
 */

public class BTCMDAutoRunSwitch extends BaseBtCommand {

    public BTCMDAutoRunSwitch(boolean state){
        try{
            data = new byte[3];
            dataLength = 3;
            data[0] = 0x02;
            data[1] = COMMAND_AUTO_RUN;
            if (state){
                data[2] = 0x01;
            }else {
                data[2] = 0x00;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public BaseBTResponse toResponse(byte[] data) throws Exception {

        return null;
    }

    @Override
    public byte getCommandId() {
        return COMMAND_AUTO_RUN;
    }


    public static class Response extends BaseBTResponse{

        public Response(byte commandId) {
            super(commandId);
        }
    }
}

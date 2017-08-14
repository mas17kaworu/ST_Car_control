package com.longkai.stcarcontrol.st_exp.bluetoothComm.commandList;

/**
 * Created by Administrator on 2017/8/5.
 */

public class BTCMDGetVersion extends BaseBtCommand{
    public BTCMDGetVersion(){
        try{
            data = new byte[2];
            dataLength = 2;
            data[0] = 0x02;
            data[1] = COMMAND_GET_FIRMWARE;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public BaseBTResponse toResponse(byte[] data) throws Exception {
        int length = data[2] -1;
        byte[] payload = new byte[length];
        System.arraycopy(data, 4, payload, 0, length);

        Response response=new Response(getCommandId());
        response.setVersion(new String(payload));
        return response;
    }

    @Override
    public byte getCommandId() {
        return COMMAND_GET_FIRMWARE;
    }


    public static class Response extends BaseBTResponse{

        private String version;

        public Response(byte commandId) {
            super(commandId);
        }

        public String getVersion(){
            return version;
        }

        public void setVersion(String v){
            version = v;
        }


    }
}

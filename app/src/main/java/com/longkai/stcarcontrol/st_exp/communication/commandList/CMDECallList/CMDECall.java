package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDECallList;

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;

/**
 * Created by Administrator on 2018/9/23.
 */

public class CMDECall extends BaseCommand {


    public CMDECall(String phoneNum, Type type){
        try{
            long number = Long.valueOf(phoneNum);
            data = new byte[0x0A];
            dataLength = 10;
            data[0] = 0x0A;
            data[1] = (COMMAND_ECALL);
            for (int i = 0; i <5; i++){
                data[i+2] =(byte) (number / Math.pow(10, (9-i*2)));
                number = (long) (number % Math.pow(10, (9-i*2)));
            }
            data[7] = (byte)number;
            data[8] = (byte) 255;
            if (Type.call.equals(type)) {
                data[9] = 2;
            } else if (Type.set.equals(type)) {
                data[9] = 1;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public enum  Type{
        set,
        call
    }

    @Override
    public BaseResponse toResponse(byte[] data) throws Exception {
        return new Response(COMMAND_ECALL);
    }

    @Override
    public byte getCommandId() {
        return COMMAND_ECALL;
    }

    public static class Response extends BaseResponse {
        public Response(byte commandId) {
            super(commandId);
        }
    }
}

package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCULoopFuncList;

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;

/**
 * Created by Administrator on 2018/5/13.
 */

public class CMDVCULoopFunc extends BaseCommand {

    protected static final byte Start_Loop_Func_6 = (byte)0x20;
    protected static final byte Start_Loop_Func_5 = (byte)0x10;
    protected static final byte Start_Loop_Func_4 = (byte)0x08;
    protected static final byte Start_Loop_Func_3 = (byte)0x04;
    protected static final byte Start_Loop_Func_2 = (byte)0x02;
    protected static final byte Start_Loop_Func_1 = (byte)0x01;

    protected static byte[] payload = {0x00,0x00,0x00};

    public CMDVCULoopFunc(){
        try{
            data = new byte[3];
            dataLength = 3;
            data[0] = 0x03;
            data[1] = COMMAND_VCU_HV_LOOP_FUNC;
            data[2] = payload[0];
        }catch (Exception e){

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

    public static class Response extends BaseResponse {
        public Response(byte commandId) {
            super(commandId);
        }
    }

    @Override
    public byte getCommandId() {
        return COMMAND_VCU_HV_LOOP_FUNC;
    }
}

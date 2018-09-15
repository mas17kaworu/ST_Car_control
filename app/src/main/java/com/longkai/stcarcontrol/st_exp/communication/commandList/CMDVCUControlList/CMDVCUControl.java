package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUControlList;

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;

/**
 * Created by Administrator on 2018/5/13.
 */

public class CMDVCUControl extends BaseCommand {


    protected static final byte Start_HV_Power_ON = (byte)0x02;
    protected static final byte Start_HV_Power_OFF = (byte)0x01;

    protected static final byte Fun_Loop_Demo = (byte)0x80;
    protected static final byte Fun_Charger_HVIL = (byte)0x40;
    protected static final byte Fun_HV_Power_Off = (byte)0x20;
    protected static final byte Fun_Inter_Tbox = (byte)0x10;
    protected static final byte Fun_Inter_MCU = (byte)0x08;
    protected static final byte Fun_Inter_BMS = (byte)0x04;
    protected static final byte Fun_HV_Power_On = (byte)0x02;
    protected static final byte Fun_HV_Iso_Det = (byte)0x01;


    protected static final byte Run_Loop_Demon = (byte)0x80;
    protected static final byte Start_Loop_Func_6 = (byte)0x20;
    protected static final byte Start_Loop_Func_5 = (byte)0x10;
    protected static final byte Start_Loop_Func_4 = (byte)0x08;
    protected static final byte Start_Loop_Func_3 = (byte)0x04;
    protected static final byte Start_Loop_Func_2 = (byte)0x02;
    protected static final byte Start_Loop_Func_1 = (byte)0x01;

    protected static byte[] payload = {0x00,0x00,0x00};

    public CMDVCUControl(){
        try{
            data = new byte[5];
            dataLength = 5;
            data[0] = 0x05;
            data[1] = COMMAND_VCU_CONTROL;
            data[2] = payload[0];
            data[3] = payload[1];
            data[4] = payload[2];
        }catch (Exception e){

        }
    }

    protected void refreshDataPayload(){
        data[2] = payload[0];
        data[3] = payload[1];
        data[4] = payload[2];
    }

    @Override
    public BaseResponse toResponse(byte[] data) throws Exception {
        Response response = new CMDVCUControl.Response(getCommandId());
        return response;
    }

    public static class Response extends BaseResponse {
        public Response(byte commandId) {
            super(commandId);
        }
    }

    @Override
    public byte getCommandId() {
        return COMMAND_VCU_CONTROL;
    }
}

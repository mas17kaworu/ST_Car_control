package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUGUIList;

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;

/**
 * Created by Administrator on 2018/5/13.
 */

public class CMDVCUHVOff extends BaseCommand {


    protected static final byte Fun_Loop_Demo = (byte)0x80;
    protected static final byte Fun_Charger_HVIL = (byte)0x40;
    protected static final byte Fun_HV_Power_Off = (byte)0x20;
    protected static final byte Fun_Inter_Tbox = (byte)0x10;
    protected static final byte Fun_Inter_MCU = (byte)0x08;
    protected static final byte Fun_Inter_BMS = (byte)0x04;
    protected static final byte Fun_HV_Power_On = (byte)0x02;
    protected static final byte Fun_HV_Iso_Det = (byte)0x01;

    protected static byte[] payload = {0x00,0x00,0x00};

    public CMDVCUHVOff(boolean state){
        try{
            data = new byte[3];
            dataLength = 3;
            data[0] = 0x03;
            data[1] = COMMAND_VCU_HV_Power_OFF;
            data[2] = state? (byte)0x01: (byte)0x00;
        }catch (Exception e){

        }
    }

    protected void refreshDataPayload(){
        data[2] = payload[0];
    }

    @Override
    public BaseResponse toResponse(byte[] data) throws Exception {
        Response response = new CMDVCUHVOff.Response(getCommandId());
        return response;
    }

    public static class Response extends BaseResponse {
        public Response(byte commandId) {
            super(commandId);
        }
    }

    @Override
    public byte getCommandId() {
        return COMMAND_VCU_HV_Power_OFF;
    }
}

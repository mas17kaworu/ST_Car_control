package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCU6List;

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;

/**
 * Created by Administrator on 2018/5/13.
 */

public class CMDVCU6 extends BaseCommand {
    public CMDVCU6(){
        try{
            data = new byte[2];
            dataLength = 2;
            data[0] = 0x02;
            data[1] = (byte) (0b101<<5 | COMMAND_VCU6);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public BaseResponse toResponse(byte[] data) throws Exception {
        Response response = new Response(getCommandId());
        if (data[2] == 0x07){
            response.Charger_Status = (data[4] & 0x01);
            response.Locker_Status = (data[4] & 0x02)>>1;
            response.HVIL_Status = (data[4] & 0x04)>>2;
            response.MCU_Status_Monitor = (data[4] & 0x08)>>3;
            response.BMS_Status_Monitor = (data[4] & 0x10)>>4;
            response.Isolation_Resistance_Monitor = (data[4] & 0x20)>>5;
            response.High_Voltage_Monitor = (data[4] & 0x40)>>6;
            response.HV_Circuit_State = (data[4] & 0x80)>>7;
            response.f_HVIL_Out = (data[6] & 0xff)<<8 | (data[5] & 0xff);
            response.f_HVIL_In = (data[8] & 0xff)<<8 | (data[7] & 0xff);
        }
        return response;
    }

    @Override
    public byte getCommandId() {
        return COMMAND_VCU6;
    }

    public static class Response extends BaseResponse {

        public int f_HVIL_In;
        public int f_HVIL_Out;
        public int HV_Circuit_State;
        public int High_Voltage_Monitor;
        public int Isolation_Resistance_Monitor;
        public int BMS_Status_Monitor;
        public int MCU_Status_Monitor;
        public int HVIL_Status;
        public int Locker_Status;
        public int Charger_Status;

        public Response(byte commandId) {
            super(commandId);
        }
    }
}

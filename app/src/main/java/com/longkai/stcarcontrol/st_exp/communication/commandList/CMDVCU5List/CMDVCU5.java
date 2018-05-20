package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCU5List;

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;

/**
 * Created by Administrator on 2018/5/13.
 */

public class CMDVCU5 extends BaseCommand {
    public CMDVCU5(){
        try{
            data = new byte[2];
            dataLength = 2;
            data[0] = 0x02;
            data[1] =(byte) (0b101<<5 |  COMMAND_VCU5);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public BaseResponse toResponse(byte[] data) throws Exception {
        Response response = new Response(getCommandId());
        if (data[2] == 0x09){
            response.Whole_Distance = (data[7] & 0xff)<<24 | (data[6] & 0xff)<<16 |
                    (data[5] & 0xff)<<8 | (data[4] & 0xff);
            response.Operation_Mode = (data[8] & 0x03);
            response.Status_of_Charger = (data[8] & 0x0C) >> 2;
            response.Status_of_Car = (data[8] & 0x10) >> 4;
            response.General_Fault_Status = (data[10] & 0xff)<<8 | (data[9] & 0xff);
        }
        return response;
    }

    @Override
    public byte getCommandId() {
        return COMMAND_VCU5;
    }

    public static class Response extends BaseResponse {

        public int General_Fault_Status;
        public int Status_of_Car;
        public int Status_of_Charger;
        public int Operation_Mode;
        public int Whole_Distance;

        public Response(byte commandId) {
            super(commandId);
        }
    }
}

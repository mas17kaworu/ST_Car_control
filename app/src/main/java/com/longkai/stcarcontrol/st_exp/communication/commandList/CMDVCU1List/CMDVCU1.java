package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCU1List;

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;

/**
 * Created by Administrator on 2018/5/13.
 */

public class CMDVCU1 extends BaseCommand {
    public CMDVCU1(){
        try{
            data = new byte[2];
            dataLength = 2;
            data[0] = 0x02;
            data[1] = (byte) (0b101<<5 | COMMAND_VCU1);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public BaseResponse toResponse(byte[] data) throws Exception {
        Response response = new Response(getCommandId());
        if (data[2] == 0x0A){
            response.Lowest_Cell_Voltage = (data[5] & 0xff)<<8 | (data[4] & 0xff);
            response.Cell_Number_of_Lowest_Cell_Voltage = (data[6] & 0xff);
            response.Module_Number_of_Lowest_Cell_Voltage = (data[7] & 0xff);
            response.Highest_Cell_Voltage = (data[9] & 0xff)<<8 | (data[8] & 0xff);
            response.Cell_Number_of_Highest_Cell_Voltage = (data[10] & 0xff);
            response.Module_Number_of_Highest_Cell_Voltage = (data[11] & 0xff);
        }
        return response;
    }

    @Override
    public byte getCommandId() {
        return COMMAND_VCU1;
    }

    public static class Response extends BaseResponse {

        public int Module_Number_of_Highest_Cell_Voltage;
        public int Cell_Number_of_Highest_Cell_Voltage;
        public int Highest_Cell_Voltage;
        public int Module_Number_of_Lowest_Cell_Voltage;
        public int Cell_Number_of_Lowest_Cell_Voltage;
        public int Lowest_Cell_Voltage;

        public Response(byte commandId) {
            super(commandId);
        }
    }
}

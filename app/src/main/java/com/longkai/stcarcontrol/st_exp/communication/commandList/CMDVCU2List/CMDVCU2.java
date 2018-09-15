package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCU2List;

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;

/**
 * Created by Administrator on 2018/5/13.
 */

public class CMDVCU2 extends BaseCommand {
    public CMDVCU2(){
        try{
            data = new byte[2];
            dataLength = 2;
            data[0] = 0x02;
            data[1] = (byte) (0b101<<5 | COMMAND_VCU2);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public BaseResponse toResponse(byte[] data) throws Exception {
        Response response = new Response(getCommandId());
        if (data[2] == 0x08){
//            response.MCU_Status_Monitor = (data[4] & 0xff);
            response.Lowest_Cell_Temp = (data[4] & 0xff);
            response.Cell_Number_of_Lowest_Cell_Temp = (data[5] & 0xff);
            response.Module_Number_of_Lowest_Cell_Temp = (data[6] & 0xff);
            response.Highest_Cell_Temp = (data[7] & 0xff);
            response.Cell_Number_of_Highest_Cell_Temp = (data[8] & 0xff);
            response.Module_Number_of_Highest_Cell_Temp = (data[9] & 0xff);
        }
        return response;
    }

    @Override
    public byte getCommandId() {
        return COMMAND_VCU2;
    }

    public static class Response extends BaseResponse {

        public int Module_Number_of_Highest_Cell_Temp;
        public int Cell_Number_of_Highest_Cell_Temp;
        public int Highest_Cell_Temp;
        public int Module_Number_of_Lowest_Cell_Temp;
        public int Cell_Number_of_Lowest_Cell_Temp;
        public int Lowest_Cell_Temp;
//        public int MCU_Status_Monitor;

        public Response(byte commandId) {
            super(commandId);
        }
    }
}

package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUMCU1List;

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;

/**
 * Created by Administrator on 2018/5/13.
 */

public class CMDVCUMCU1 extends BaseCommand {
    public CMDVCUMCU1(){
        try{
            data = new byte[2];
            dataLength = 2;
            data[0] = 0x02;
            data[1] = (byte) (0b101<<5 | COMMAND_VCU_MCU_1);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public BaseResponse toResponse(byte[] data) throws Exception {
        Response response = new Response(getCommandId());
        if (data[2] == 0x0E){
            response.Current_of_MCU = (int)(((data[5] & 0xff)<<8 | (data[4] & 0xff)) * 0.1f) - 1000;
            response.Input_Voltage_of_MCU = (int)(((data[7] & 0xff)<<8 |(data[6] & 0xff)) * 0.1f);
            response.Torch_of_Motor = (((data[9] & 0xff)<<8 |(data[8] & 0xff)) *0.1f - 2000);
            response.Temp_of_MCU = (data[10] & 0xff) - 40;
            response.Temp_of_Motor = (data[11] & 0xff) - 40;
            response.Motor_Realtime_Speed = ((data[13] & 0xff)<<8 |(data[12] & 0xff));
            response.Motor_Current = (int)(((data[15] & 0xff)<<8 |(data[14] & 0xff)) * 0.1f);

        }
        return response;
    }

    @Override
    public byte getCommandId() {
        return COMMAND_VCU_MCU_1;
    }

    public static class Response extends BaseResponse {
        public	int	Motor_Current	;
        public	int	Motor_Realtime_Speed	;
        public	int Temp_of_Motor	;
        public	int	Temp_of_MCU	;
        public	float	Torch_of_Motor	;
        public	int	Input_Voltage_of_MCU	;
        public	int	Current_of_MCU	;



        public Response(byte commandId) {
            super(commandId);
        }
    }
}

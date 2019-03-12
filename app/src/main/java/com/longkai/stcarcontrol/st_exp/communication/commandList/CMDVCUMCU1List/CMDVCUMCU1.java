package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUMCU1List;

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;
import com.longkai.stcarcontrol.st_exp.communication.utils.CheckSumBit;

/**
 * Created by Administrator on 2018/5/13.
 */

public class CMDVCUMCU1 extends BaseCommand {
    public CMDVCUMCU1(){
        try{
            data = new byte[2];
            dataLength = 2;
            data[0] = 0x02;
            data[1] = (byte) (0b000<<5 | COMMAND_VCU_MCU_1);
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
            response.Torch_of_Motor = (((data[9] & 0xff)<<8 | (data[8] & 0xff)) *0.1f);
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

        public Response(){
            setCommandId(COMMAND_VCU_MCU_1);
        }

        public Response(byte commandId) {
            super(commandId);
        }

        //For test
        public byte[] mockResponse(){
            byte[] array = new byte[0x11];
            array[0] = BaseCommand.COMMAND_HEAD0;
            array[1] = BaseCommand.COMMAND_HEAD1;
            array[2] = 0x0E;
            array[3] = COMMAND_VCU_MCU_1;

            int tmpInt = (this.Current_of_MCU + 1000) * 10;
            array[4] = (byte)(((tmpInt) & 0xff));
            array[5] = (byte)(((tmpInt) & 0xff00) >> 8);

            tmpInt = (this.Input_Voltage_of_MCU) * 10;
            array[6] = (byte)(((tmpInt) & 0xff));
            array[7] = (byte)(((tmpInt) & 0xff00) >> 8);

            tmpInt = (int)(this.Torch_of_Motor) * 10;
            array[8] = (byte)(((tmpInt) & 0xff));
            array[9] = (byte)(((tmpInt) & 0xff00) >> 8);

            tmpInt = this.Temp_of_MCU + 40;
            array[10] = (byte)(((tmpInt) & 0xff));

            tmpInt = this.Temp_of_Motor + 40;
            array[11] = (byte)(((tmpInt) & 0xff));

            tmpInt = this.Motor_Realtime_Speed;
            array[12] = (byte)(((tmpInt) & 0xff));
            array[13] = (byte)(((tmpInt) & 0xff00) >> 8);

            tmpInt = this.Motor_Current;
            array[14] = (byte)(((tmpInt) & 0xff));
            array[15] = (byte)(((tmpInt) & 0xff00) >> 8);

            array[16] = CheckSumBit.checkSum(array, array.length - 1);
            return array;
        }
    }
}

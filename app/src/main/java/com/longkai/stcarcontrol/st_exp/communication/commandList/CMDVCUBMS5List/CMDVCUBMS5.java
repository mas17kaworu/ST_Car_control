package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUBMS5List;

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDGetVersion;
import com.longkai.stcarcontrol.st_exp.communication.utils.CheckSumBit;

/**
 * Created by Administrator on 2018/5/13.
 */

public class CMDVCUBMS5 extends BaseCommand {
    public CMDVCUBMS5(){
        try{
            data = new byte[2];
            dataLength = 2;
            data[0] = 0x02;
            data[1] = (byte) (0b101<<5 | COMMAND_VCU_BMS_5);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public BaseResponse toResponse(byte[] data) throws Exception {
        Response response = new Response(getCommandId());
        if (data[2] == 0x04){
            response.BMS_Fault_2 = (data[4] & 0x01);
            response.BMS_Fault_1 = (data[4] & 0x02)>>1;
            response.SOC = (data[5] & 0xff) * 0.392f;
        }
        return response;
    }

    @Override
    public byte getCommandId() {
        return COMMAND_VCU_BMS_5;
    }

    public static class Response extends BaseResponse {
        public	float	SOC	; // 0~100
        public	int	BMS_Fault_1; //Pack_Fault
        public	int	BMS_Fault_2; //Cell_Fualt

        public Response(){
            setCommandId(COMMAND_VCU_BMS_5);
        }

        public Response(byte commandId) {
            super(commandId);
        }

        @Override
        public byte[] mockResponse() {
            byte[] array = new byte[0x07];
            array[0] = BaseCommand.COMMAND_HEAD0;
            array[1] = BaseCommand.COMMAND_HEAD1;
            array[2] = 0x04;
            array[3] = (byte)getCommandId();

            float tmpfloat = (this.SOC);
            array[5] = (byte) (((int)(tmpfloat / 0.392)) & 0xff);


            array[array.length - 1] = CheckSumBit.checkSum(array, array.length - 1);
            return array;
        }
    }
}

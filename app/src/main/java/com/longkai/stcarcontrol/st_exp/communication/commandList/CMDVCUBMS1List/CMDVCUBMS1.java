package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUBMS1List;

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUMCU1List.CMDVCUMCU1;
import com.longkai.stcarcontrol.st_exp.communication.utils.CheckSumBit;

/**
 * Created by Administrator on 2018/5/13.
 */

public class CMDVCUBMS1 extends BaseCommand {
    public CMDVCUBMS1(){
        try{
            data = new byte[2];
            dataLength = 2;
            data[0] = 0x02;
            data[1] = (byte) (0b000<<5 | COMMAND_VCU_BMS_1);//10100000
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public BaseResponse toResponse(byte[] data) throws Exception {
        Response response = new Response(getCommandId());
        if (data[2] == 0x0C){
            response.R_Isolation_Minus = (data[5] & 0xff)<<8 | (data[4] & 0xff);
            response.R_Isolation_Plus = (data[7] & 0xff)<<8 | (data[6] & 0xff);
            response.U_HighVoltage_3 = ((data[9] & 0xff)<<8 | (data[8] & 0xff)) *0.1f;
            response.U_HighVoltage_2 = ((data[11] & 0xff)<<8 | (data[10] & 0xff)) *0.1f;
            response.U_HighVoltage_1 = ((data[13] & 0xff)<<8 | (data[12] & 0xff)) *0.1f;
        }
        return response;
    }

    @Override
    public byte getCommandId() {
        return COMMAND_VCU_BMS_1;
    }

    public static class Response extends BaseResponse {

        public  float	U_HighVoltage_1	;
        public	float	U_HighVoltage_2	;
        public	float	U_HighVoltage_3	;
        public	int	R_Isolation_Plus	;
        public	int	R_Isolation_Minus	;

        public Response(){
            setCommandId(COMMAND_VCU_BMS_1);
        }

        public Response(byte commandId) {
            super(commandId);
        }

        @Override
        public byte[] mockResponse() {
            byte[] array = new byte[0x0f];
            array[0] = BaseCommand.COMMAND_HEAD0;
            array[1] = BaseCommand.COMMAND_HEAD1;
            array[2] = 0x0C;
            array[3] = (byte)getCommandId();

            int tmpInt = (this.R_Isolation_Minus);
            array[4] = (byte)(((tmpInt) & 0xff));
            array[5] = (byte)(((tmpInt) & 0xff00) >> 8);

            tmpInt = (this.R_Isolation_Plus);
            array[6] = (byte)(((tmpInt) & 0xff));
            array[7] = (byte)(((tmpInt) & 0xff00) >> 8);

            tmpInt = (int)(this.U_HighVoltage_3) * 10;
            array[8] = (byte)(((tmpInt) & 0xff));
            array[9] = (byte)(((tmpInt) & 0xff00) >> 8);

            tmpInt = (int)(this.U_HighVoltage_2) * 10;
            array[10] = (byte)(((tmpInt) & 0xff));
            array[11] = (byte)(((tmpInt) & 0xff00) >> 8);

            tmpInt = (int)(this.U_HighVoltage_1) * 10;
            array[12] = (byte)(((tmpInt) & 0xff));
            array[13] = (byte)(((tmpInt) & 0xff00) >> 8);

            array[14] = CheckSumBit.checkSum(array, array.length - 1);
            return array;
        }
    }
}

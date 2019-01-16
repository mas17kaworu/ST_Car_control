package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUBMS3List;

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;

/**
 * Created by Administrator on 2018/5/13.
 */

public class CMDVCUBMS3 extends BaseCommand {
    public CMDVCUBMS3(){
        try{
            data = new byte[2];
            dataLength = 2;
            data[0] = 0x02;
            data[1] = (byte) (0b101<<5 | COMMAND_VCU_BMS_3);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public BaseResponse toResponse(byte[] data) throws Exception {
        Response response = new Response(getCommandId());
        if (data[2] >= 0x20){
            response.CellBalance_Status_14 = (data[4] & 0x01);
            response.CellBalance_Status_13 = (data[4] & 0x02)>>1;
            response.CellBalance_Status_12 = (data[4] & 0x04)>>2;
            response.CellBalance_Status_11 = (data[4] & 0x08)>>3;
            response.CellBalance_Status_10 = (data[4] & 0x10)>>4;
            response.CellBalance_Status_9 = (data[4] & 0x20)>>5;
            response.CellBalance_Status_8 = (data[4] & 0x40)>>6;
            response.CellBalance_Status_7 = (data[4] & 0x80)>>7;
            response.CellBalance_Status_6 = (data[5] & 0x01);
            response.CellBalance_Status_5 = (data[5] & 0x02)>>1;
            response.CellBalance_Status_4 = (data[5] & 0x04)>>2;
            response.CellBalance_Status_3 = (data[5] & 0x08)>>3;
            response.CellBalance_Status_2 = (data[5] & 0x10)>>4;
            response.CellBalance_Status_1 = (data[5] & 0x20)>>5;

            response.CellBalanceArray[13] = (data[4] & 0x01);
            response.CellBalanceArray[12] = (data[4] & 0x02)>>1;
            response.CellBalanceArray[11] = (data[4] & 0x04)>>2;
            response.CellBalanceArray[10] = (data[4] & 0x08)>>3;
            response.CellBalanceArray[9] = (data[4] & 0x10)>>4;
            response.CellBalanceArray[8] = (data[4] & 0x20)>>5;
            response.CellBalanceArray[7] = (data[4] & 0x40)>>6;
            response.CellBalanceArray[6] = (data[4] & 0x80)>>7;
            response.CellBalanceArray[5] = (data[5] & 0x01);
            response.CellBalanceArray[4] = (data[5] & 0x02)>>1;
            response.CellBalanceArray[3] = (data[5] & 0x04)>>2;
            response.CellBalanceArray[2] = (data[5] & 0x08)>>3;
            response.CellBalanceArray[1] = (data[5] & 0x10)>>4;
            response.CellBalanceArray[0] = (data[5] & 0x20)>>5;

            //cell voltage 单位mV
            response.Cell_14_Voltage = (int)(((data[7] & 0xff)<<8 |(data[6] & 0xff)) * 0.1f);
            response.Cell_13_Voltage = (int)(((data[9] & 0xff)<<8 |(data[8] & 0xff)) * 0.1f);
            response.Cell_12_Voltage = (int)(((data[11] & 0xff)<<8 |(data[10] & 0xff)) * 0.1f);
            response.Cell_11_Voltage = (int)(((data[13] & 0xff)<<8 |(data[12] & 0xff)) * 0.1f);
            response.Cell_10_Voltage = (int)(((data[15] & 0xff)<<8 |(data[14] & 0xff)) * 0.1f);
            response.Cell_9_Voltage = (int)(((data[17] & 0xff)<<8 |(data[16] & 0xff)) * 0.1f);
            response.Cell_8_Voltage = (int)(((data[19] & 0xff)<<8 |(data[18] & 0xff)) * 0.1f);
            response.Cell_7_Voltage = (int)(((data[21] & 0xff)<<8 |(data[20] & 0xff)) * 0.1f);
            response.Cell_6_Voltage = (int)(((data[23] & 0xff)<<8 |(data[22] & 0xff)) * 0.1f);
            response.Cell_5_Voltage = (int)(((data[25] & 0xff)<<8 |(data[24] & 0xff)) * 0.1f);
            response.Cell_4_Voltage = (int)(((data[27] & 0xff)<<8 |(data[26] & 0xff)) * 0.1f);
            response.Cell_3_Voltage = (int)(((data[29] & 0xff)<<8 |(data[28] & 0xff)) * 0.1f);
            response.Cell_2_Voltage = (int)(((data[31] & 0xff)<<8 |(data[30] & 0xff)) * 0.1f);
            response.Cell_1_Voltage = (int)(((data[33] & 0xff)<<8 |(data[32] & 0xff)) * 0.1f);

            response.CellVoltageArray[13] = (data[7] & 0xff)<<8 |(data[6] & 0xff);
            response.CellVoltageArray[12] = (data[9] & 0xff)<<8 |(data[8] & 0xff);
            response.CellVoltageArray[11] = (data[11] & 0xff)<<8 |(data[10] & 0xff);
            response.CellVoltageArray[10] = (data[13] & 0xff)<<8 |(data[12] & 0xff);
            response.CellVoltageArray[9] = (data[15] & 0xff)<<8 |(data[14] & 0xff);
            response.CellVoltageArray[8] = (data[17] & 0xff)<<8 |(data[16] & 0xff);
            response.CellVoltageArray[7] = (data[19] & 0xff)<<8 |(data[18] & 0xff);
            response.CellVoltageArray[6] = (data[21] & 0xff)<<8 |(data[20] & 0xff);
            response.CellVoltageArray[5] = (data[23] & 0xff)<<8 |(data[22] & 0xff);
            response.CellVoltageArray[4] = (data[25] & 0xff)<<8 |(data[24] & 0xff);
            response.CellVoltageArray[3] = (data[27] & 0xff)<<8 |(data[26] & 0xff);
            response.CellVoltageArray[2] = (data[29] & 0xff)<<8 |(data[28] & 0xff);
            response.CellVoltageArray[1] = (data[31] & 0xff)<<8 |(data[30] & 0xff);
            response.CellVoltageArray[0] = (data[33] & 0xff)<<8 |(data[32] & 0xff);
        }
        return response;
    }

    @Override
    public byte getCommandId() {
        return COMMAND_VCU_BMS_3;
    }

    public static class Response extends BaseResponse {
        public	int	Cell_1_Voltage	;
        public	int	Cell_2_Voltage	;
        public	int	Cell_3_Voltage	;
        public	int	Cell_4_Voltage	;
        public	int	Cell_5_Voltage	;
        public	int	Cell_6_Voltage	;
        public	int	Cell_7_Voltage	;
        public	int	Cell_8_Voltage	;
        public	int	Cell_9_Voltage	;
        public	int	Cell_10_Voltage	;
        public	int	Cell_11_Voltage	;
        public	int	Cell_12_Voltage	;
        public	int	Cell_13_Voltage	;
        public	int	Cell_14_Voltage	;
        public	int	CellBalance_Status_1	;
        public	int	CellBalance_Status_2	;
        public	int	CellBalance_Status_3	;
        public	int	CellBalance_Status_4	;
        public	int	CellBalance_Status_5	;
        public	int	CellBalance_Status_6	;
        public	int	CellBalance_Status_7	;
        public	int	CellBalance_Status_8	;
        public	int	CellBalance_Status_9	;
        public	int	CellBalance_Status_10	;
        public	int	CellBalance_Status_11	;
        public	int	CellBalance_Status_12	;
        public	int	CellBalance_Status_13	;
        public	int	CellBalance_Status_14	;
        public int[] CellBalanceArray=new int[14];
        public int[] CellVoltageArray=new int[14];

        public Response(byte commandId) {
            super(commandId);
        }
    }
}

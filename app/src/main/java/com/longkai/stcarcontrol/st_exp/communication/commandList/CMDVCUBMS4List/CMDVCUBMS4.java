package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUBMS4List;

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;

/**
 * Created by Administrator on 2018/5/13.
 */

public class CMDVCUBMS4 extends BaseCommand {
    public CMDVCUBMS4(){
        try{
            data = new byte[2];
            dataLength = 2;
            data[0] = 0x02;
            data[1] = (byte) (0b000<<5 | COMMAND_VCU_BMS_4);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public BaseResponse toResponse(byte[] data) throws Exception {
        Response response = new Response(getCommandId());
        if (data[2] == 0x14){
            response.Module_Temperature_7 = ((data[5] & 0x01)<<8 |(data[4] & 0xff)) * 0.2f;
            response.Module_Temperature_6 = ((data[7] & 0x01)<<8 |(data[6] & 0xff)) * 0.2f;
            response.Module_Temperature_5 = ((data[9] & 0x01)<<8 |(data[8] & 0xff)) * 0.2f;
            response.Module_Temperature_4 = ((data[11] & 0x01)<<8 |(data[10] & 0xff)) * 0.2f;
            response.Module_Temperature_3 = ((data[13] & 0x01)<<8 |(data[12] & 0xff)) * 0.2f;
            response.Module_Temperature_2 = ((data[15] & 0x01)<<8 |(data[14] & 0xff)) * 0.2f;
            response.Module_Temperature_1 = ((data[17] & 0x01)<<8 |(data[16] & 0xff)) * 0.2f;
            response.Pack_Current = ((data[19] & 0xff)<<8 |(data[18] & 0xff)) * 0.1f;
            response.Pack_Voltage = ((data[21] & 0xff)<<8 |(data[20] & 0xff)) * 0.1f;
        }
        return response;
    }

    @Override
    public byte getCommandId() {
        return COMMAND_VCU_BMS_4;
    }

    public static class Response extends BaseResponse {
        public	float	Pack_Voltage	;
        public	float	Pack_Current	;
        public	float	Module_Temperature_1	;
        public	float	Module_Temperature_2	;
        public	float	Module_Temperature_3	;
        public	float	Module_Temperature_4	;
        public	float	Module_Temperature_5	;
        public	float	Module_Temperature_6	;
        public	float	Module_Temperature_7	;


        public Response(byte commandId) {
            super(commandId);
        }
    }
}

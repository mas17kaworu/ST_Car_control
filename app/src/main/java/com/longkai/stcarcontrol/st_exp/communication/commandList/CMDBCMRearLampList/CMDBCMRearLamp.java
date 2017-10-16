package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDBCMRearLampList;

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;

/**
 *
 *
 * Created by Administrator on 2017/8/15.
 */

public class CMDBCMRearLamp extends BaseCommand {

    /**
     *          b7          b6          b5          b4          b3          b3          b1          b0
     * Code1:                                       Left        Right       Position    Break       Reversing
     *
     *
     */

    protected static final byte TurnLeft = (byte)0x10;
    protected static final byte TurnRight = (byte)0x08;
    protected static final byte Position = (byte)0x04;
    protected static final byte Brake    = (byte)0x02;
    protected static final byte Reversing = (byte)0x01;

    protected static byte[] payload = {0x00};

    public CMDBCMRearLamp(){
        try{
            data = new byte[3];
            dataLength = 3;
            data[0] = 0x03;
            data[1] = COMMAND_BCM_REAR_LIGHT;
            data[2] = payload[0];
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 填写diagnostic 的方法
     *
     * @param diagnostic
     */
    public CMDBCMRearLamp(boolean diagnostic){
        try{
            data = new byte[3];
            dataLength = 3;
            data[0] = 0x03;
            data[1] = diagnostic ? (byte) (COMMAND_BCM_REAR_LIGHT | 0x80 ): COMMAND_BCM_REAR_LIGHT;
            data[2] = payload[0];
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void refreshDataPayload(){
        data[2] = payload[0];
    }

    @Override
    public BaseResponse toResponse(byte[] data) throws Exception {
        Response response = new Response(getCommandId());
        if (data[2] >= 0x1A){
            func(response.openLoad, data, 4);
            func(response.overLoad, data, 6);
            //  adc采样
            for (int i=0;i<9;i++){
                response.tempreture[i] = data[7+i*2] | (data[8+i*2]<<8);
            }

        } else if (data[2] == 0x02){

        }
        return response;
    }


    @Override
    public byte getCommandId() {
        return COMMAND_BCM_REAR_LIGHT;
    }

    public static class Response extends BaseResponse {

        public int[] openLoad;
        public int[] overLoad;
        public int[] tempreture;

        public Response(byte commandId) {
            super(commandId);
            openLoad = new int[17];
            overLoad = new int[17];
            tempreture = new int[9];
        }
    }

    private void func(final int[] dstArray, final byte[] srcByte,final int startBit){
        int num = 0;
        int i=0;
        byte tmp = srcByte[startBit + num];
        for (; i < 8;i++) {
            dstArray[i] = tmp & 0x01;
            tmp = (byte) (tmp>>1);
        }
        num++;
        tmp = srcByte[startBit + num];
        for (; i < 16;i++) {
            dstArray[i] = tmp & 0x01;
            tmp = (byte) (tmp>>1);
        }
        num++;
        tmp = srcByte[startBit + num];
        dstArray[i] = tmp & 0x01;
    }
}
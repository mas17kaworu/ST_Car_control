package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDFOTAList;

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;
import com.longkai.stcarcontrol.st_exp.communication.utils.CheckSumBit;

/**
 *
 * DIAG_CMD = {0:0:1 }, 请求更新A区;
 * DIAG_CMD = {0:1:0 },请求更新B区;
 * DIAG_CMD = {0:1:1 },取消更新请求（PAD收到允许更新回复后，更新CMD失能，继而可以下发0x20 CMD）;
 *
 * Created by Administrator on 2018/5/13.
 */

public class CMDFOTADIAG extends BaseCommand {
    private int DIAG_CMD = 0;
    private int DEVICE_NUM = 0;

    public CMDFOTADIAG(){
        try{
            data = new byte[3];
            dataLength = 3;
            data[0] = 0x03;
            data[1] = COMMAND_FOTA_DIAG;
            data[2] = (byte)(DIAG_CMD | (DEVICE_NUM << 5));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public CMDFOTADIAG(int diag_cmd){
        try{
            data = new byte[3];
            dataLength = 3;
            data[0] = 0x03;
            data[1] = COMMAND_FOTA_DIAG;
            data[2] = (byte)(diag_cmd | (DEVICE_NUM << 5));
            DIAG_CMD = diag_cmd;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public int getDIAG_CMD(){
        return DIAG_CMD;
    }
    public void setDIAG_CMD(int diag_cmd){
        DIAG_CMD = diag_cmd;
        data[2] = (byte)(DIAG_CMD | (DEVICE_NUM << 5));
    }

    @Override
    public BaseResponse toResponse(byte[] data) throws Exception {
        Response response = new Response(getCommandId());
        if (data[2] == 0x03){
            response.DEVICE_NUM = data[4]>>5;
            response.DIAG_CMD_RESPONSE = data[4] & 0x07;
        }
        return response;
    }

    @Override
    public byte getCommandId() {
        return COMMAND_FOTA_DIAG;
    }

    public static class Response extends BaseResponse {

        /**
         *
         * 收到更新请求回复：DIAG_CMD = {0:0:1 }, 允许更新A区;
         * DIAG_CMD = {0:1:0 },允许更新B区;
         * DIAG_CMD = {0:1:1 },拒绝更新请求或通讯取消;
         * DIAG_CMD = {0:0:0 },当前软件在该区运行，请选择其他
         *
         *
         *
         * DIAG_CMD = {1:0:0 },固件更新中；
         * DIAG_CMD = {1:1:1 },固件更新完成；
         * DIAG_CMD = {0:1:1 },通讯取消或通讯失败；（其他指令为无效指令，PAD继续等待超时）
         *
         */
        public	int	DIAG_CMD_RESPONSE	;
        public	int	DEVICE_NUM	;

        public static final int A_READY_TO_UPDATE = 0x01;
        public static final int B_READY_TO_UPDATE = 0x02;
        public static final int REQUEST_DENY = 0x03;
        public static final int RUNNING = 0x00;

        public static final int FLASHING = 0x04;
        public static final int FLASH_FINISHED = 0x07;
        public static final int FLASH_ABORT = 0x03;

        public Response() {
            setCommandId(COMMAND_FOTA_DIAG);
        }

        public Response(byte commandId) {
            super(commandId);
        }

        @Override
        public byte[] mockResponse() {
            byte[] array = new byte[0x06];
            array[0] = BaseCommand.COMMAND_HEAD0;
            array[1] = BaseCommand.COMMAND_HEAD1;
            array[2] = 0x03;
            array[3] = (byte)getCommandId();

            int tmpInt = this.DIAG_CMD_RESPONSE;
            array[4] = (byte)( tmpInt );

            /*int tmpInt = (this.R_Isolation_Minus);
            array[4] = (byte)(((tmpInt) & 0xff));
            array[5] = (byte)(((tmpInt) & 0xff00) >> 8);*/


            array[5] = CheckSumBit.checkSum(array, array.length - 1);
            return array;
        }
    }
}

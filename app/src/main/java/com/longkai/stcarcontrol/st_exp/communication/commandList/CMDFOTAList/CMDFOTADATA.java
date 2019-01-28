package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDFOTAList;

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;
import com.longkai.stcarcontrol.st_exp.communication.utils.CheckSumBit;

/**
 *
 * 1：如果LAST_PKG = 1, PKG_NUM_OR_LENGTH指示最后一帧的bytes长度；如果LAST_PKG = 0，
 * PKG_NUM_OR_LENGTH指示PKG分包序号，分包序号由0向上递加1，除最后一帧外其他帧bytes长度固定为1024bytes
 * 2：Device_NUM指示该数据包对应的device number，暂定为0.
 * 3：Byte区域为每一分包数据的内容
 * 4：只有该命令 length和checksum固定为0xff ，其他命令length和checksum计算遵从之前协议
 * 5：PAD每发一帧数据都会等待超时10秒，超时发送CMD_FOTA_DIAG诊断命令中DIAG_CMD = {0:1:1 }取消指令
 *
 * Created by Administrator on 2018/5/13.
 */

public class CMDFOTADATA extends BaseCommand {


    private int LAST_PKG = 0;
    private int DEVICE_NUM = 0;
    private int PKG_NUM = 0;
    private int PKG_LENGTH = 0;

    /**
     *
     * @param pkgNum
     * @param pkgLength
     * @param lastPKG
     * @param payloadArray
     */
    public CMDFOTADATA(int pkgNum, int pkgLength, int lastPKG, byte[] payloadArray){
        try {
            data = new byte[pkgLength + 4];
            dataLength = pkgLength + 4;
            data[0] = (byte) 0xff; //Fixed
            data[1] = COMMAND_FOTA_DATA;
            if (lastPKG == 1) {
                data[2] = (byte) ((lastPKG << 7) | (DEVICE_NUM << 3) | ((pkgLength & 0x70) >> 8) );
                data[3] = (byte) (pkgLength & 0xff);
            } else if (lastPKG == 0){
                data[2] = (byte) ((lastPKG << 7) | (DEVICE_NUM << 3) | ((pkgNum & 0x70) >> 8) );
                data[3] = (byte) (pkgNum & 0xff);
            }

            this.PKG_NUM = pkgNum;
            this.PKG_LENGTH = pkgLength;
            this.LAST_PKG = lastPKG;
            System.arraycopy(payloadArray, 0, data, 4, pkgLength);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte[] toRawData() {
        //override checksum bit
        byte[] raw = super.toRawData();
        raw[raw.length - 1] = (byte)0xff;
        return raw;
    }

    @Override
    public BaseResponse toResponse(byte[] data) throws Exception {
        Response response = new Response(getCommandId());
        if (data[2] == 0x05){
            response.PKG_Received = (data[6] & 0x01);
            response.PKG_Aborted = (data[6] & 0x02) >> 1;
            response.PKG_NUM = ((data[5] & 0xff) | ((data[4] & 0x07) << 8));
            response.isLastPackage = (data[4] & 0x80) != 0;
        }
        return response;
    }


    public int getPKG_NUM(){
        return PKG_NUM;
    }

    public int getPKG_LENGTH(){
        return PKG_LENGTH;
    }

    public int getLAST_PKG(){
        return LAST_PKG;
    }

    @Override
    public byte getCommandId() {
        return COMMAND_FOTA_DATA;
    }

    public static class Response extends BaseResponse {

        public	int	PKG_Received	;
        public	int	PKG_Aborted	;
        public  int PKG_NUM ;
        public  boolean isLastPackage;

        public Response() {
            setCommandId(COMMAND_FOTA_DATA);
        }

        public Response(byte commandId) {
            super(commandId);
        }

        @Override
        public byte[] mockResponse() {
            byte[] array = new byte[0x08];
            array[0] = BaseCommand.COMMAND_HEAD0;
            array[1] = BaseCommand.COMMAND_HEAD1;
            array[2] = 0x05;
            array[3] = (byte)getCommandId();

            array[4] = this.isLastPackage ? (byte)0x80 : (byte)0x00;
            array[4] = (byte) (array[4] | ((byte)((this.PKG_NUM & 0x700) >> 8)));

            array[5] = (byte) (this.PKG_NUM & 0xff);
            array[6] = (byte) (this.PKG_Aborted << 1 | this.PKG_Received);
            /*int tmpInt = (this.R_Isolation_Minus);
            array[4] = (byte)(((tmpInt) & 0xff));
            array[5] = (byte)(((tmpInt) & 0xff00) >> 8);
            */

            array[array.length - 1] = CheckSumBit.checkSum(array, array.length - 1);
            return array;
        }
    }
}

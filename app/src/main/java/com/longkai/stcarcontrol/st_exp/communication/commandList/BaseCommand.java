package com.longkai.stcarcontrol.st_exp.communication.commandList;

import com.longkai.stcarcontrol.st_exp.communication.Command;
import com.longkai.stcarcontrol.st_exp.communication.utils.CheckSumBit;

/**
 * Created by Administrator on 2017/7/25.
 */

public abstract class BaseCommand implements Command {
    public static final boolean IS_BIGENDIAN = false;
    public static final byte COMMAND_HEAD0 = 0x5A;
    public static final byte COMMAND_HEAD1 = 0x3C;

    protected static final byte COMMAND_GET_FIRMWARE    = 0x01;
    protected static final byte COMMAND_AUTO_RUN        = 0x02;
    protected static final byte COMMAND_LED_HEADLAMP    = 0x03;
    protected static final byte COMMAND_BCM_REAR_LIGHT  = 0x04;
    protected static final byte COMMAND_POWER_SEAT      = 0x05;
    protected static final byte COMMAND_DOOR            = 0x06;
    protected static final byte COMMAND_HAVC            = 0x07;
    protected static final byte COMMAND_PLGM            = 0x08;
    protected static final byte COMMAND_CENTRAL_CONTORL = 0x09;

    protected byte[] data;//payload
    protected int dataLength;

    public void turnOn(){
    }

    public void turnOff(){

    }


    public byte[] toRawData() {
        byte[] raw;
        if (data == null || data.length == 2) {
            raw = new byte[5];
        } else {
            raw = new byte[3 + data.length];
        }
        raw[0] = COMMAND_HEAD0;
        raw[1] = COMMAND_HEAD1;
        System.arraycopy(data, 0, raw, 2, dataLength);
        raw[raw.length-1] = CheckSumBit.checkSum(data, dataLength);
        return raw;
    }

    /*protected byte[] get2Bytes(int value) {
        byte[] result = new byte[2];
        if (IS_BIGENDIAN) {
            System.arraycopy(ByteUtils.intToByteArray(value, IS_BIGENDIAN), 2, result, 0, 2);
        } else {
            System.arraycopy(ByteUtils.intToByteArray(value, IS_BIGENDIAN), 0, result, 0, 2);
        }
        return result;
    }*/
}
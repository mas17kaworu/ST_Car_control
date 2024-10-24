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


    protected static final byte COMMAND_VCU_CAR_MODE    = 0x0A;
    protected static final byte COMMAND_VCU_FUN         = 0x0B;
    protected static final byte COMMAND_VCU_HV_Power_ON = 0x0C;
    protected static final byte COMMAND_VCU_HV_Power_OFF= 0x0D;
    protected static final byte COMMAND_VCU_HV_LOOP_DEMO= 0x0E;
    protected static final byte COMMAND_VCU_HV_LOOP_FUNC= 0x0F;


    protected static final byte COMMAND_VCU1            = 0x10;
    protected static final byte COMMAND_VCU2            = 0x11;
    protected static final byte COMMAND_VCU3            = 0x12;
    protected static final byte COMMAND_VCU4            = 0x12;
    protected static final byte COMMAND_VCU5            = 0x14;
    protected static final byte COMMAND_VCU6            = 0x15;
    protected static final byte COMMAND_VCU7            = 0x1F;
    protected static final byte COMMAND_VCU_BMS_1       = 0x16;
    protected static final byte COMMAND_VCU_BMS_2       = 0x17;
    protected static final byte COMMAND_VCU_BMS_3       = 0x18;
    protected static final byte COMMAND_VCU_BMS_4       = 0x19;
    protected static final byte COMMAND_VCU_BMS_5       = 0x1A;
    protected static final byte COMMAND_VCU_BMS_6       = 0x1B;
    protected static final byte COMMAND_VCU_MCU_1       = 0x1C;
    protected static final byte COMMAND_VCU_MCU_2       = 0x1D;

    protected static final byte COMMAND_GUI7           = 0x1E;
    protected static final byte COMMAND_FOTA_DATA      = 0x20;
    protected static final byte COMMAND_FOTA_DIAG      = 0x21;

    protected static final byte COMMAND_VCU_INFOTEINMENT= 0x00;
    protected static final byte COMMAND_INFOTEINMENT_NEW= 0x22;
    protected static final byte COMMAND_ECALL           = 0x23;

    protected static final byte COMMAND_NFC_RETURN      = 0x27;
    protected static final byte COMMAND_OBC_RETURN      = 0x26;

    protected static final byte COMMAND_OLED_BACK       = 0x28;

    protected static final byte COMMAND_SOUND           = 0x29;
    protected static final byte COMMAND_AVAS            = 0x30;
    protected static final byte COMMAND_KEY_PAIR        = 0x31;
    protected static final byte COMMAND_KEY_CHECK       = 0x32;
    protected static final byte COMMAND_SOUNDS_LED      = 0x33;
    protected static final byte COMMAND_C11_LIGHT       = 0x34;
    protected static final byte COMMAND_PBOX            = 0x35;
    protected static final byte COMMAND_OLED2           = 0x36;

    protected static final byte COMMAND_XPDC1           = 0x37;
    protected static final byte COMMAND_XPDC2           = 0x38;

    protected static final byte COMMAND_ZCU_EFUSE       = 0x39;
    protected static final byte COMMAND_ZCU             = 0x3A;

    protected byte[] data;//payload
    protected int dataLength;

    public void turnOn(){
    }

    public void turnOff(){
    }


  /**
   *
   *   - Header:   For synchronization purpose
   *   - Length:   The data length (byte) of communication Length, command & content
   *   - Command:  The command of protocol
   *   - Content:  The content of relevant command
   *   - Checksum: The checksum of the Length, Command & Content.
   * example
   *
   * Header	0x3C5A (先发送0x5A，再0x3C)
   * Length	0x3
   * Command	"B[7]: Reserved"	"B[0:7]: 0x27 (NFC CMD)"
   * Content  xxx
   *
   * Checksum	0x--
   *
   */
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

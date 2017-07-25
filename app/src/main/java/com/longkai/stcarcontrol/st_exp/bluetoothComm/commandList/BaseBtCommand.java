package com.longkai.stcarcontrol.st_exp.bluetoothComm.commandList;

import com.longkai.stcarcontrol.st_exp.bluetoothComm.CrcUtils;

/**
 * Created by Administrator on 2017/7/25.
 */

public class BaseBtCommand {


    protected byte[] data;//payload



    public byte[] toRawData() {
        byte[] raw;
        int len;
        if (data == null || data.length == 0) {
            raw = new byte[6];
            len = 2;
        } else {
            raw = new byte[6 + data.length];
            len = data.length + 2;
            System.arraycopy(data, 0, raw, 5, data.length);
        }
        raw[0] = 0x55;
        /*raw[1] = getSenderId();
        System.arraycopy(get2Bytes(len), 0, raw, 2, 2);
        raw[4] = getCommandId();*/
        raw[raw.length - 1] = CrcUtils.crc8Table(raw, 0, raw.length - 1);
        return raw;
    }

}

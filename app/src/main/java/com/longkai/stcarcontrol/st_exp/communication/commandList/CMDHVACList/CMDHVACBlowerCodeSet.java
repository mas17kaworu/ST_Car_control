package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDHVACList;

/**
 * Created by Administrator on 2017/8/29.
 */

public class CMDHVACBlowerCodeSet extends CMDHVAC {
    /**
     *
     * @param value  范围未定
     */
    public CMDHVACBlowerCodeSet(int value){
        // TODO: 2017/9/5 make sure 255 int change to byte is right
        super();
        payload[0] = (byte) value;
        refreshDataPayload();
    }
}

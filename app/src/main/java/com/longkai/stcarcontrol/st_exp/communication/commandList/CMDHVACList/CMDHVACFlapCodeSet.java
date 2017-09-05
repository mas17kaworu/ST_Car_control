package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDHVACList;

/**
 * Created by Administrator on 2017/8/29.
 */

public class CMDHVACFlapCodeSet extends CMDHVAC{
    /**
     *
     * @param value  范围未定
     */
    public CMDHVACFlapCodeSet(int value){
        super();
        payload[1] = (byte) value;
        refreshDataPayload();
    }
}

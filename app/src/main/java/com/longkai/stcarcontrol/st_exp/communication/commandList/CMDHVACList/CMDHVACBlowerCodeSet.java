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
        super();
        payload[0] = (byte) value;
    }
}

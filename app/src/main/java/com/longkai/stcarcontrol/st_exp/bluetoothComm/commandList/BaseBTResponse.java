package com.longkai.stcarcontrol.st_exp.bluetoothComm.commandList;

/**
 * Created by Administrator on 2017/8/14.
 */

public class BaseBTResponse {
    public static final int CODE_SUCCESS=0;
    public static final int CODE_ERR_NOT_SUPPORT=1;
    public static final int CODE_ERR_INVAL_PARAM=2;
    public static final int CODE_ERR_INVAL_SETTING=3;
    public static final int CODE_ERR_BUSY=4;
    public static final int CODE_ERR_NOT_MATCH=5;
    public static final int CODE_ERR_GPS_NOT_FIXED=6;
    public static final int CODE_ERR_UNKNOW=254;

    protected byte commandId;

    public BaseBTResponse(byte commandId) {
        this.commandId=commandId;
    }

    public int getCommandId() {
        return commandId;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BaseBTResponse)) {
            return false;
        }
        BaseBTResponse oo = (BaseBTResponse) o;
        return commandId == oo.commandId;
    }
}

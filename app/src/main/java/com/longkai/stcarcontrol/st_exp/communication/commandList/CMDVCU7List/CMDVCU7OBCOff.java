package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCU7List;

/**
 * Created by Administrator on 2018/10/29.
 */

public class CMDVCU7OBCOff extends CMDVCU7 {
    public CMDVCU7OBCOff(){
        super();
    }

    public void on(){
        payload[5]=1;
        refreshDataPayload();
    }

    public void off(){
        payload[5]=0;
        refreshDataPayload();
    }

    public int getValue(){
        return payload[5];
    }
}

package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCU7List;

/**
 * Created by Administrator on 2018/10/29.
 */

public class CMDVCU7OBCOn extends CMDVCU7 {
    public CMDVCU7OBCOn(){
        super();
    }

    public void on(){
        payload[4]=1;
        refreshDataPayload();
    }

    public void off(){
        payload[4]=0;
        refreshDataPayload();
    }

    public int getValue(){
        return payload[4];
    }
}

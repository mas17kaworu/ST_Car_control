package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCU7List;

/**
 * Created by Administrator on 2018/10/29.
 */

public class CMDVCU7DCDC extends CMDVCU7 {
    public CMDVCU7DCDC(){
        super();
    }

    public void on(){
        payload[6]=1;
        refreshDataPayload();
    }

    public void off(){
        payload[6]=0;
        refreshDataPayload();
    }

    public int getValue(){
        return payload[6];
    }
}

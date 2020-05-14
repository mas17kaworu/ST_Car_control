package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUGUI7List;

/**
 * Created by Administrator on 2018/10/29.
 */

public class CMDVCUGUI7DCDC extends CMDVCUGUI7 {
    public CMDVCUGUI7DCDC(){
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

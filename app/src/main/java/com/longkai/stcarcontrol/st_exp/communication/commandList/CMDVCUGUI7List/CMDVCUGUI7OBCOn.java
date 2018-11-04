package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUGUI7List;


/**
 * Created by Administrator on 2018/10/29.
 */

public class CMDVCUGUI7OBCOn extends CMDVCUGUI7 {
    public CMDVCUGUI7OBCOn(){
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

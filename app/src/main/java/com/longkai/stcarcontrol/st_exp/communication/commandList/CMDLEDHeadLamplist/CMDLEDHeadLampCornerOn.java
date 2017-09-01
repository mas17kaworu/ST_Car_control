package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLamplist;

/**
 * @author yuneec-lk
 * @version 1.0
 * @since 1.0 on 2017/8/25 0025
 */

public class CMDLEDHeadLampCornerOn extends CMDLEDHeadLamp{
    public CMDLEDHeadLampCornerOn(){
        super();
        payload[0]|= Corner;
    }
}

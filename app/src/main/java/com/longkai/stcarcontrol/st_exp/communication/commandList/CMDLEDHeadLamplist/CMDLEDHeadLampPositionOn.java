package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLamplist;

/**
 * @author yuneec-lk
 * @version 1.0
 * @since 1.0 on 2017/8/25 0025
 */

public class CMDLEDHeadLampPositionOn extends CMDLEDHeadLamp {
    public CMDLEDHeadLampPositionOn(){
        super();
        payload[0]|= Position;
    }
}

package com.longkai.stcarcontrol.st_exp.communication.commandList.ledHeadLampCMDList;

/**
 * @author yuneec-lk
 * @version 1.0
 * @since 1.0 on 2017/8/25 0025
 */

public class CMDLEDHeadLampHighwayOff extends CMDLEDHeadLamp {
    public CMDLEDHeadLampHighwayOff(){
        super();
        payload[1] &= (~Highway);
    }
}

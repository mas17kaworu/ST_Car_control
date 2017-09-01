package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLamplist;

/**
 * @author yuneec-lk
 * @version 1.0
 * @since 1.0 on 2017/8/25 0025
 */

public class CMDLEDHeadLampHighBeamOn extends CMDLEDHeadLamp {
    public CMDLEDHeadLampHighBeamOn(Boolean isOn){
        super();
        if ( isOn ) {
            payload[0] |= HBAll;
        } else {
            payload[0] &= (~HBAll);
        }
    }
}

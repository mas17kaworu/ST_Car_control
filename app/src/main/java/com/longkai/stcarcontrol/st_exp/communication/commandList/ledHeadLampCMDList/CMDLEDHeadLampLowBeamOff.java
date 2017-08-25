package com.longkai.stcarcontrol.st_exp.communication.commandList.ledHeadLampCMDList;

/**
 * @author yuneec-lk
 * @version 1.0
 * @since 1.0 on 2017/8/25 0025
 */

public class CMDLEDHeadLampLowBeamOff extends CMDLEDHeadLamp{
    public CMDLEDHeadLampLowBeamOff(){
        super();
        payload[0] &= (~LowBeam1);
        payload[0] &= (~LowBeam2);
    }
}

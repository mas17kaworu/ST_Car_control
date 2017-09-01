package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLamplist;

/**
 *
 * 不是大灯
 * @author yuneec-lk
 * @version 1.0
 * @since 1.0 on 2017/8/25 0025
 */

public class CMDLEDHeadLampDRLLightOff extends CMDLEDHeadLamp {
    public CMDLEDHeadLampDRLLightOff(){
        super();
        payload[0]&= (~DRLLight);
    }
}

package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDBCMRearLampList;

/**
 * @author yuneec-lk
 * @version 1.0
 * @since 1.0 on 2017/8/25 0025
 */

public class CMDBCMRearLampTurnLeftOn extends CMDBCMRearLamp {
    public CMDBCMRearLampTurnLeftOn(){
        super();
        payload[0] |= TurnLeft;
    }
}

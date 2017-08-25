package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDBCMRearLampList;

/**
 * @author yuneec-lk
 * @version 1.0
 * @since 1.0 on 2017/8/25 0025
 */

public class CMDBCMRearLampBrakeLightOff extends CMDBCMRearLamp {
    public CMDBCMRearLampBrakeLightOff(){
        super();
        payload[0] &= (~Brake);
    }
}

package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDInfoteinment;

/**
 * Created by Administrator on 2018/9/23.
 */

public class CMDNewInfoteinmentVolumedecrease extends CMDInfoteinment {


    public void CMDNewInfoteinmentVolumedecrease(){
        payload[0] = (byte) 1d;
        payload[2] = (byte) 2d;
        refreshDataPayload();
    }
}

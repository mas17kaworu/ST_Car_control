package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDInfoteinment;

/**
 * Created by Administrator on 2018/9/23.
 */

public class CMDNewInfoteinmentVolumeIncrease extends CMDInfoteinment {


    public void CMDNewInfoteinmentVolumeIncrease(){
        payload[0] = (byte) 1d;
        payload[2] = (byte) 1d;
        refreshDataPayload();
    }
}

package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDInfoteinmentList;

/**
 * Created by Administrator on 2018/9/23.
 */

public class CMDNewInfoteinmentVolumeIncrease extends CMDNewInfoteinment {

    public CMDNewInfoteinmentVolumeIncrease(){
        super();
        payload[0] = (byte) 1d;
        payload[2] = (byte) 1d;
        refreshDataPayload();
    }
}

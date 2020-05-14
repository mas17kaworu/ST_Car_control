package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDInfoteinmentList;

/**
 * Created by Administrator on 2018/9/23.
 */

public class CMDNewInfoteinmentEngineVoice extends CMDNewInfoteinment {
    public CMDNewInfoteinmentEngineVoice(){
        super();
    }

    /**
     *
     * @param num 1~4
     */
    public void changeVoiceTo(int num) {
        payload[0] = (byte) 2d;
        payload[1] = (byte) num;
        payload[2] = 0;
        refreshDataPayload();
    }
}

package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDInfoteinmentList;

/**
 * Created by Administrator on 2018/9/23.
 */

public class CMDInfoteinmentEngineVoice extends CMDInfoteinment {
    public CMDInfoteinmentEngineVoice(){
        super();
    }

    /**
     *
     * @param num 1~4
     */
    public void changeVoiceTo(int num) {
        payload[3] = (byte) num;
        refreshDataPayload();
    }
}

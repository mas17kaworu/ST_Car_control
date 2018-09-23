package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDInfoteinment;

/**
 * Created by Administrator on 2018/9/23.
 */

public class CMDInfoteinmentVoiceVolume extends CMDInfoteinment {


    /**
     *
     * @param volume 1~100
     */
    public void setVolume(int volume){
        payload[0] =(byte) volume;
        refreshDataPayload();
    }
}

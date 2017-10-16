package com.longkai.stcarcontrol.st_exp.communication;

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;

/**
 * Created by Administrator on 2017/8/14.
 */

public interface CommandListener {

    /**
     * Set the timestamp of send this command.
     * @param timeStamp millisecond of System time.
     */
    void setSendTimeStamp(long timeStamp);

    /**
     * Get the timestamp of send this command.
     * @return the timestamp of send this command.
     */
    long getSendTimestamp();

    /**
     * Called when command execute success.
     * @param response the response of command.
     */
    void onSuccess(BaseResponse response);

    /**
     * Called when command execute timeout. the default value of timeout is 2 second.
     */
    void onTimeout();

    /**
     * Occur some errors when command execute.
     * @param errorCode The reason of error.
     */
    void onError(int errorCode);

    public int getTimeout();
}
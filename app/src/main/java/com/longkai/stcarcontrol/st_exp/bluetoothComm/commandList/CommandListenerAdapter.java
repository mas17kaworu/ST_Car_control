package com.longkai.stcarcontrol.st_exp.bluetoothComm.commandList;


import com.longkai.stcarcontrol.st_exp.bluetoothComm.BTCommandListener;

/**
 * When a command be sending,a listener correspond to it.
 * The success and fail methods define into the listener.
 *
 * @author jw.gu
 * @version 1.0
 * @see BTCommandListener
 */
public class CommandListenerAdapter implements BTCommandListener {
    private long sendTimeStamp;

    /**
     * Set the timestamp of send this command.
     *
     * @param timeStamp millisecond of System time.
     */
    @Override
    public void setSendTimeStamp(long timeStamp) {
        sendTimeStamp = timeStamp;
    }

    /**
     * Get the timestamp of send this command.
     *
     * @return the timestamp of send this command.
     */
    @Override
    public long getSendTimestamp() {
        return sendTimeStamp;
    }

    /**
     * Called when command execute success.
     *
     * @param response the response of command.
     */
    @Override
    public void onSuccess(BaseBTResponse response) {
    }

    /**
     * Called when command execute timeout. the default value of timeout is 2 second.
     */
    @Override
    public void onTimeout() {

    }

    /**
     * Occur some errors when command execute.
     *
     * @param errorCode The reason of error.
     */
    @Override
    public void onError(int errorCode) {

    }

    @Override
    public int getTimeout() {
        return 1000;
    }
}

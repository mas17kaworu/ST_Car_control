package com.longkai.stcarcontrol.st_exp.communication;

/**
 * Created by lk.sun on 3/7/2016.
 * Report receive message.
 */
public interface MessageReceivedListener {
    /**
     * Called when received a completely message.
     * @param data The data buffer of read from protocol layer.
     * @param offset The offset of data.
     * @param length The length of received.
     */
    public void onReceive(byte[] data, int offset, int length);
}

package com.longkai.stcarcontrol.st_exp.communication;

/**
 * Report connection states
 * @author lk.sun
 * @version 1.0
 */
public interface ConnectionListener {
    /**
     * Called when connection was connected.
     */
    void onConnected();

    /**
     * Called when connection was disconnected.
     */
    void onDisconnected();

}
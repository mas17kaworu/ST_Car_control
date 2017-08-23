package com.longkai.stcarcontrol.st_exp.communication;

import android.os.Bundle;

/**
 * Created by lk.sun on 3/4/2016.
 * Transmission data interface.
 */
public interface ConnectionInterface {
    /**
     * Open connection, it must be executed success before transfer data.
     * @param parameter The configure of specific connection.
     * @param listener When connection status is changed, the status will be report to it.
     * @return true open connection success, otherwise open fail.
     */
    public boolean open(Bundle parameter, ConnectionListener listener);

    /**
     * Close connection.
     */
    public void close();

    /**
     * Write data to connection with blocking.
     * @param data write data
     * @return -1 write fail, >0 the number of write bytes.
     */
    public int writeDataBlock(byte[] data);

    /**
     * Set the listener of report when connection data arrived.
     * @param listener to be report read data.
     */
    public void setReceiveListener(MessageReceivedListener listener);
}

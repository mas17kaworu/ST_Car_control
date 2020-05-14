package com.longkai.stcarcontrol.st_exp.communication;


import android.os.Bundle;

/**
 * Created by Administrator on 2019/2/1.
 */

public class TestConnection implements ConnectionInterface {
    @Override
    public boolean open(Bundle parameter, ConnectionListener listener) {
        return false;
    }

    @Override
    public void close() {

    }

    @Override
    public int writeDataBlock(byte[] data) {
        return 0;
    }

    @Override
    public void setReceiveListener(MessageReceivedListener listener) {

    }
}

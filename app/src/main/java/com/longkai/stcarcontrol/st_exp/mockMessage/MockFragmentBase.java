package com.longkai.stcarcontrol.st_exp.mockMessage;

import android.os.Handler;

import com.longkai.stcarcontrol.st_exp.communication.ProtocolMessageDispatch;
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager;

/**
 * Created by Administrator on 2018/12/16.
 */

public abstract class MockFragmentBase implements Runnable {
    protected Handler handler;
    protected ProtocolMessageDispatch dispatcher;

    public MockFragmentBase(Handler handler){
        this.handler = handler;
        dispatcher = ServiceManager.getInstance().getMessageDispatcher();
    }
}

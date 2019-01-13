package com.longkai.stcarcontrol.st_exp.mockMessage;

import android.os.Handler;
import android.util.SparseArray;

import com.longkai.stcarcontrol.st_exp.communication.Command;
import com.longkai.stcarcontrol.st_exp.communication.ProtocolMessageDispatch;
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDFOTAList.CMDFOTADIAG;

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

    protected Command getExactCmd(Class<?> cls){
        SparseArray<Command> sentList = dispatcher.getSentCommandList();
        Command cmdReturn = null;
        for (int i = 0; i < sentList.size(); i++){
            Command tmpCmd = sentList.valueAt(i);
            if (tmpCmd.getClass().equals(cls)) {
                cmdReturn = tmpCmd;
            }
        }
        return cmdReturn;
    }
}

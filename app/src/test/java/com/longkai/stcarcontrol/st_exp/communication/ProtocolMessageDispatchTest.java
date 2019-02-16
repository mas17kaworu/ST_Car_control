package com.longkai.stcarcontrol.st_exp.communication;

import android.os.Bundle;

import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDFOTAList.CMDFOTADIAG;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter;

import org.junit.Before;
import org.junit.Test;


/**
 * Created by Administrator on 2019/2/1.
 */
public class ProtocolMessageDispatchTest {

    private ProtocolMessageDispatch dispatcher;

    @Before
    public void prepare(){
        dispatcher = new ProtocolMessageDispatch(new TestConnection());
    }

    @Test
    public void checkTimeoutCommand() throws Exception {
        dispatcher.registerRegularCommand(command, listener);
    }


    Command command = new CMDFOTADIAG();
    CommandListener listener = new CommandListenerAdapter();


}
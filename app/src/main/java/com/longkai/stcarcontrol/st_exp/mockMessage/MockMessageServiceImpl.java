package com.longkai.stcarcontrol.st_exp.mockMessage;

import android.os.Handler;
import android.os.HandlerThread;

import com.longkai.stcarcontrol.st_exp.fragment.VCUBMSFragment;
import com.longkai.stcarcontrol.st_exp.fragment.VCUMCUFragment;
import com.longkai.stcarcontrol.st_exp.mockMessage.MockFragmentList.VCUBMSFragmentMock;
import com.longkai.stcarcontrol.st_exp.mockMessage.MockFragmentList.VCUMCUFragmentMock;

/**
 * Created by Administrator on 2018/12/15.
 */

public class MockMessageServiceImpl implements MockMessageService {

    private static MockMessageServiceImpl instance;

    private static Handler doBackgroundHandler;

    public static MockMessageService getService() {
        if (instance == null){
            instance = new MockMessageServiceImpl();
            init();
        }
        return instance;
    }

    private static void init(){
        HandlerThread handlerThread = new HandlerThread("service-background-mockMessage-thread");
        handlerThread.start();
        doBackgroundHandler = new Handler(handlerThread.getLooper());
    }

    @Override
    public void StartService(String fragmentClass) {
        Runnable runnable = null;
        if (fragmentClass.equalsIgnoreCase(VCUMCUFragment.class.toString())){
            runnable = new VCUMCUFragmentMock(doBackgroundHandler);
        } else if (fragmentClass.equalsIgnoreCase(VCUBMSFragment.class.toString())){
            runnable = new VCUBMSFragmentMock(doBackgroundHandler);
        }
        doBackgroundHandler.post(runnable);
    }

    @Override
    public void StopService(String fragmentClass) {
        doBackgroundHandler.removeCallbacksAndMessages(null);
//        doBackgroundHandler.getLooper().quit();
    }
}

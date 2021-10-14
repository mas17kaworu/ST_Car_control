package com.longkai.stcarcontrol.st_exp.mockMessage;

import android.os.Handler;
import android.os.HandlerThread;

import com.longkai.stcarcontrol.st_exp.fragment.KeyCheckFragment;
import com.longkai.stcarcontrol.st_exp.fragment.KeyPairFragment;
import com.longkai.stcarcontrol.st_exp.fragment.NFCFragment;
import com.longkai.stcarcontrol.st_exp.fragment.VCUBMSFragment;
import com.longkai.stcarcontrol.st_exp.fragment.VCUMCUFragment;
import com.longkai.stcarcontrol.st_exp.fragment.VCUOBCDemoFragment;
import com.longkai.stcarcontrol.st_exp.fragment.VCUUpdateFirmwareFragment;
import com.longkai.stcarcontrol.st_exp.mockMessage.MockFragmentList.KeyCheckFragmentMock;
import com.longkai.stcarcontrol.st_exp.mockMessage.MockFragmentList.KeyPairFragmentMock;
import com.longkai.stcarcontrol.st_exp.mockMessage.MockFragmentList.NFCFragmentMock;
import com.longkai.stcarcontrol.st_exp.mockMessage.MockFragmentList.OBCReturnFragmentMock;
import com.longkai.stcarcontrol.st_exp.mockMessage.MockFragmentList.VCUBMSFragmentMock;
import com.longkai.stcarcontrol.st_exp.mockMessage.MockFragmentList.VCUMCUFragmentMock;
import com.longkai.stcarcontrol.st_exp.mockMessage.MockFragmentList.VCUUpdateFragmentMock;

import static com.longkai.stcarcontrol.st_exp.STCarApplication.inUIDebugMode;

/**
 * Created by Administrator on 2018/12/15.
 */

public class MockMessageServiceImpl implements MockMessageService {

  private static MockMessageServiceImpl instance;

  private static Handler doBackgroundHandler;

  public static MockMessageService getService() {
    if (instance == null) {
      instance = new MockMessageServiceImpl();
      init();
    }
    return instance;
  }

  private static void init() {
    HandlerThread handlerThread = new HandlerThread("service-background-mockMessage-thread");
    handlerThread.start();
    doBackgroundHandler = new Handler(handlerThread.getLooper());
  }

  @Override
  public void StartService(String fragmentClass) {
    if (inUIDebugMode) {
      Runnable runnable = null;
      if (fragmentClass.equalsIgnoreCase(VCUMCUFragment.class.toString())) {
        runnable = new VCUMCUFragmentMock(doBackgroundHandler);
      } else if (fragmentClass.equalsIgnoreCase(VCUBMSFragment.class.toString())) {
        runnable = new VCUBMSFragmentMock(doBackgroundHandler);
      } else if (fragmentClass.equalsIgnoreCase(VCUUpdateFirmwareFragment.class.toString())) {
        runnable = new VCUUpdateFragmentMock(doBackgroundHandler);
      } else if (fragmentClass.equalsIgnoreCase(NFCFragment.class.toString())) {
        runnable = new NFCFragmentMock(doBackgroundHandler);
      } else if (fragmentClass.equalsIgnoreCase(VCUOBCDemoFragment.class.toString())) {
        runnable = new OBCReturnFragmentMock(doBackgroundHandler);
      } else if (fragmentClass.equalsIgnoreCase(KeyPairFragment.class.toString())) {
        runnable = new KeyPairFragmentMock(doBackgroundHandler);
      } else if (fragmentClass.equalsIgnoreCase(KeyCheckFragment.class.toString())) {
        runnable = new KeyCheckFragmentMock(doBackgroundHandler);
      }
      doBackgroundHandler.post(runnable);
    }
  }

  @Override
  public void StopService(String fragmentClass) {
    doBackgroundHandler.removeCallbacksAndMessages(null);
    //        doBackgroundHandler.getLooper().quit();
  }
}

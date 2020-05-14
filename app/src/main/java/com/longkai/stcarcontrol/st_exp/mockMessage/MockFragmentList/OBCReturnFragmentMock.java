package com.longkai.stcarcontrol.st_exp.mockMessage.MockFragmentList;

import android.os.Handler;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDOBCDemoList.CMDOBCReturn;
import com.longkai.stcarcontrol.st_exp.communication.utils.CheckSumBit;
import com.longkai.stcarcontrol.st_exp.mockMessage.MockFragmentBase;

public class OBCReturnFragmentMock extends MockFragmentBase {
  private CMDOBCReturn.Response response = new CMDOBCReturn.Response();

  public OBCReturnFragmentMock(Handler handler) {
    super(handler);
  }

  @Override public void run() {
    case1();
    handler.removeCallbacksAndMessages(null);//remove all
    handler.postDelayed(this, 500); //500ms 循环
  }

  private void case1() {
    byte[] responseBytes = {
        0x5a, 0x3c, 0x12, 0x26,
        0x03,
        0x25,
        0x03,
        (byte)0xA4,
        0x0c,
        (byte)0xC6,
        0x0a,
        0x6D,
        0x02,
        0x3E,
        0x00//check
    };

    responseBytes[responseBytes.length-1] = CheckSumBit.checkSum(responseBytes, responseBytes.length - 1);
    dispatcher.onReceive(responseBytes, 0, responseBytes.length);
  }
}

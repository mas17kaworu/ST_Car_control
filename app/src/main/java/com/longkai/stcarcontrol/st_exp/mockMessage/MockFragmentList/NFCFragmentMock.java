package com.longkai.stcarcontrol.st_exp.mockMessage.MockFragmentList;

import android.os.Handler;
import android.util.Log;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDNFCList.CMDNFCReturn;
import com.longkai.stcarcontrol.st_exp.mockMessage.MockFragmentBase;

public class NFCFragmentMock extends MockFragmentBase {

  private CMDNFCReturn.Response response1 = new CMDNFCReturn.Response();

  public NFCFragmentMock(Handler handler) {
    super(handler);

  }

  @Override public void run() {
    Log.i("NFCFragmentMock", "NFCFragmentMock is running");
    response1.door_info = 1;
    response1.filter_info = 1;
    response1.key_info = 1;
    byte[] mockByte = response1.mockResponse();
    dispatcher.onReceive(mockByte, 0, mockByte.length);
    handler.removeCallbacksAndMessages(null);//remove all
    handler.postDelayed(this, 500);
  }
}

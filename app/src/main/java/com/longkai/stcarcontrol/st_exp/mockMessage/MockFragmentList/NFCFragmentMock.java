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

  private int responseCount = 0;

  @Override public void run() {
    case1();
    handler.removeCallbacksAndMessages(null);//remove all
    handler.postDelayed(this, 500); //500ms 循环
  }

  private void case1() {
    Log.i("NFCFragmentMock", "NFCFragmentMock is running");
    byte[] mockByte;
    if (responseCount < 10) {
      response1.door_info = 1;
      response1.filter_info = 1;
      response1.key_info = 1;
    } else if (responseCount < 12){
      response1.door_info = 2;
      response1.filter_info = 0;
      response1.key_info = 2;
    } else if (responseCount < 20) {
      response1.door_info = 1;
      response1.filter_info = 0;
      response1.key_info = 1;
    }
    mockByte = response1.mockResponse();
    responseCount++;
    dispatcher.onReceive(mockByte, 0, mockByte.length);
  }
}

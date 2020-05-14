package com.longkai.stcarcontrol.st_exp.mockMessage.MockFragmentList;

import android.os.Handler;
import android.util.Log;

import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUMCU1List.CMDVCUMCU1;
import com.longkai.stcarcontrol.st_exp.mockMessage.MockFragmentBase;

/**
 * Created by Administrator on 2018/12/15.
 */

public class VCUMCUFragmentMock extends MockFragmentBase {
    private CMDVCUMCU1.Response cmu1Rp = new CMDVCUMCU1.Response();

    public VCUMCUFragmentMock(Handler handler) {
        super(handler);
        cmu1Rp.Motor_Realtime_Speed = 1000;
        cmu1Rp.Temp_of_MCU = 200;
        cmu1Rp.Temp_of_Motor = -20;
        cmu1Rp.Torch_of_Motor = 200;
    }

    @Override
    public void run() {
        Log.i("VCUMCUFragmentMock", "VCUMCUFragmentMock is running");
        byte[] mockByte = cmu1Rp.mockResponse();
        dispatcher.onReceive(mockByte, 0, mockByte.length);
        handler.removeCallbacksAndMessages(null);//remove all
        handler.postDelayed(this, 500);
    }
}

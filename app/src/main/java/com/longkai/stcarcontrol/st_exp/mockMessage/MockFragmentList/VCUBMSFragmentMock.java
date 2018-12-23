package com.longkai.stcarcontrol.st_exp.mockMessage.MockFragmentList;

import android.os.Handler;
import android.util.Log;

import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUBMS1List.CMDVCUBMS1;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUMCU1List.CMDVCUMCU1;
import com.longkai.stcarcontrol.st_exp.mockMessage.MockFragmentBase;

/**
 * Created by Administrator on 2018/12/15.
 */

public class VCUBMSFragmentMock extends MockFragmentBase {
    private String TAG = "VCUBMSFragmentMock";

    private CMDVCUBMS1.Response response = new CMDVCUBMS1.Response();

    public VCUBMSFragmentMock(Handler handler) {
        super(handler);
        response.U_HighVoltage_3 = 70;
        response.U_HighVoltage_2 = 30;
        response.U_HighVoltage_1 = 5;
    }

    @Override
    public void run() {
        Log.i(TAG, TAG + " is running");
        byte[] mockByte = response.mockResponse();
        dispatcher.onReceive(mockByte, 0, mockByte.length);
        handler.removeCallbacksAndMessages(null);//remove all
        handler.postDelayed(this, 500);
    }
}

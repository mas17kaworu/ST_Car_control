package com.longkai.stcarcontrol.st_exp.mockMessage.MockFragmentList;

import android.os.Handler;
import android.util.Log;

import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUBMS1List.CMDVCUBMS1;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUBMS5List.CMDVCUBMS5;
import com.longkai.stcarcontrol.st_exp.mockMessage.MockFragmentBase;

/**
 * Created by Administrator on 2018/12/15.
 */

public class VCUBMSFragmentMock extends MockFragmentBase {
    private String TAG = "VCUBMSFragmentMock";

    private CMDVCUBMS1.Response BMS1Response = new CMDVCUBMS1.Response();
    private CMDVCUBMS5.Response BMS5Response = new CMDVCUBMS5.Response();

    public VCUBMSFragmentMock(Handler handler) {
        super(handler);
        BMS1Response.U_HighVoltage_3 = 70;
        BMS1Response.U_HighVoltage_2 = 30;
        BMS1Response.U_HighVoltage_1 = 5;

        BMS5Response.SOC = 20;
    }

    @Override
    public void run() {
        Log.i(TAG, TAG + " is running");
        byte[] mock1Byte = BMS1Response.mockResponse();
        byte[] mock2Byte = BMS5Response.mockResponse();

        dispatcher.onReceive(mock1Byte, 0, mock1Byte.length);
        dispatcher.onReceive(mock2Byte, 0, mock2Byte.length);
        handler.removeCallbacksAndMessages(null);//remove all
        handler.postDelayed(this, 500);
    }
}

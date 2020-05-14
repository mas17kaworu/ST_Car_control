package com.longkai.stcarcontrol.st_exp.mockMessage.MockFragmentList;

import android.os.Handler;
import android.util.Log;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDFOTAList.CMDFOTADATA;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDFOTAList.CMDFOTADIAG;
import com.longkai.stcarcontrol.st_exp.mockMessage.MockFragmentBase;

/**
 * Created by Administrator on 2019/1/13.
 */

public class VCUUpdateFragmentMock extends MockFragmentBase {

    private CMDFOTADIAG.Response diagResponse = new CMDFOTADIAG.Response();
    private CMDFOTADATA.Response dataResponse = new CMDFOTADATA.Response();


    private int responseNumber = 0;

    public VCUUpdateFragmentMock(Handler handler) {
        super(handler);
    }

    @Override
    public void run() {
        case1();
        handler.removeCallbacksAndMessages(null);//remove all
        handler.postDelayed(this, 500);
    }

    // Success
    private void case1(){
        CMDFOTADIAG cmd = (CMDFOTADIAG) getExactCmd(CMDFOTADIAG.class);

        if (cmd != null) {
            Log.i("longkai test", "longkai mock response : diagResponse.DIAG_CMD_RESPONSE  DIAGCMD = " + cmd.getDIAG_CMD());
            if (cmd.getDIAG_CMD() == 0x01 || cmd.getDIAG_CMD() == 0x02) {
                diagResponse.DIAG_CMD_RESPONSE = 0x01;
                responseNumber = 0;
            } else if (cmd.getDIAG_CMD() == 0) {
                if (responseNumber < 6) {
                    responseNumber++;
                    diagResponse.DIAG_CMD_RESPONSE = CMDFOTADIAG.Response.FLASHING;
                } else {
                    diagResponse.DIAG_CMD_RESPONSE = CMDFOTADIAG.Response.FLASH_FINISHED;
                }
            }
            byte[] mockByte = diagResponse.mockResponse();
            dispatcher.onReceive(mockByte, 0, mockByte.length);
        }

        CMDFOTADATA cmd2 = (CMDFOTADATA) getExactCmd(CMDFOTADATA.class);
        if (cmd2 != null) {
            Log.i("longkai test", "longkai mock response : diagResponse.CMDFOTADATA Num = " + cmd2.getPKG_NUM());

            dataResponse.isLastPackage = cmd2.getLAST_PKG() == 1;
            dataResponse.PKG_NUM = cmd2.getPKG_NUM();
            dataResponse.PKG_Received = 1;
            dataResponse.PKG_Aborted = 0;

            byte[] mockByte = dataResponse.mockResponse();
            dispatcher.onReceive(mockByte, 0, mockByte.length);
        }

    }

    //failed
    private void case2(){
        CMDFOTADIAG cmd = (CMDFOTADIAG) getExactCmd(CMDFOTADIAG.class);

        if (cmd != null) {
            Log.i("longkai test", "longkai mock response : diagResponse.DIAG_CMD_RESPONSE");

            diagResponse.DIAG_CMD_RESPONSE = 0x03;
            byte[] mockByte = diagResponse.mockResponse();
            dispatcher.onReceive(mockByte, 0, mockByte.length);
        }
    }
}

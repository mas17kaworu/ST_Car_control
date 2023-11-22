package com.longkai.stcarcontrol.st_exp.mockMessage.MockFragmentList

import android.os.Handler
import android.util.Log
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDKeyPairList.CMDKeyCheck
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCU.CMDDFAResponse
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCU.CMDResponse
import com.longkai.stcarcontrol.st_exp.mockMessage.MockFragmentBase
import kotlin.random.Random

class CarInfoFragmentMock(handler: Handler?) : MockFragmentBase(handler){
    private final var TAG ="CarInfoFragmentMock";
    override fun run() {
        case1()
        handler.removeCallbacksAndMessages(null) //remove all
        handler.postDelayed(this, 1000) //500ms 循环
    }

    private fun case1() {
        Log.i("CarInfoFragmentMock", "CarInfoFragmentMock is running ")
        val response = CMDDFAResponse(1)
        val mockByte = response.mockResponse()
        dispatcher.onReceive(mockByte, 0, mockByte.size)
    }
}
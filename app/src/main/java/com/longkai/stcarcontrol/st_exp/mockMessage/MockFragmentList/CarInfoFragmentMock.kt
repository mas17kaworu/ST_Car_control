package com.longkai.stcarcontrol.st_exp.mockMessage.MockFragmentList

import android.os.Handler
import android.util.Log
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCU.CMDResponse
import com.longkai.stcarcontrol.st_exp.mockMessage.MockFragmentBase

class CarInfoFragmentMock(handler: Handler?) : MockFragmentBase(handler){
    private final var TAG ="CarInfoFragmentMock";
    override fun run() {
        runLooper()
    }

    private fun runLooper(){
//        while (true){
//            Thread.sleep(300)
//            var response =CMDResponse(ByteArray(1))
//            var mockByte = response.mockResponse()
//            dispatcher.onReceive(mockByte, 0, mockByte.size)
//            Log.d(TAG,"runLooper")
//        }
    }
}
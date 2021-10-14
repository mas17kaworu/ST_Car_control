package com.longkai.stcarcontrol.st_exp.mockMessage.MockFragmentList

import android.os.Handler
import android.util.Log
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDFOTAList.CMDFOTADIAG
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDKeyPairList.CMDKeyCheck
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDKeyPairList.CMDKeyPairStart
import com.longkai.stcarcontrol.st_exp.mockMessage.MockFragmentBase
import kotlin.random.Random

class KeyCheckFragmentMock(handler: Handler?) : MockFragmentBase(handler) {
    override fun run() {
        case1()
        handler.removeCallbacksAndMessages(null) //remove all
        handler.postDelayed(this, 5000) //500ms 循环
    }

    private fun case1() {
        val status = Random.nextInt(0, 2).toByte()
        Log.i("KeyCheckFragmentMock", "KeyCheckFragmentMock is running, status: $status")
        val response = CMDKeyCheck.Response(status)
        val mockByte = response.mockResponse()
        dispatcher.onReceive(mockByte, 0, mockByte.size)
    }
}
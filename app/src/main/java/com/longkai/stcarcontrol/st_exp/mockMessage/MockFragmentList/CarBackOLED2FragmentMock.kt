package com.longkai.stcarcontrol.st_exp.mockMessage.MockFragmentList

import android.os.Handler
import androidx.core.os.postDelayed
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDOLEDBackList.CMDOLED2
import com.longkai.stcarcontrol.st_exp.mockMessage.MockFragmentBase

class CarBackOLED2FragmentMock(handler: Handler?) : MockFragmentBase(handler) {
    override fun run() {
        case1()
        handler.removeCallbacksAndMessages(null) //remove all
//        handler.postDelayed(this)
    }

    private fun case1() {
        while (true) {
            getExactCmd(CMDOLED2::class.java) as CMDOLED2? ?: return

            var payload = 0x01.toUByte().toByte()
            var response = CMDOLED2.Response(payload)
            var mockByte = response.mockResponse()
            dispatcher.onReceive(mockByte, 0, mockByte.size)

            Thread.sleep(3000)

            payload = 0x02.toUByte().toByte()
            response = CMDOLED2.Response(payload)
            mockByte = response.mockResponse()
            dispatcher.onReceive(mockByte, 0, mockByte.size)

            Thread.sleep(3000)

            payload = 0x04.toUByte().toByte()
            response = CMDOLED2.Response(payload)
            mockByte = response.mockResponse()
            dispatcher.onReceive(mockByte, 0, mockByte.size)

            Thread.sleep(3000)

            payload = 0x08.toUByte().toByte()
            response = CMDOLED2.Response(payload)
            mockByte = response.mockResponse()
            dispatcher.onReceive(mockByte, 0, mockByte.size)

            Thread.sleep(3000)

            payload = 0x10.toUByte().toByte()
            response = CMDOLED2.Response(payload)
            mockByte = response.mockResponse()
            dispatcher.onReceive(mockByte, 0, mockByte.size)

            Thread.sleep(3000)
        }
    }
}
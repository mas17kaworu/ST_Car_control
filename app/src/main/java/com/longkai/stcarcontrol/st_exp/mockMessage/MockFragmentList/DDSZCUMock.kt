package com.longkai.stcarcontrol.st_exp.mockMessage.MockFragmentList

import android.os.Handler
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDZCU.CMDZCU
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDZCU.CMDZCU.LinkStatus
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDZCU.CMDZCUEfuse
import com.longkai.stcarcontrol.st_exp.mockMessage.MockFragmentBase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DDSZCUMock(handler: Handler?) : MockFragmentBase(handler) {
    override fun run() {
//        case1()
        case2()
        handler.removeCallbacksAndMessages(null) //remove all
    }

    fun case1() {
        CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                var firstResponse = CMDZCUEfuse.Response(
                    current = 0f,
                    voltage = 5f,
                    tempDevice = 10f,
                    tempMos = 20f,
                    loadStatus = CMDZCU.LinkStatus.Fail,
                )
                var firstByte = firstResponse.mockResponse()
                dispatcher.onReceive(firstByte, 0, firstByte.size)
                delay(200)

                val secondResponse = CMDZCUEfuse.Response(
                    current = 5f,
                    voltage = 10f,
                    tempDevice = 20f,
                    tempMos = 25f,
                    loadStatus = CMDZCU.LinkStatus.OK,
                )
                val secondByte = secondResponse.mockResponse()
                dispatcher.onReceive(secondByte, 0, secondByte.size)
                delay(200)

                firstResponse = CMDZCUEfuse.Response(
                    current = 3f,
                    voltage = 15f,
                    tempDevice = 18f,
                    tempMos = 30f,
                    loadStatus = CMDZCU.LinkStatus.Fail,
                )
                firstByte = firstResponse.mockResponse()
                dispatcher.onReceive(firstByte, 0, firstByte.size)
                delay(200)
            }
        }
    }

    fun case2() {
        CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                var firstResponse = CMDZCU.Response(
                    link1Status = LinkStatus.OK,
                    link2Status = LinkStatus.OK,
                    link3Status = LinkStatus.OK,
                    link4Status = LinkStatus.OK,
                )
                var firstBytes = firstResponse.mockResponse()
                dispatcher.onReceive(firstBytes, 0, firstBytes.size)
                delay(1000)

                val secondResponse = CMDZCU.Response(
                    link1Status = LinkStatus.Fail,
                    link2Status = LinkStatus.Fail,
                    link3Status = LinkStatus.OK,
                    link4Status = LinkStatus.OK,
                )
                val secondByte = secondResponse.mockResponse()
                dispatcher.onReceive(secondByte, 0, secondByte.size)
                delay(8000)

                firstResponse = CMDZCU.Response(
                    link1Status = LinkStatus.OK,
                    link2Status = LinkStatus.OK,
                    link3Status = LinkStatus.Fail,
                    link4Status = LinkStatus.Fail,
                )
                firstBytes = firstResponse.mockResponse()
                dispatcher.onReceive(firstBytes, 0, firstBytes.size)
                delay(3000)

                firstResponse = CMDZCU.Response(
                    link1Status = LinkStatus.Fail,
                    link2Status = LinkStatus.Fail,
                    link3Status = LinkStatus.Fail,
                    link4Status = LinkStatus.Fail,
                )
                firstBytes = firstResponse.mockResponse()
                dispatcher.onReceive(firstBytes, 0, firstBytes.size)
                delay(1000)
            }
        }
    }

    fun case3() {
        CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                var firstResponse = CMDZCU.Response(
                    link1Status = LinkStatus.OK,
                    link2Status = LinkStatus.OK,
                    link3Status = LinkStatus.OK,
                    link4Status = LinkStatus.OK,
                )
                var firstBytes = firstResponse.mockResponse()
                dispatcher.onReceive(firstBytes, 0, firstBytes.size)
                delay(3000)

                val secondResponse = CMDZCU.Response(
                    link1Status = LinkStatus.Fail,
                    link2Status = LinkStatus.Fail,
                    link3Status = LinkStatus.OK,
                    link4Status = LinkStatus.OK,
                )
                val secondByte = secondResponse.mockResponse()
                dispatcher.onReceive(secondByte, 0, secondByte.size)
                delay(3000)

                firstResponse = CMDZCU.Response(
                    link1Status = LinkStatus.OK,
                    link2Status = LinkStatus.OK,
                    link3Status = LinkStatus.Fail,
                    link4Status = LinkStatus.Fail,
                )
                firstBytes = firstResponse.mockResponse()
                dispatcher.onReceive(firstBytes, 0, firstBytes.size)
                delay(1000)
            }
        }
    }
}
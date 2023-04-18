package com.longkai.stcarcontrol.st_exp.mockMessage.MockFragmentList

import android.content.Context
import android.os.Handler
import android.util.Log
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPBox.CMDPBox
import com.longkai.stcarcontrol.st_exp.mockMessage.MockFragmentBase
import kotlinx.coroutines.*
import kotlin.random.Random

class CommandPBoxMock(handler: Handler?, val context: Context) : MockFragmentBase(handler) {
    override fun run() {
        case1()
    }

    private fun case1() {
        Log.i("CommandPBoxMock", "case1")
        getExactCmd(CMDPBox::class.java) as CMDPBox? ?: return

        val pboxReader = context.resources.assets.open("trackingTest/pbox.txt").bufferedReader()
        val realReader = context.resources.assets.open("trackingTest/real.txt").bufferedReader()

        CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                if (isStopped) break

                var pboxLine: String?
                var realLine: String?

                withContext(Dispatchers.IO) {
                    pboxLine = pboxReader.readLine()
                    realLine = realReader.readLine()
                }

                if (pboxLine == null && realLine == null) break

                pboxLine?.let {
                    val response = CMDPBox.Response(CMDPBox.DataType.PBox, it)
                    val mockByte = response.mockResponse()
                    dispatcher.onReceive(mockByte, 0, mockByte.size)
                }

                realLine?.let {
                    val response = CMDPBox.Response(CMDPBox.DataType.Real, it)
                    val mockByte = response.mockResponse()
                    dispatcher.onReceive(mockByte, 0, mockByte.size)
                }

                delay(100)
            }
        }
    }
}

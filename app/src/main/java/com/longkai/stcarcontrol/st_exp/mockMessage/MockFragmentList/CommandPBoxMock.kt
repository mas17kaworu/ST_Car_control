package com.longkai.stcarcontrol.st_exp.mockMessage.MockFragmentList

import android.content.Context
import android.os.Handler
import android.util.Log
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPBox.CMDPBox
import com.longkai.stcarcontrol.st_exp.communication.utils.CheckSumBit
import com.longkai.stcarcontrol.st_exp.mockMessage.MockFragmentBase
import kotlinx.coroutines.*

class CommandPBoxMock(handler: Handler?, val context: Context) : MockFragmentBase(handler) {
    override fun run() {
        case1()
    }


    //



    private fun case1() {
        Log.i("CommandPBoxMock", "case1")
        getExactCmd(CMDPBox::class.java) as CMDPBox? ?: return

        val pboxReader = context.resources.assets.open("trackingTest/pbox.txt").bufferedReader()
        val realReader = context.resources.assets.open("trackingTest/real.txt").bufferedReader()


        var responseBytes = byteArrayOf(
//            0x5a.toUByte().toByte(), 0x3c.toUByte().toByte(), 0xfd.toUByte().toByte(), 0x33.toUByte().toByte(), 0x01.toUByte().toByte(), 0x2a.toUByte().toByte(), 0x33.toUByte().toByte(), 0x44.toUByte().toByte(), 0x0d.toUByte().toByte(), 0x0a.toUByte().toByte(), 0x24.toUByte().toByte(), 0x47.toUByte().toByte(), 0x4e.toUByte().toByte(), 0x47.toUByte().toByte(), 0x53.toUByte().toByte(), 0x41.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x41.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x33.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x30.toUByte().toByte(), 0x32.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x30.toUByte().toByte(), 0x33.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x30.toUByte().toByte(), 0x37.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x31.toUByte().toByte(), 0x2e.toUByte().toByte(), 0x32.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x30.toUByte().toByte(), 0x2e.toUByte().toByte(), 0x36.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x31.toUByte().toByte(), 0x2e.toUByte().toByte(), 0x30.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x35.toUByte().toByte(), 0x2a.toUByte().toByte(), 0x33.toUByte().toByte(), 0x37.toUByte().toByte(), 0x0d.toUByte().toByte(), 0x0a.toUByte().toByte(), 0x24.toUByte().toByte(), 0x47.toUByte().toByte(), 0x50.toUByte().toByte(), 0x47.toUByte().toByte(), 0x53.toUByte().toByte(), 0x56.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x32.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x31.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x30.toUByte().toByte(), 0x36.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x30.toUByte().toByte(), 0x31.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x36.toUByte().toByte(), 0x36.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x30.toUByte().toByte(), 0x34.toUByte().toByte(), 0x31.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x34.toUByte().toByte(), 0x38.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x30.toUByte().toByte(), 0x33.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x32.toUByte().toByte(), 0x36.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x31.toUByte().toByte(), 0x33.toUByte().toByte(), 0x35.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x33.toUByte().toByte(), 0x39.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x30.toUByte().toByte(), 0x37.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x35.toUByte().toByte(), 0x32.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x32.toUByte().toByte(), 0x30.toUByte().toByte(), 0x31.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x34.toUByte().toByte(), 0x35.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x31.toUByte().toByte(), 0x34.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x34.toUByte().toByte(), 0x31.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x33.toUByte().toByte(), 0x33.toUByte().toByte(), 0x31.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x34.toUByte().toByte(), 0x34.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x30.toUByte().toByte(), 0x2a.toUByte().toByte(), 0x36.toUByte().toByte(), 0x31.toUByte().toByte(), 0x0d.toUByte().toByte(), 0x0a.toUByte().toByte(), 0x24.toUByte().toByte(), 0x47.toUByte().toByte(), 0x50.toUByte().toByte(), 0x47.toUByte().toByte(), 0x53.toUByte().toByte(), 0x56.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x32.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x32.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x30.toUByte().toByte(), 0x36.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x31.toUByte().toByte(), 0x37.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x33.toUByte().toByte(), 0x33.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x32.toUByte().toByte(), 0x38.toUByte().toByte(), 0x37.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x34.toUByte().toByte(), 0x30.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x33.toUByte().toByte(), 0x30.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x35.toUByte().toByte(), 0x36.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x32.toUByte().toByte(), 0x36.toUByte().toByte(), 0x36.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x34.toUByte().toByte(), 0x36.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x30.toUByte().toByte(), 0x2a.toUByte().toByte(), 0x36.toUByte().toByte(), 0x43.toUByte().toByte(), 0x0d.toUByte().toByte(), 0x0a.toUByte().toByte(), 0x24.toUByte().toByte(), 0x47.toUByte().toByte(), 0x41.toUByte().toByte(), 0x47.toUByte().toByte(), 0x53.toUByte().toByte(), 0x56.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x32.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x31.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x30.toUByte().toByte(), 0x37.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x30.toUByte().toByte(), 0x32.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x33.toUByte().toByte(), 0x32.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x33.toUByte().toByte(), 0x32.toUByte().toByte(), 0x36.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x34.toUByte().toByte(), 0x32.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x31.toUByte().toByte(), 0x30.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x32.toUByte().toByte(), 0x35.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x30.toUByte().toByte(), 0x33.toUByte().toByte(), 0x36.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x34.toUByte().toByte(), 0x30.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x31.toUByte().toByte(), 0x31.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x35.toUByte().toByte(), 0x33.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x30.toUByte().toByte(), 0x33.toUByte().toByte(), 0x35.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x34.toUByte().toByte(), 0x33.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x32.toUByte().toByte(), 0x34.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x33.toUByte().toByte(), 0x31.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x31.toUByte().toByte(), 0x31.toUByte().toByte(), 0x32.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x34.toUByte().toByte(), 0x32.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x30.toUByte().toByte(), 0x2a.toUByte().toByte(), 0x37.toUByte().toByte(), 0x32.toUByte().toByte(), 0x0d.toUByte().toByte(), 0x0a.toUByte().toByte(), 64.toUByte().toByte()
            0x5a.toUByte().toByte(), 0x3c.toUByte().toByte(), 0x90.toUByte().toByte(), 0x33.toUByte().toByte(), 0x02.toUByte().toByte(), 0x24.toUByte().toByte(), 0x47.toUByte().toByte(), 0x50.toUByte().toByte(), 0x47.toUByte().toByte(), 0x47.toUByte().toByte(), 0x41.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x31.toUByte().toByte(), 0x30.toUByte().toByte(), 0x33.toUByte().toByte(), 0x31.toUByte().toByte(), 0x35.toUByte().toByte(), 0x31.toUByte().toByte(), 0x2e.toUByte().toByte(), 0x30.toUByte().toByte(), 0x30.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x32.toUByte().toByte(), 0x32.toUByte().toByte(), 0x33.toUByte().toByte(), 0x32.toUByte().toByte(), 0x2e.toUByte().toByte(), 0x35.toUByte().toByte(), 0x34.toUByte().toByte(), 0x30.toUByte().toByte(), 0x32.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x4e.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x31.toUByte().toByte(), 0x31.toUByte().toByte(), 0x33.toUByte().toByte(), 0x35.toUByte().toByte(), 0x36.toUByte().toByte(), 0x2e.toUByte().toByte(), 0x38.toUByte().toByte(), 0x32.toUByte().toByte(), 0x36.toUByte().toByte(), 0x30.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x45.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x31.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x33.toUByte().toByte(), 0x34.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x30.toUByte().toByte(), 0x2e.toUByte().toByte(), 0x35.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x39.toUByte().toByte(), 0x30.toUByte().toByte(), 0x2e.toUByte().toByte(), 0x36.toUByte().toByte(), 0x38.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x4d.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x2d.toUByte().toByte(), 0x33.toUByte().toByte(), 0x2e.toUByte().toByte(), 0x34.toUByte().toByte(), 0x30.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x4d.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x2a.toUByte().toByte(), 0x34.toUByte().toByte(), 0x41.toUByte().toByte(), 0x0d.toUByte().toByte(), 0x0a.toUByte().toByte(), 0x24.toUByte().toByte(), 0x47.toUByte().toByte(), 0x50.toUByte().toByte(), 0x52.toUByte().toByte(), 0x4d.toUByte().toByte(), 0x43.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x31.toUByte().toByte(), 0x30.toUByte().toByte(), 0x33.toUByte().toByte(), 0x31.toUByte().toByte(), 0x35.toUByte().toByte(), 0x31.toUByte().toByte(), 0x2e.toUByte().toByte(), 0x30.toUByte().toByte(), 0x30.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x41.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x32.toUByte().toByte(), 0x32.toUByte().toByte(), 0x33.toUByte().toByte(), 0x32.toUByte().toByte(), 0x2e.toUByte().toByte(), 0x35.toUByte().toByte(), 0x34.toUByte().toByte(), 0x30.toUByte().toByte(), 0x32.toUByte().toByte(), 0x34.toUByte().toByte(), 0x35.toUByte().toByte(), 0x31.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x4e.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x31.toUByte().toByte(), 0x31.toUByte().toByte(), 0x33.toUByte().toByte(), 0x35.toUByte().toByte(), 0x36.toUByte().toByte(), 0x2e.toUByte().toByte(), 0x38.toUByte().toByte(), 0x32.toUByte().toByte(), 0x35.toUByte().toByte(), 0x39.toUByte().toByte(), 0x35.toUByte().toByte(), 0x36.toUByte().toByte(), 0x30.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x45.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x30.toUByte().toByte(), 0x2e.toUByte().toByte(), 0x30.toUByte().toByte(), 0x32.toUByte().toByte(), 0x37.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x38.toUByte().toByte(), 0x35.toUByte().toByte(), 0x2e.toUByte().toByte(), 0x38.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x30.toUByte().toByte(), 0x35.toUByte().toByte(), 0x30.toUByte().toByte(), 0x35.toUByte().toByte(), 0x32.toUByte().toByte(), 0x33.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x30.toUByte().toByte(), 0x2e.toUByte().toByte(), 0x30.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x45.toUByte().toByte(), 0x2c.toUByte().toByte(), 0x41.toUByte().toByte(), 0x2a.toUByte().toByte(), 0x30.toUByte().toByte(), 0x34.toUByte().toByte()
        )

        val verifyChecksum = CheckSumBit.checkSum(responseBytes, responseBytes.size - 1)
        println("length: ${responseBytes.size}")
        println("verify checksum: $verifyChecksum")
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
//                    val response = CMDPBox.Response(CMDPBox.DataType.PBox, it)
//                    val mockByte = response.mockResponse()
//                    dispatcher.onReceive(mockByte, 0, mockByte.size)
                    dispatcher.onReceive(responseBytes, 0, responseBytes.size)
                }

//                realLine?.let {
//                    val response = CMDPBox.Response(CMDPBox.DataType.Real, it)
//                    val mockByte = response.mockResponse()
//                    dispatcher.onReceive(mockByte, 0, mockByte.size)
//                }

                delay(100)
            }
        }
    }
}

package com.longkai.stcarcontrol.st_exp.compose.data.dds.test

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.os.postDelayed
import com.longkai.stcarcontrol.st_exp.compose.data.dds.fakeAvasActions
import com.longkai.stcarcontrol.st_exp.compose.data.dds.fakeOledActions
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.toAvasTopicData
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.toOledTopicData
import com.longkai.stcarcontrol.st_exp.compose.data.dds.service.DdsService

class MockDdsService(private val context: Context): DdsService {

    override fun start() {    }

    override fun registerTopicListener(listener: DdsService.TopicListener) {
        Handler(Looper.getMainLooper()).postDelayed(2000) {
            // Log.i(TAG_DDS, "Send DDS mock data")
            ScreenLog.log( "Send DDS mock data 1 ")
            listener.onAvasDataAvailable(fakeAvasActions.toAvasTopicData())
            listener.onOledDataAvailable(fakeOledActions.toOledTopicData())
            listener.onDigitalKeyStateChanged(true)
        }

        Handler(Looper.getMainLooper()).postDelayed(4000) {
            // Log.i(TAG_DDS, "Send DDS mock data")
            ScreenLog.log( "Send DDS mock data 2")
            listener.onAvasDataAvailable(fakeAvasActions.toAvasTopicData())
            listener.onOledDataAvailable(fakeOledActions.toOledTopicData())
            listener.onDigitalKeyStateChanged(true)
        }

        Handler(Looper.getMainLooper()).postDelayed(6000) {
            // Log.i(TAG_DDS, "Send DDS mock data")
            ScreenLog.log( "Send DDS mock data 3")
            listener.onAvasDataAvailable(fakeAvasActions.toAvasTopicData())
            listener.onOledDataAvailable(fakeOledActions.toOledTopicData())
            listener.onDigitalKeyStateChanged(true)
        }
    }

    override fun unregisterTopicListener() { }

    override fun sendAvasAction(data: ByteArray) {
        ScreenLog.log( "Avas action executed: ${data.toDebugString()}")
        // Log.i(TAG_DDS, "Avas action executed: ${data.toDebugString()}")
    }

    override fun sendOledAction(data: ByteArray) {
        Log.i(TAG_DDS, "Oled action executed: ${data.toDebugString()}")
    }
}

fun ByteArray.toDebugString() = this.joinToString(" ") { it.toString() }

const val TAG_DDS = "DDS"
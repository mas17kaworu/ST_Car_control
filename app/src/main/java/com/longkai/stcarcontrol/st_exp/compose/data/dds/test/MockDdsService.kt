package com.longkai.stcarcontrol.st_exp.compose.data.dds.test

import android.content.Context
import com.longkai.stcarcontrol.st_exp.compose.data.dds.fakeAvasActions
import com.longkai.stcarcontrol.st_exp.compose.data.dds.fakeOledActions
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.toAvasTopicData
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.toOledTopicData
import com.longkai.stcarcontrol.st_exp.compose.data.dds.service.DdsService

class MockDdsService(private val context: Context): DdsService {

    override fun start() {    }

    override fun registerTopicListener(listener: DdsService.TopicListener) {
        listener.onAvasDataAvailable(fakeAvasActions.toAvasTopicData())
        listener.onOledDataAvailable(fakeOledActions.toOledTopicData())
        listener.onDigitalKeyStateChanged(true)
    }

    override fun unregisterTopicListener() { }

    override fun sendAvasAction(data: ByteArray) {
        println("Avas action executed: ${data.toDebugString()}")
    }

    override fun sendOledAction(data: ByteArray) {
        println("Oled action executed: ${data.toDebugString()}")
    }
}

fun ByteArray.toDebugString() = this.joinToString(" ") { it.toString() }
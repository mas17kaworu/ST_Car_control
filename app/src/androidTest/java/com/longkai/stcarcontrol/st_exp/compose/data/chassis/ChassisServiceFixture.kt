package com.longkai.stcarcontrol.st_exp.compose.data.chassis

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.test.platform.app.InstrumentationRegistry
import com.longkai.stcarcontrol.st_exp.communication.CommunicationServer
import com.longkai.stcarcontrol.st_exp.communication.ConnectionInterface
import com.longkai.stcarcontrol.st_exp.communication.ConnectionListener
import com.longkai.stcarcontrol.st_exp.communication.MessageReceivedListener
import com.longkai.stcarcontrol.st_exp.communication.ProtocolMessageDispatch
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDChassisList.CMDChassisReport
import com.longkai.stcarcontrol.st_exp.communication.utils.CheckSumBit
import java.nio.ByteBuffer
import java.nio.ByteOrder

/** Uses the real Binder/dispatcher with memory-only I/O, without starting the Android service. */
class ChassisServiceFixture : AutoCloseable {
    val manager: ServiceManager = ServiceManager.getInstance()
    val writes = mutableListOf<ByteArray>()
    private val handler = Handler(Looper.getMainLooper())
    private val dispatcher = ProtocolMessageDispatch(object : ConnectionInterface {
        override fun open(parameter: Bundle?, listener: ConnectionListener?): Boolean =
            error("The chassis test must not open a device connection")

        override fun close() {}
        override fun setReceiveListener(listener: MessageReceivedListener) {}
        override fun writeDataBlock(data: ByteArray): Int {
            writes += data.copyOf()
            return data.size
        }
    })
    private val binderField = ServiceManager::class.java.getDeclaredField("binder").apply {
        isAccessible = true
    }
    private val server = CommunicationServer()
    private val binder = server.onBind(null)

    val registeredCommandCount: Int get() = dispatcher.sentCommandList.size()

    init {
        onMain {
            check(binderField.get(manager) == null) { "An existing service must not be replaced by a test" }
            // Keep test wiring out of the production service API.
            CommunicationServer::class.java.getDeclaredField("mMessageHandler").apply {
                isAccessible = true
                set(server, dispatcher)
            }
            CommunicationServer::class.java.getDeclaredField("doBackgroundHandler").apply {
                isAccessible = true
                set(server, handler)
            }
            CommunicationServer::class.java.getDeclaredField("mConnectionListenerList").apply {
                isAccessible = true
                set(server, mutableListOf<ConnectionListener>())
            }
            binderField.set(manager, binder)
        }
    }

    fun receiveReport(response: CMDChassisReport.Response) {
        val frame = ByteBuffer.allocate(21).order(ByteOrder.LITTLE_ENDIAN)
            .put(0x5A.toByte()).put(0x3C.toByte()).put(0x12.toByte()).put(0x3E.toByte())
            .putInt(response.speedRaw.toInt()).putInt(response.steeringRaw)
            .putInt(response.ehbForceRaw.toInt()).putInt(response.embForceRaw.toInt())
            .put(0.toByte()).array()
        frame[20] = CheckSumBit.checkSum(frame, 20)
        receiveFrame(frame)
    }

    fun receiveFrame(frame: ByteArray) = onMain { dispatcher.onReceive(frame, 0, frame.size) }

    fun awaitIdle() = InstrumentationRegistry.getInstrumentation().waitForIdleSync()

    override fun close() = onMain {
        check(binderField.get(manager) === binder)
        binderField.set(manager, null)
        handler.removeCallbacksAndMessages(null)
    }

    private fun onMain(block: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) block()
        else InstrumentationRegistry.getInstrumentation().runOnMainSync { block() }
    }
}

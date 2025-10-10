package com.longkai.stcarcontrol.st_exp.compose.data.dds.service

import android.content.Context
import android.net.wifi.WifiManager
import android.util.Log
import com.example.dds_api_ndk.*
import com.longkai.stcarcontrol.st_exp.compose.data.dds.test.ScreenLog
import com.longkai.stcarcontrol.st_exp.compose.data.dds.test.toDebugString
import java.math.BigInteger
import java.net.InetAddress
import java.net.UnknownHostException
import java.nio.ByteOrder
import java.nio.charset.Charset

interface DdsService {

    enum class DigitalKeyState {
        LockDoor, UnlockDoor, Reset
    }

    interface TopicListener {
        fun onAvasDataAvailable(topicData: TopicData)
        fun onOledDataAvailable(topicData: TopicData)
        /**
         * @param state
         * 1 -- unlock door;
         * 2 -- lock door;
         * others -- clear state;
         */
        fun onDigitalKeyStateChanged(keyState: DigitalKeyState)
    }

    fun start()
    fun registerTopicListener(listener: TopicListener)
    fun unregisterTopicListener()
    fun sendAvasAction(data: ByteArray)
    fun sendOledAction(data: ByteArray)
}

class DdsServiceImpl(private val context: Context): DdsService {
    private val TAG = this.javaClass.simpleName

    private lateinit var avasReader: JavaDataReader
    private lateinit var oledReader: JavaDataReader
    private lateinit var dkeyReader: JavaDataReader

    private lateinit var avasWriter: JavaDataWriter
    private lateinit var oledWriter: JavaDataWriter

    private var hasStarted: Boolean = false

    private var topicListener: DdsService.TopicListener? = null

    override fun registerTopicListener(listener: DdsService.TopicListener) {
        this.topicListener = listener
    }

    override fun unregisterTopicListener() {
        this.topicListener = null
    }

    override fun sendAvasAction(data: ByteArray) {
        // Log.i(TAG, "sendAvasAction: ${data.toDebugString()}")
        // ScreenLog.log("sendAvasAction: ${data.toDebugString()}")
        this.avasWriter.sendData(data)
    }

    override fun sendOledAction(data: ByteArray) {
        // Log.i(TAG, "sendOledAction: ${data.toDebugString()}")
        // ScreenLog.log(TAG, "sendOledAction: ${data.toDebugString()}")
        try {
            this.oledWriter.sendData(data)
        } catch (e: Exception) {
            ScreenLog.log(TAG, e.message ?: "sendOledAction Exception")
        }
    }

    override fun start() {
        if (hasStarted) return

        val wifiIp = wifiIpAddress(context.applicationContext)
        init(DomainId, wifiIp, DiscoverIp, Guid)
    }

    /* SDK层创建的所有的对象都需要自己进行管理 */
    private fun init(domainId: Long, ip: String?, discoverIp: String, guid: ByteArray?): Long {
        ScreenLog.log("init domainId $domainId ip $ip guid ${guid?.printArrayInInt()}")
        if (ip == null) {
            ScreenLog.log("ipAddress is null, check your wifi connection")
            return -1
        }
        val domainParticipant =
            JavaDomainParticipant.CreateJavaDomainparticipant(domainId, ip, discoverIp, guid)
        if (null == domainParticipant) {
            ScreenLog.log("Create JavaDomainParticipant error")
            return 1
        }

        val subscriber = domainParticipant.CreateJavaSubscriber()
        if (null == subscriber) {
            ScreenLog.log("Create JavaSubscriber error")
            return 2
        }

        val avasReader = createReader(domainParticipant, subscriber, ReaderTopic.AvasService.topicName) { strData ->
            ScreenLog.log(" onAvasDataAvailable ${strData.printArrayInInt()}")
            try {
                val topicData = parseTopicData(strData)
                topicListener?.onAvasDataAvailable(topicData)
            } catch (e: Throwable) {
                ScreenLog.log("parse AvasData error")
            }
        }

        val oledReader = createReader(domainParticipant, subscriber, ReaderTopic.OledService.topicName) { strData ->
            ScreenLog.log(" onOledDataAvailable ")
            try {
                val topicData = parseTopicData(strData)
                topicListener?.onOledDataAvailable(topicData)
            } catch (e: Throwable) {
                ScreenLog.log("parse OLEDData error")
            }
        }
        val dkeyReader = createReader(domainParticipant, subscriber, ReaderTopic.DkeyService.topicName) { strData ->
            ScreenLog.log("onDigitalKeyStateChanged, rawData: $strData")
            try {
                val state = strData[0].toInt()
                val keyState = when (state) {
                    1 -> DdsService.DigitalKeyState.UnlockDoor
                    2 -> DdsService.DigitalKeyState.LockDoor
                    else -> DdsService.DigitalKeyState.Reset
                }
                topicListener?.onDigitalKeyStateChanged(keyState)
            } catch (e: Throwable) {
                ScreenLog.log("parse DigitalKey Data error")
            }
        }

        val publisher = domainParticipant.CreateJavaPublisher()
        if (null == publisher) {
            ScreenLog.log(TAG,"Create JavaPublisher error")
            return 3
        }

        val avasWriter = createWriter(domainParticipant, publisher, WriterTopic.AvasControl.topicName)
        val oledWriter = createWriter(domainParticipant, publisher, WriterTopic.OledControl.topicName)


        if (avasReader == null || oledReader == null || dkeyReader == null || avasWriter == null || oledWriter == null) {
            ScreenLog.log(TAG,"Create reader or writer failed")
            return 4
        }

        this.avasReader = avasReader
        this.oledReader = oledReader
        this.dkeyReader = dkeyReader
        this.avasWriter = avasWriter
        this.oledWriter = oledWriter

        hasStarted = true

        ScreenLog.log(TAG,"Init success!")
        return 0
    }

    private fun createReader(
        domainParticipant: JavaDomainParticipant,
        subscriber: JavaSubscriber,
        topicName: String,
        listener: (ByteArray) -> Unit
    ): JavaDataReader? {
        val topic = domainParticipant.CreateJavaTopic(topicName)
        if (topic == null) {
            ScreenLog.log("Create topic failed: $topicName")
            return null
        }
        /* 创建数据读者，指定订阅某一个主题的数据 */
        val reader = subscriber.CreateJavaDataReader(topic, object : JavaDataReaderListener() {
            override fun on_data_available(p0: JavaDataReader, strData: ByteArray, p2: Long) {
                listener(strData)
            }
        })
        if (reader == null) {
            ScreenLog.log("Create topic reader failed: $topicName")
            return null
        }
        return reader
    }

    private fun createWriter(
        domainParticipant: JavaDomainParticipant,
        publisher: JavaPublisher,
        topicName: String
    ): JavaDataWriter? {
        val topic = domainParticipant.CreateJavaTopic(topicName)
        if (topic == null) {
            ScreenLog.log("Create topic failed: $topicName")
            return null
        }
        val writer = publisher.CreateJavaDataWriter(topic)
        if (writer == null) {
            ScreenLog.log("Create topic writer failed")
            return null
        }
        return writer
    }

    private fun JavaDataWriter.sendData(data: ByteArray): Long {
        return this.Write(data, data.size.toLong())
    }

    private fun wifiIpAddress(context: Context): String? {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        var ipAddress = wifiManager.connectionInfo.ipAddress

        // Convert little-endian to big-endian if needed
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            ipAddress = Integer.reverseBytes(ipAddress)
        }
        val ipByteArray = BigInteger.valueOf(ipAddress.toLong()).toByteArray()
        val ipAddressString: String? = try {
            InetAddress.getByAddress(ipByteArray).hostAddress
        } catch (ex: UnknownHostException) {
            ScreenLog.log("WIFIIP", "Unable to get host address.")
            null
        }
        return ipAddressString
    }

    companion object {
        private val Guid = ByteArray(12).apply {
            this[0] = 1
            this[1] = 1
            this[2] = 1
            this[3] = 0
            this[4] = 0
            this[5] = 1
            this[6] = 1
            this[7] = 1
            this[8] = 1
            this[9] = 1
            this[10] = 1
            this[11] = 1
        }
        private val DomainId: Long = 0L
        private const val DiscoverIp: String = "239.255.0.1" // 239.255.0.1   255.255.255.255
    }
}

private fun ByteArray.printArrayInInt(): String {
    val stringBuffer = StringBuffer()
    this.forEach { stringBuffer.append(it.toUByte().toInt().toString() + " ")  }
    return stringBuffer.toString()
}


enum class ReaderTopic(val topicName: String) {
    AvasService("AVAS service"),
    OledService("OLED service"),
    DkeyService("Dkey_service")
}

enum class WriterTopic(val topicName: String) {
    AvasControl("AVAS control"),
    OledControl("OLED control")
}
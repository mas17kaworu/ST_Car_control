package com.longkai.stcarcontrol.st_exp.compose.data.dds.service

import java.nio.BufferUnderflowException
import java.nio.ByteBuffer

data class TopicData(
    val serviceNumber: Int,
    val services: List<TopicService>
)

data class TopicService(
    val name: String,
    val content: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TopicService

        if (name != other.name) return false
        if (!content.contentEquals(other.content)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + content.contentHashCode()
        return result
    }
}

/**
 * Sample data: "0x2, 0x2, 0x1, 0x97, 0x97, 0x1, 0x2, 0x1, 0x98, 0x98, 0x2, 0x0", end with "0x0".
 */
fun parseTopicData(strData: ByteArray): TopicData {
    try {
        val byteBuffer = ByteBuffer.wrap(strData)
        val serviceNumber = byteBuffer.get().toInt()

        val services = mutableListOf<TopicService>()
        for (i in 0 until serviceNumber) {
            val nameLength = byteBuffer.get().toInt()
            val contentLength = byteBuffer.get().toInt()
            val name = ByteArray(nameLength)
            val content = ByteArray(contentLength)
            byteBuffer.get(name)
            byteBuffer.get(content)

            services.add(
                TopicService(
                    name = name.toString(),
                    content = content
                )
            )
        }

        return TopicData(
            serviceNumber = serviceNumber,
            services = services
        )
    } catch (e: BufferUnderflowException) {
        println("parse topic content failed: $strData")
        throw e
    }
}

package com.longkai.stcarcontrol.st_exp.compose.data.dds.service

import junit.framework.TestCase
import org.junit.Test

class TopicDataKtTest{
  @Test
  fun `parseTopicData`() {
    //Sample data: "0x2, 0x2, 0x1, 0x97, 0x97, 0x1, 0x2, 0x1, 0x98, 0x98, 0x2, 0x0", end with "0x0".
    val sampleData = ByteArray(12).apply {
      this[0] = 0x2
      this[1] = 0x2
      this[2] = 0x1
      this[3] = 97.toByte()
      this[4] = 97.toByte()
      this[5] = 0x1
      this[6] = 0x2
      this[7] = 0x1
      this[8] = 98.toByte()
      this[9] = 98.toByte()
      this[10] = 0x2
      this[11] = 0x0
    }
    sampleData.printArrayInInt()

    val topicData = parseTopicData(sampleData)
    println(topicData.serviceNumber)
    println(topicData.services)
  }
}

private fun ByteArray.printArrayInInt(): String {
  val stringBuffer = StringBuffer()
  this.forEach { stringBuffer.append(it.toUByte().toInt().toString() + " ")  }
  return stringBuffer.toString()
}
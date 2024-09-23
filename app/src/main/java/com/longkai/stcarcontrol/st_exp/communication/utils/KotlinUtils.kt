package com.longkai.stcarcontrol.st_exp.communication.utils

fun byteArrayToInt(byteArray: ByteArray, startIndex1: Int): Int {
    return (byteArray[startIndex1].toInt() and 0xFF) or
            ((byteArray[startIndex1 + 1].toInt() and 0xFF) shl 8) or
            ((byteArray[startIndex1 + 2].toInt() and 0xFF) shl 16) or
            ((byteArray[startIndex1 + 3].toInt() and 0xFF) shl 24)
}
package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDFrontC11LightList

class CMDFrontC11Mode2 : CMDFrontC11Light() {
    init {
        payload[1] = 0
    }

    override fun turnOn() {
        payload[1] = 0
        payload[1] = (payload[1].toInt() or Mode2.toInt()).toByte()
        refreshDataPayload()
    }

    override fun turnOff() {
        payload[1] = 0
        refreshDataPayload()
    }
}
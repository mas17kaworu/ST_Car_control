package com.longkai.stcarcontrol.st_exp.compose.data.dds

import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.ExpressService
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.ServiceAction
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.TriggerCondition

val fakeSoundEffect1 = ServiceAction.AvasAction("Sound Effect 1", String(byteArrayOf(0x01)))
val fakeSoundEffect2 = ServiceAction.AvasAction("Sound Effect 2", String(byteArrayOf(0x02)))
val fakeSoundEffect3 = ServiceAction.AvasAction("Sound Effect 3", String(byteArrayOf(0x03)))
val fakeSoundEffect4 = ServiceAction.AvasAction("Sound Effect 1", String(byteArrayOf(0x04)))
val fakeSoundEffect5 = ServiceAction.AvasAction("Sound Effect 2", String(byteArrayOf(0x05)))
val fakeSoundEffect6 = ServiceAction.AvasAction("Sound Effect 3", String(byteArrayOf(0x06)))
val fakeSoundEffect7 = ServiceAction.AvasAction("Sound Effect 1", String(byteArrayOf(0x07)))
val fakeSoundEffect8 = ServiceAction.AvasAction("Sound Effect 2", String(byteArrayOf(0x08)))
val fakeSoundEffect9 = ServiceAction.AvasAction("Sound Effect 3", String(byteArrayOf(0x09)))

val fakeLightEffect1 = ServiceAction.OledAction("Light Effect 1", String(byteArrayOf(0x01)))
val fakeLightEffect2 = ServiceAction.OledAction("Light Effect 2", String(byteArrayOf(0x02)))
val fakeLightEffect3 = ServiceAction.OledAction("Light Effect 3", String(byteArrayOf(0x03)))

val fakeExpressServices = listOf(
    ExpressService(
        id = 0,
        name = "Welcome service",
        triggerCondition = TriggerCondition.DigitalKeyUnlock,
        actions = listOf(
            fakeSoundEffect2,
            ServiceAction.Delay("Delay", 10),
            fakeLightEffect3
        )
    ),
    ExpressService(
        id = 1,
        name = "Light 2 service",
        triggerCondition = TriggerCondition.DoubleClick,
        actions = listOf(
            fakeLightEffect2
        )
    ),
    ExpressService(
        id = 2,
        name = "Sound 1 service",
        triggerCondition = TriggerCondition.DoubleClick,
        actions = listOf(
            fakeSoundEffect1
        )
    )
)

val fakeAvasActions = listOf(
    fakeSoundEffect1,
    fakeSoundEffect2,
    fakeSoundEffect3,
    fakeSoundEffect4,
    fakeSoundEffect5,
    fakeSoundEffect6,
    fakeSoundEffect7,
    fakeSoundEffect8,
    fakeSoundEffect9
)

val fakeOledActions = listOf(
    fakeLightEffect1, fakeLightEffect2, fakeLightEffect3
)
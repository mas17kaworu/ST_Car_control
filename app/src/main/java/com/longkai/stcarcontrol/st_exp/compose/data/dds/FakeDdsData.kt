package com.longkai.stcarcontrol.st_exp.compose.data.dds

import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.ExpressService
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.ServiceAction
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.TriggerCondition

val fakeExpressServices = listOf(
    ExpressService(
        id = 0,
        name = "Welcome service",
        triggerCondition = TriggerCondition.DigitalKeyUnlock,
        actions = listOf(
            ServiceAction.AvasAction("Sound Effect 2"),
            ServiceAction.Delay("Delay", 10),
            ServiceAction.OledAction("Light Effect 3")
        )
    ),
    ExpressService(
        id = 1,
        name = "Light 2 service",
        triggerCondition = TriggerCondition.DoubleClick,
        actions = listOf(
            ServiceAction.OledAction("Light Effect 2")
        )
    ),
    ExpressService(
        id = 2,
        name = "Sound 1 service",
        triggerCondition = TriggerCondition.DoubleClick,
        actions = listOf(
            ServiceAction.AvasAction("Sound Effect 1")
        )
    )
)

val fakeAvasActions = listOf(
    ServiceAction.AvasAction("Sound Effect 1"),
    ServiceAction.AvasAction("Sound Effect 2"),
    ServiceAction.AvasAction("Sound Effect 3")
)

val fakeOledActions = listOf(
    ServiceAction.OledAction("Light Effect 1"),
    ServiceAction.OledAction("Light Effect 2"),
    ServiceAction.OledAction("Light Effect 3")
)
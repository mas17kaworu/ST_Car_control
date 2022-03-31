package com.longkai.stcarcontrol.st_exp.compose.data.dds.model

import com.longkai.stcarcontrol.st_exp.compose.data.dds.service.TopicData
import com.longkai.stcarcontrol.st_exp.compose.data.dds.service.TopicService

fun TopicData.toAvasActions() = this.services.map {
    ServiceAction.AvasAction(
        name = it.name,
        action = String(it.content)
    )
}

fun TopicData.toOledActions() = this.services.map {
    ServiceAction.OledAction(
        name = it.name,
        action = String(it.content)
    )
}

fun List<ServiceAction.AvasAction>.toAvasTopicData() = TopicData(
    serviceNumber = this.size,
    services = this.map {
        TopicService(
            name = it.name,
            content = it.action.toByteArray()
        )
    }
)

fun List<ServiceAction.OledAction>.toOledTopicData() = TopicData(
    serviceNumber = this.size,
    services = this.map {
        TopicService(
            name = it.name,
            content = it.action.toByteArray()
        )
    }
)

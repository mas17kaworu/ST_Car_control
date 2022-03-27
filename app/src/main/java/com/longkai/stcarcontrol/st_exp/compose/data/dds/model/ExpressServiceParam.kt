package com.longkai.stcarcontrol.st_exp.compose.data.dds.model

data class ExpressServiceParam(
    val name: String,
    val triggerCondition: TriggerCondition,
    val actions: List<ServiceAction>
)
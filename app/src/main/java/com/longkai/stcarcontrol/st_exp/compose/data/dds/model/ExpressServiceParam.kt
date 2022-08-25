package com.longkai.stcarcontrol.st_exp.compose.data.dds.model

import android.net.Uri

data class ExpressServiceParam(
    val name: String,
    val triggerCondition: TriggerCondition,
    val actions: List<ServiceAction>,
    val imageUri: String? = null
)
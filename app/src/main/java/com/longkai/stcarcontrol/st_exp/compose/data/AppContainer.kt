package com.longkai.stcarcontrol.st_exp.compose.data

import android.content.Context
import com.longkai.stcarcontrol.st_exp.ai.EsrHelper
import com.longkai.stcarcontrol.st_exp.compose.data.dds.DdsRepo
import com.longkai.stcarcontrol.st_exp.compose.data.dds.DdsRepoImpl
import com.longkai.stcarcontrol.st_exp.compose.data.dds.test.MockDdsService
import com.longkai.stcarcontrol.st_exp.compose.data.dds.service.DdsServiceImpl

interface AppContainer {
    val ddsRepo: DdsRepo

    val aiRepo: AIRepo
}

class AppContainerImpl(
    private val applicationContext: Context,
    private val inDebugMode: Boolean
) : AppContainer {

    override val ddsRepo: DdsRepo by lazy {
        DdsRepoImpl(
            context = applicationContext,
            ddsService = if (inDebugMode) MockDdsService(applicationContext) else DdsServiceImpl(applicationContext)
        )
    }

    override val aiRepo: AIRepo by lazy {
        AIRepoImpl()
    }

}
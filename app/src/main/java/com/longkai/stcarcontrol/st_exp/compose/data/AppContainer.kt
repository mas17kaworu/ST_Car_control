package com.longkai.stcarcontrol.st_exp.compose.data

import android.content.Context
import com.longkai.stcarcontrol.st_exp.ai.EsrHelper
import com.longkai.stcarcontrol.st_exp.compose.data.dds.DdsRepo
import com.longkai.stcarcontrol.st_exp.compose.data.dds.DdsRepoImpl
import com.longkai.stcarcontrol.st_exp.compose.data.dds.test.MockDdsService
import com.longkai.stcarcontrol.st_exp.compose.data.dds.service.DdsServiceImpl
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.ChassisRepository
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.DefaultChassisRepository
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.fake.FakeChassisDeviceDataSource

interface AppContainer {
    val ddsRepo: DdsRepo

    val aiRepo: AIRepo

    val chassisRepository: ChassisRepository
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

    override val chassisRepository: ChassisRepository by lazy {
        // Chassis remains a standalone demo until its hardware protocol is configured.
        DefaultChassisRepository(FakeChassisDeviceDataSource())
    }

}
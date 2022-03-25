package com.longkai.stcarcontrol.st_exp.compose.data

import android.content.Context
import com.longkai.stcarcontrol.st_exp.compose.data.dds.DdsRepo
import com.longkai.stcarcontrol.st_exp.compose.data.dds.DdsRepoImpl

interface AppContainer {
    val ddsRepo: DdsRepo
}

class AppContainerImpl(private val applicationContext: Context): AppContainer {
    override val ddsRepo: DdsRepo by lazy {
        DdsRepoImpl(applicationContext)
    }

}
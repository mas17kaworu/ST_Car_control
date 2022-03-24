package com.longkai.stcarcontrol.st_exp.compose.data

import android.content.Context
import com.longkai.stcarcontrol.st_exp.compose.ui.dds.DdsRepo
import com.longkai.stcarcontrol.st_exp.compose.ui.dds.DdsRepoImpl

interface AppContainer {
    val ddsRepo: DdsRepo
}

class AppContainerImpl(private val applicationContext: Context): AppContainer {
    override val ddsRepo: DdsRepo by lazy {
        DdsRepoImpl()
    }

}
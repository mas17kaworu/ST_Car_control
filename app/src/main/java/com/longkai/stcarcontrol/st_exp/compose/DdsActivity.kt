package com.longkai.stcarcontrol.st_exp.compose

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.ProvideWindowInsets
import com.longkai.stcarcontrol.st_exp.STCarApplication
import com.longkai.stcarcontrol.st_exp.compose.data.dds.notification.DigitalKeyUnlockService
import com.longkai.stcarcontrol.st_exp.compose.ui.components.ActivityViewContainer
import com.longkai.stcarcontrol.st_exp.compose.ui.dds.DdsScreen
import com.longkai.stcarcontrol.st_exp.compose.ui.dds.DdsViewModel
import com.longkai.stcarcontrol.st_exp.compose.ui.theme.STCarTheme

class DdsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val appContainer = (application as STCarApplication).appContainer
        setContent {
            STCarTheme {
                ProvideWindowInsets(windowInsetsAnimationsEnabled = true) {
                    ActivityViewContainer() {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp)
                        ) {
                            val ddsViewModel: DdsViewModel = viewModel(
                                factory = DdsViewModel.provideFactory(appContainer.ddsRepo)
                            )
                            DdsScreen(ddsViewModel = ddsViewModel)
                        }
                    }
                }
            }
        }

        startService(Intent(this, DigitalKeyUnlockService::class.java))
    }
}



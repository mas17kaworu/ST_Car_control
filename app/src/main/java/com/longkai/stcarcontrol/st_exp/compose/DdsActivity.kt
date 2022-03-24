package com.longkai.stcarcontrol.st_exp.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composestart.ui.theme.STCarTheme
import com.google.accompanist.insets.ProvideWindowInsets
import com.longkai.stcarcontrol.st_exp.STCarApplication
import com.longkai.stcarcontrol.st_exp.compose.ui.DdsScreen
import com.longkai.stcarcontrol.st_exp.compose.ui.dds.DdsViewModel

class DdsActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    WindowCompat.setDecorFitsSystemWindows(window, false)

    val appContainer = (application as STCarApplication).appContainer
    setContent {
      STCarTheme {
        ProvideWindowInsets(windowInsetsAnimationsEnabled = true) {
          val ddsViewModel: DdsViewModel = viewModel(
            factory = DdsViewModel.provideFactory(appContainer.ddsRepo)
          )
          DdsScreen(ddsViewModel)
        }
      }
    }
  }
}

package com.longkai.stcarcontrol.st_exp.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.example.composestart.ui.theme.STCarTheme
import com.google.accompanist.insets.ProvideWindowInsets
import com.longkai.stcarcontrol.st_exp.compose.ui.DDSView

class DDSActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    WindowCompat.setDecorFitsSystemWindows(window, false)

    setContent {
      STCarTheme {
        ProvideWindowInsets(windowInsetsAnimationsEnabled = true) {
          DDSView()
        }
      }
    }
  }
}

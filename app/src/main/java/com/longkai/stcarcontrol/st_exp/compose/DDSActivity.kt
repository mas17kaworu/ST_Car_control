package com.longkai.stcarcontrol.st_exp.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.example.composestart.ui.theme.STCarTheme
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.longkai.stcarcontrol.st_exp.R
import com.longkai.stcarcontrol.st_exp.activity.BaseActivity

class DDSActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    WindowCompat.setDecorFitsSystemWindows(window, false)

    setContent {
      STCarTheme {
        ProvideWindowInsets {
          DDSView()
        }
      }
    }
  }
}

@Composable
fun DDSView() {
  Box(modifier = Modifier.fillMaxSize()) {
    Image(
      painter = painterResource(id = R.mipmap.ic_fragment_background),
      contentDescription = "background",
      modifier = Modifier.fillMaxSize(),
      contentScale = ContentScale.FillBounds
    )
    Box(
      modifier = Modifier.fillMaxSize()
      .statusBarsPadding()
      .navigationBarsPadding()
    ) {

      Text(text = "Hello Android!")
    }
  }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
  STCarTheme {
    DDSView()
  }
}
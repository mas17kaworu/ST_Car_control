package com.longkai.stcarcontrol.st_exp.compose.ui.dds

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.longkai.stcarcontrol.st_exp.compose.data.dds.ImagePreferenceRepo
import com.longkai.stcarcontrol.st_exp.compose.ui.components.CorneredContainer

@Composable
fun FangjiaCard(
  modifier: Modifier = Modifier,
  fangjiaState: Int,
) {
  val imageUris = remember {
    ImagePreferenceRepo.getImageUris()
  }
  var imageUri = remember(
    key1 = fangjiaState
  ) {
    imageUris.getOrNull(fangjiaState)
  }
  Column(
    modifier = modifier,
  ) {
    CorneredContainer(
      modifier = Modifier.weight(1f),
      cornerSize = 24.dp,
      backgroundColor = Color(0xFF3f454d)
    ) {
      Box(
        modifier = Modifier.fillMaxSize(1f)
      ) {
        AsyncImage(
          model = imageUri,
          contentDescription = null,
          modifier = Modifier
            .align(Alignment.Center)
            .size(350.dp)
        )
        Text(
          modifier = Modifier
            .align(Alignment.TopStart)
            .padding(top = 16.dp, start = 16.dp),
          text = "Window Lift Status",
//          text = "Window Lift Status $fangjiaState",
          fontSize = 20.sp,
        )
      }
    }

    Spacer(modifier = Modifier.height(120.dp))
  }
}
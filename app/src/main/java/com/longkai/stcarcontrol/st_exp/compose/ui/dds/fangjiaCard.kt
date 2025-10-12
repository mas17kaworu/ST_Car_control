package com.longkai.stcarcontrol.st_exp.compose.ui.dds

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.longkai.stcarcontrol.st_exp.compose.ui.components.CorneredContainer

@Composable
fun FangjiaCard(
  modifier: Modifier = Modifier,
  fangjiaState: Int,
) {
  CorneredContainer(
    modifier = modifier.background(color = Color.DarkGray),
    cornerSize = 24.dp,
  ) {
    Box(modifier = Modifier.fillMaxSize(1f)) {

    }
  }
}
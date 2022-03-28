package com.longkai.stcarcontrol.st_exp.compose.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.longkai.stcarcontrol.st_exp.compose.ui.theme.Typography


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CorneredContainer(
    modifier: Modifier = Modifier,
    cornerSize: Dp = 16.dp,
    outerPadding: Dp = 16.dp,
    backgroundColor: Color = Color.Gray.copy(alpha = 0.3f),
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.padding(outerPadding),
        shape = RoundedCornerShape(cornerSize),
        color = backgroundColor,
        contentColor = Color.White
    ) {
        content()
    }
}
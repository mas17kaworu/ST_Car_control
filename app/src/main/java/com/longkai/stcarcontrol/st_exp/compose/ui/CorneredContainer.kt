package com.longkai.stcarcontrol.st_exp.compose.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.composestart.ui.theme.Typography


@Composable
fun CorneredContainer(
    modifier: Modifier = Modifier,
    cornerSize: Dp = 16.dp,
    outerPadding: Dp = 16.dp,
    innerPadding: Dp = 16.dp,
    backgroundColor: Color = Color.Gray,
    content: @Composable BoxWithConstraintsScope.() -> Unit
) {
    Surface(
        modifier = modifier.padding(outerPadding),
        shape = RoundedCornerShape(cornerSize),
        color = backgroundColor,
        contentColor = Color.White
    ) {
        BoxWithConstraints(
            modifier = Modifier.padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

@Composable
fun ServiceListInContainer() {

    CorneredContainer(
        cornerSize = 24.dp
    ) {
        Column {
            Text(
                text = "Service Express",
                color = MaterialTheme.colors.primaryVariant,
                style = Typography.h5
            )
            Spacer(modifier = Modifier.height(16.dp))
            CorneredContainer(
                outerPadding = 0.dp,
                backgroundColor = MaterialTheme.colors.primaryVariant
            ) {
                Text(
                    text = "+",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = Typography.h3
                )
            }
        }
    }
}
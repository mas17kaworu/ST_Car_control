package com.longkai.stcarcontrol.st_exp.compose.ui.dds

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.RepeatMode.*
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.longkai.stcarcontrol.st_exp.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun AIDialog(
    aiListenResult: String,
//    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties()
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colors.background,
        ) {
            Column(
                modifier = Modifier.sizeIn(
                    minWidth = 300.dp,
                    maxWidth = 300.dp,
                    minHeight = 200.dp
                ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
//                val infiniteTransition = rememberInfiniteTransition()
//                val rotation by infiniteTransition.animateFloat(
//                    initialValue = 0f,
//                    targetValue = 360f,
//                    animationSpec = infiniteRepeatable(
//                        animation = tween(durationMillis = 1000, easing = LinearEasing)
//                    )
//                )
//
//                Icon(
//                    painterResource(R.drawable.cycle),
//                    contentDescription = "loading",
//                    tint = MaterialTheme.colors.primary,
//                    modifier = Modifier
//                        .size(32.dp)
//                        .graphicsLayer {
//                            rotationZ = rotation
//                        })

                OscillatingAudioBarsAnimation()
                
                Spacer(modifier = Modifier.height(20.dp))

                Box(modifier = Modifier) {
                    Text(
                        text = aiListenResult,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun OscillatingAudioBarsAnimation() {
    val barCount = 5  // 条块数量
    val barWidth = 8.dp
    val maxBarHeight = 40.dp
    val minBarHeight = 10.dp

    // 创建一个无限动画，用于控制每个条块的上下往复运动
    val infiniteTransition = rememberInfiniteTransition()

    // 生成每根柱子的不同动画，柱子高度将在min和max之间来回摆动
    val barHeights = List(barCount) { index ->
        val animationProgress = infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1000, easing = LinearEasing),
                repeatMode = Reverse
            )
        )
        derivedStateOf {
            // 使用sin函数来生成高度的平滑变化
            val oscillation = sin((animationProgress.value * 2 * Math.PI).toFloat() + (index * Math.PI / barCount).toFloat())
            val normalizedOscillation = (oscillation + 1) / 2  // 将sin值范围[-1, 1]映射到[0, 1]
            minBarHeight + (maxBarHeight - minBarHeight) * normalizedOscillation
        }
    }

    // 绘制条状动画
    Row(
        modifier = Modifier
            .width(80.dp)
            .height(maxBarHeight),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        barHeights.forEach { height ->
            Bar(height = height.value, width = barWidth)
        }
    }
}

@Composable
fun Bar(height: Dp, width: Dp) {
    Canvas(
        modifier = Modifier
            .width(width)
            .height(height)
    ) {
        drawRect(color = Color.Green)
    }
}
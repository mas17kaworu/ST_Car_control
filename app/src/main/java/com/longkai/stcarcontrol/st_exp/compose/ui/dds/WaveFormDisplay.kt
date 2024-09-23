package com.longkai.stcarcontrol.st_exp.compose.ui.dds

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import com.longkai.stcarcontrol.st_exp.R
import com.longkai.stcarcontrol.st_exp.compose.ui.components.CorneredContainer
import kotlinx.coroutines.flow.emptyFlow
import java.text.DecimalFormat


@Composable
fun WaveformDialog(
    flow: Flow<Float>,
    onDismiss: () -> Unit,
    waveMaxValue: Int,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnClickOutside = true)
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colors.background,
        ) {
            BouncingDotWithPath(
                currentMaxValue = waveMaxValue,
                voltageMaxValue = waveMaxValue,
                currentFlow = flow,
                voltageFlow = emptyFlow(),
                modifier = Modifier.size(500.dp, 400.dp)
            )
        }
    }
}

private const val PointCountMaxNumber = 200

@Composable
fun BouncingDotWithPath(
    modifier: Modifier,
    currentMaxValue: Int,
    voltageMaxValue: Int,
    currentFlow: Flow<Float>,
    voltageFlow: Flow<Float>,
) {
    val currentPath = remember { mutableStateListOf<Offset>() }
    val dotRadius = 2f
    var currentDotYValue by remember { mutableStateOf(0f) }
    val voltagePath = remember { mutableStateListOf<Offset>() }
    var voltageDotYValue by remember { mutableStateOf(0f) }

    var paused by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(currentFlow) {
        // 收集来自 flow 的高度数据
        currentFlow.collectLatest { value ->
            if (!paused) {
                currentDotYValue = value
            }
        }
    }

    LaunchedEffect(voltageFlow) {
        // 收集来自 flow 的高度数据
        voltageFlow.collectLatest { value ->
            if (!paused) {
                voltageDotYValue = value
            }
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            if (!paused) {
                // 每帧更新轨迹，将所有点向左移动
                val xStep = 5f // 这个值可以调整以控制轨迹延伸的速度
                for (i in currentPath.indices) {
                    currentPath[i] = currentPath[i].copy(x = currentPath[i].x - xStep)
                }
                // 添加新的点到轨迹中
                currentPath.add(Offset(x = 0f, y = currentDotYValue))
                // 限制轨迹点数量以节省内存
                if (currentPath.size > PointCountMaxNumber) {
                    currentPath.removeFirst()
                }

                for (i in voltagePath.indices) {
                    voltagePath[i] = voltagePath[i].copy(x = voltagePath[i].x - xStep)
                }
                voltagePath.add(Offset(x = 0f, y = voltageDotYValue))
                if (voltagePath.size > PointCountMaxNumber) {
                    voltagePath.removeFirst()
                }
            }
            delay(50) // 大约每16毫秒（60fps）更新一次
        }
    }

    Canvas(
        modifier = modifier
            .background(color = MaterialTheme.colors.background)
            .padding(30.dp)
            .clickable {
                paused = !paused
            }
    ) {
        val yStart = size.height - 5
        // 绘制白色背景
        drawRect(color = Color(0xff1e2530), size = size)

        // 绘制网格
        val gridSize = 20.dp.toPx()
        for (x in 0 until size.width.toInt() step gridSize.toInt()) {
            drawLine(
                color = Color.LightGray,
                start = Offset(x.toFloat(), 0f),
                end = Offset(x.toFloat(), size.height)
            )
        }
        for (y in 0 until size.height.toInt() step gridSize.toInt()) {
            drawLine(
                color = Color.LightGray,
                start = Offset(0f, y.toFloat()),
                end = Offset(size.width, y.toFloat())
            )
            // 在右侧绘制对应的数值（数值对应于 Y 轴坐标）
//            drawTextOnCanvas(
//                value = y, canvasWidth = size.width, y = y.toFloat(), padding = textPadding
//            )
        }
        // 绘制最大值文字和T
        drawTextOnCanvas(
            value = "${currentMaxValue}A",
            x = -40f,
            y = -5f,
            textColor = android.graphics.Color.RED,
            fontSize = 40f,
        )
        drawTextOnCanvas(
            value = "${voltageMaxValue}V",
            x = size.width - 20f,
            y = -5f,
            textColor = android.graphics.Color.GREEN,
            fontSize = 40f,
        )
        drawTextOnCanvas(
            value = "T",
            x = -30f,
            y = yStart + 20f,
            fontSize = 40f,
        )

        // 绘制轨迹
        val centerX = size.width * 2 / 3f

        val realCurrentPath = currentPath.map {
            val canvasY = it.y.toCanvasY(
                canvasHeight = size.height,
                maxYValue = currentMaxValue,
                yStart = yStart,
            )
            it.copy(it.x + centerX, canvasY)
        }
        val realTempPath = voltagePath.map {
            val canvasY = it.y.toCanvasY(
                canvasHeight = size.height,
                maxYValue = voltageMaxValue,
                yStart = yStart,
            )
            it.copy(it.x + centerX, canvasY)
        }

        val trajectoryCurrentPath = Path().apply {
            if (currentPath.size > 2) {
                // 从第一个点开始
                moveTo(realCurrentPath[0].x, realCurrentPath[0].y)
                for (i in 1 until realCurrentPath.size - 1) {
                    val p0 = realCurrentPath[i - 1]
                    val p1 = realCurrentPath[i]
                    val p2 = realCurrentPath[i + 1]

                    // 计算控制点
                    val controlPoint = Offset(
                        x = (p0.x + p2.x) / 2,
                        y = (p0.y + p2.y) / 2
                    )

                    // 使用二次贝塞尔曲线
                    quadraticBezierTo(
                        x1 = p1.x,
                        y1 = p1.y,
                        x2 = controlPoint.x,
                        y2 = controlPoint.y
                    )
                }

                // 最后一个点直接连接
                lineTo(realCurrentPath.last().x, realCurrentPath.last().y)
            }
        }
        drawPath(
            path = trajectoryCurrentPath,
            color = Color.Red,
            style = Stroke(
                width = 2.dp.toPx(),
            )
        )

        // 绘制 voltage path
        val trajectoryVoltagePath = Path().apply {
            if (currentPath.size > 2) {
                // 从第一个点开始
                moveTo(realTempPath[0].x, realTempPath[0].y)
                for (i in 1 until realTempPath.size - 1) {
                    val p0 = realTempPath[i - 1]
                    val p1 = realTempPath[i]
                    val p2 = realTempPath[i + 1]

                    // 计算控制点
                    val controlPoint = Offset(
                        x = (p0.x + p2.x) / 2,
                        y = (p0.y + p2.y) / 2
                    )

                    // 使用二次贝塞尔曲线
                    quadraticBezierTo(
                        x1 = p1.x,
                        y1 = p1.y,
                        x2 = controlPoint.x,
                        y2 = controlPoint.y
                    )
                }

                // 最后一个点直接连接
                lineTo(realTempPath.last().x, realTempPath.last().y)
            }
        }
        drawPath(
            path = trajectoryVoltagePath,
            color = Color.Green,
            style = Stroke(
                width = 2.dp.toPx(),
            )
        )

        // 绘制点
//        val dotCanvasY = currentDotYValue.toCanvasY(
//            canvasHeight = size.height,
//            maxYValue = currentMaxValue,
//        )
//        drawCircle(
//            color = Color.Red,
//            radius = dotRadius,
//            center = Offset(
//                x = centerX,
//                y = dotCanvasY,
//            ),
//        )

        // 计算点的实际Y值并显示在点的右侧
        val currentDotCanvasY = currentDotYValue.toCanvasY(
            canvasHeight = size.height,
            maxYValue = currentMaxValue,
            yStart = yStart,
        )
        val textPadding = 4.dp.toPx()
        drawTextOnCanvas(
            value = currentDotYValue.toString() + "A",
            x = centerX + dotRadius + textPadding,
            y = currentDotCanvasY,
            paddingX = textPadding,
            paddingY = 0f
        )

        val voltageDotCanvasY = voltageDotYValue.toCanvasY(
            canvasHeight = size.height,
            maxYValue = voltageMaxValue,
            yStart = yStart,
        )
        drawTextOnCanvas(
            value = voltageDotYValue.toString() + "V",
            x = centerX + dotRadius + textPadding,
            y = voltageDotCanvasY,
            paddingX = textPadding,
            paddingY = 0f
        )

    }
}

private fun Float.toCanvasY(
    canvasHeight: Float,
    maxYValue: Int,
    yStart: Float,
) =
    yStart - this * (yStart) / maxYValue


fun DrawScope.drawTextOnCanvas(
    value: String,
    x: Float,
    y: Float,
    paddingX: Float = 0f,
    paddingY: Float = 0f,
    textColor: Int = android.graphics.Color.WHITE,
    fontSize: Float = 30f,
) {
    drawContext.canvas.nativeCanvas.apply {
        drawText(
            value,
            x + paddingX,
            y + paddingY,
            android.graphics.Paint().apply {
                color = textColor
                textSize = fontSize
            }
        )
    }
}

@Composable
fun WaveCardView(
    modifier: Modifier = Modifier,
    currentFlow: Flow<Float>,
    voltageFlow: Flow<Float>,
    tempDeviceValue: Float?,
    tempMosValue: Float?,
    currentMaxValue: Int,
    voltageMaxValue: Int,
) {
    CorneredContainer(
        modifier = modifier,
        cornerSize = 24.dp,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            val current = currentFlow.collectAsState(initial = null)
            val voltage = voltageFlow.collectAsState(initial = null)
            CorneredContainer(cornerSize = 4.dp) {
                BouncingDotWithPath(
                    currentFlow = currentFlow,
                    voltageFlow = voltageFlow,
                    currentMaxValue = currentMaxValue,
                    voltageMaxValue = voltageMaxValue,
                    modifier = Modifier.size(481.dp, 301.dp),
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row {
                ColumnInfoText(
                    title = stringResource(id = R.string.outputCurrent),
                    value = current.value.showNumber(),
                    unit = "A"
                )
                Spacer(modifier = Modifier.width(24.dp))
                ColumnInfoText(
                    title = stringResource(id = R.string.outputVoltage),
                    value = voltage.value.showNumber(),
                    unit = "V"
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row {
                ColumnInfoText(
                    title = stringResource(id = R.string.deviceTemp),
                    value = tempDeviceValue.showNumber("####"),
                    unit = "°C"
                )
                Spacer(modifier = Modifier.width(24.dp))
                ColumnInfoText(
                    title = stringResource(id = R.string.mosfetTemp),
                    value = tempMosValue.showNumber("###"),
                    unit = "°C"
                )
            }

        }
    }
}

private fun Float?.showNumber(
    pattern: String = "####.##"
): String {
    return this?.let {
        DecimalFormat(pattern).format(it)
    } ?: "--"
}

@Composable
fun ColumnInfoText(
    title: String,
    value: String,
    unit: String,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            color = Color.White,
        )
        Text(
            text = "$value $unit",
            color = Color.White,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BouncingDotWithPath(
        currentMaxValue = 100,
        voltageMaxValue = 100,
        voltageFlow = emptyFlow(),
        currentFlow = emptyFlow(),
        modifier = Modifier,
    )
}

@Preview
@Composable
fun ColumnInfoTextPreview() {
    ColumnInfoText(
        title = "Output Current",
        value = "20.11",
        unit = "A"
    )
}

@Preview
@Composable
fun WaveCardViewPreview() {
    WaveCardView(
        modifier = Modifier.size(width = 500.dp, height = 800.dp),
        currentFlow = flow { },
        voltageFlow = flow { },
        tempDeviceValue = 100000.99f,
        tempMosValue = null,
        currentMaxValue = 10,
        voltageMaxValue = 10,
    )
}
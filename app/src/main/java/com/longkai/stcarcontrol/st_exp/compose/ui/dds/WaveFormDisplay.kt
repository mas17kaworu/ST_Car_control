import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow


@Composable
fun WaveformDialog(
    flow: Flow<Int>,
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
                waveMaxValue = waveMaxValue,
                dotFlow = flow,
                modifier = Modifier.size(500.dp, 400.dp)
            )
        }
    }
}

@Composable
fun BouncingDotWithPath(
    waveMaxValue: Int,
    dotFlow: Flow<Int>,
    modifier: Modifier
) {
    val path = remember { mutableStateListOf<Offset>() }
    val dotRadius = 2f
    var dotValue by remember { mutableStateOf(0f) }

    LaunchedEffect(dotFlow) {
        // 收集来自 flow 的高度数据
        dotFlow.collectLatest { value ->
            dotValue = value.toFloat()
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            // 每帧更新轨迹，将所有点向左移动
            val xStep = 5f // 这个值可以调整以控制轨迹延伸的速度
            for (i in path.indices) {
                path[i] = path[i].copy(x = path[i].x - xStep)
            }

            // 添加新的点到轨迹中
            path.add(Offset(x = 0f, y = dotValue))

            // 限制轨迹点数量以节省内存
            if (path.size > 200) {
                path.removeFirst()
            }

            delay(50) // 大约每16毫秒（60fps）更新一次
        }
    }

    Canvas(modifier = modifier) {
        // 绘制白色背景
        drawRect(color = Color.White, size = size)

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

        // 绘制轨迹
        val centerX = size.width * 2 / 3f

        val realPositionPath = path.map {
            val canvasY = it.y.toCanvasY(canvasHeight = size.height, maxYValue = waveMaxValue)
            it.copy(it.x + centerX, canvasY)
        }

        val trajectoryPath = Path().apply {
            if (path.size > 2) {
                // 从第一个点开始
                moveTo(realPositionPath[0].x, realPositionPath[0].y)
                for (i in 1 until realPositionPath.size - 1) {
                    val p0 = realPositionPath[i - 1]
                    val p1 = realPositionPath[i]
                    val p2 = realPositionPath[i + 1]

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
                lineTo(realPositionPath.last().x, realPositionPath.last().y)
            }
        }
        drawPath(
            path = trajectoryPath,
            color = Color.Red,
            style = Stroke(
                width = 2.dp.toPx(),
            )
        )

        // 绘制点
        val dotCanvasY = dotValue.toCanvasY(
            canvasHeight = size.height,
            maxYValue = waveMaxValue,
        )
        drawCircle(
            color = Color.Red,
            radius = dotRadius,
            center = Offset(
                x = centerX,
                y = dotCanvasY,
            ),
        )

        // 计算点的实际Y值并显示在点的右侧
        val textPadding = 4.dp.toPx()
        drawTextOnCanvas(
            value = dotValue.toInt().toString() + "A",
            x = centerX + dotRadius + textPadding,
            y = dotCanvasY,
            paddingX = textPadding,
            paddingY = 0f
        )
    }
}

private fun Float.toCanvasY(canvasHeight: Float, maxYValue: Int) =
    canvasHeight / 2 - this * (canvasHeight / 2) / maxYValue


fun DrawScope.drawTextOnCanvas(value: String, x: Float, y: Float, paddingX: Float, paddingY: Float) {
    drawContext.canvas.nativeCanvas.apply {
        drawText(
            value,
            x + paddingX,
            y + paddingY,
            android.graphics.Paint().apply {
                color = android.graphics.Color.BLACK
                textSize = 30f
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val fakeFlow = flow {
        while (true) {
            emit((0..100).random()) // 随机生成整数
            delay(100)
        }
    }

    BouncingDotWithPath(waveMaxValue = 100, dotFlow = fakeFlow, modifier = Modifier)
}
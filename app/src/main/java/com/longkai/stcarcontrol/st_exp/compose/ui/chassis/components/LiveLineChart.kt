package com.longkai.stcarcontrol.st_exp.compose.ui.chassis.components

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.longkai.stcarcontrol.st_exp.R
import java.util.Locale

data class ChartPoint(val timestampMillis: Long, val value: Float)

data class ChartSeries(val points: List<ChartPoint>, val color: Color, val dashed: Boolean = false)

@Composable
fun LiveLineChart(
    series: List<ChartSeries>,
    range: ClosedFloatingPointRange<Float>,
    description: String,
    tag: String,
    modifier: Modifier = Modifier,
    windowSeconds: Int = 30,
    timeIntervals: Int = 2
) {
    require(windowSeconds > 0 && timeIntervals in 1..windowSeconds && windowSeconds % timeIntervals == 0)
    val timeLabels = (0..timeIntervals).map { index ->
        if (index == timeIntervals) stringResource(R.string.chassis_now)
        else stringResource(R.string.chassis_seconds_ago, windowSeconds * (timeIntervals - index) / timeIntervals)
    }
    val windowMillis = windowSeconds * 1_000L
    val endTime = series.mapNotNull { it.points.lastOrNull()?.timestampMillis }.maxOrNull() ?: 0L
    LineChart(
        series = series, range = range, timeRangeMillis = (endTime - windowMillis)..endTime,
        timeLabels = timeLabels, description = description, tag = tag, modifier = modifier
    )
}

@Composable
fun LineChart(
    series: List<ChartSeries>,
    range: ClosedFloatingPointRange<Float>,
    timeRangeMillis: LongRange,
    timeLabels: List<String>,
    description: String,
    tag: String,
    modifier: Modifier = Modifier
) {
    require(range.start < range.endInclusive)
    require(timeRangeMillis.first < timeRangeMillis.last && timeLabels.size >= 2)
    val density = LocalDensity.current
    val labelPaint = remember(density) {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = Color(0xFF91A9BB).toArgb()
            textSize = with(density) { 9.sp.toPx() }
        }
    }
    val startTime = timeRangeMillis.first
    val endTime = timeRangeMillis.last
    val windowMillis = endTime - startTime
    val visibleSeries = remember(series, startTime, endTime) {
        series.map { line ->
            val firstVisible = line.points.indexOfFirst { it.timestampMillis >= startTime }
            // Keep the preceding sample so the line crosses the left edge without a gap.
            line.copy(points = if (firstVisible < 0) emptyList()
                else line.points.subList((firstVisible - 1).coerceAtLeast(0), line.points.size))
        }
    }
    Box(modifier.testTag(tag).semantics {
        contentDescription = description
        text = AnnotatedString(timeLabels.joinToString(" "))
    }) {
        ChartGrid(range.start, range.endInclusive, timeLabels.joinToString("\n"), Modifier.matchParentSize())
        Canvas(Modifier.matchParentSize()) {
            val bounds = chartBounds(size, range.start, range.endInclusive, labelPaint)
            if (bounds.width <= 0f || bounds.height <= 0f) return@Canvas
            clipRect(bounds.left, bounds.top, bounds.right + 3.dp.toPx(), bounds.bottom) {
                visibleSeries.forEach { line ->
                    val path = Path()
                    var lastPoint: Offset? = null
                    line.points.forEachIndexed { index, point ->
                        val x = bounds.left + (point.timestampMillis - startTime).toFloat() / windowMillis * bounds.width
                        val y = bounds.bottom - (point.value.coerceIn(range.start, range.endInclusive) - range.start) /
                            (range.endInclusive - range.start) * bounds.height
                        if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                        lastPoint = Offset(x, y)
                    }
                    drawPath(path, line.color, style = Stroke(
                        width = 2.dp.toPx(),
                        pathEffect = if (line.dashed) PathEffect.dashPathEffect(floatArrayOf(8.dp.toPx(), 6.dp.toPx())) else null
                    ))
                    lastPoint?.let { drawCircle(line.color, 2.5.dp.toPx(), it) }
                }
            }
        }
    }
}

@Composable
private fun ChartGrid(minimum: Float, maximum: Float, timeLabelText: String, modifier: Modifier) {
    val density = LocalDensity.current
    val paint = remember(density) {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color(0xFF91A9BB).toArgb()
            textSize = with(density) { 9.sp.toPx() }
        }
    }
    val timeLabels = remember(timeLabelText) { timeLabelText.lines() }
    Spacer(modifier.graphicsLayer().drawWithCache {
        val bounds = chartBounds(size, minimum, maximum, paint)
        if (bounds.width <= 0f || bounds.height <= 0f) return@drawWithCache onDrawBehind {}
        val fontHeight = paint.fontMetrics.run { descent - ascent }
        val intervals = when {
            bounds.height >= (fontHeight + 4.dp.toPx()) * 4 -> 4
            bounds.height >= (fontHeight + 4.dp.toPx()) * 2 -> 2
            else -> 1
        }
        val horizontalLines = Path()
        val valueLabels = (0..intervals).map { i ->
            val y = bounds.top + bounds.height * i / intervals
            horizontalLines.moveTo(bounds.left, y)
            horizontalLines.lineTo(bounds.right, y)
            val value = maximum - (maximum - minimum) * i / intervals
            val text = if (value % 1f == 0f) value.toInt().toString() else String.format(Locale.US, "%.1f", value)
            text to Offset(bounds.left - 6.dp.toPx(), y + paint.textSize / 3)
        }
        val verticalLines = Path()
        val timePositions = timeLabels.indices.map { i ->
            val x = bounds.left + bounds.width * i / timeLabels.lastIndex
            verticalLines.moveTo(x, bounds.top)
            verticalLines.lineTo(x, bounds.bottom)
            Offset(x, size.height - 4.dp.toPx())
        }
        val gridColor = Color(0xFF7D99AA).copy(alpha = .22f)
        val dashed = Stroke(width = 1f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(3.dp.toPx(), 4.dp.toPx())))
        onDrawBehind {
            drawPath(horizontalLines, gridColor, style = dashed)
            drawPath(verticalLines, gridColor, style = Stroke(width = 1f))
            paint.textAlign = Paint.Align.RIGHT
            valueLabels.forEach { (label, position) ->
                drawContext.canvas.nativeCanvas.drawText(label, position.x, position.y, paint)
            }
            timeLabels.forEachIndexed { index, label ->
                paint.textAlign = if (index == timeLabels.lastIndex) Paint.Align.RIGHT else Paint.Align.CENTER
                val position = timePositions[index]
                drawContext.canvas.nativeCanvas.drawText(label, position.x, position.y, paint)
            }
        }
    })
}

private fun Density.chartBounds(size: Size, minimum: Float, maximum: Float, paint: Paint): Rect {
    val fontHeight = paint.fontMetrics.run { descent - ascent }
    return Rect(
        left = maxOf(42.dp.toPx(), paint.measureText(minimum.toInt().toString()) + 8.dp.toPx(),
            paint.measureText(maximum.toInt().toString()) + 8.dp.toPx()),
        top = maxOf(7.dp.toPx(), fontHeight / 2),
        right = size.width - 6.dp.toPx(),
        bottom = size.height - maxOf(20.dp.toPx(), fontHeight * 1.5f + 4.dp.toPx())
    )
}

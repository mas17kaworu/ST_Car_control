package com.longkai.stcarcontrol.st_exp.compose.ui.chassis.components

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
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
    require(range.start < range.endInclusive)
    val density = LocalDensity.current
    val labelPaint = remember(density) {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = Color(0xFF91A9BB).toArgb()
            textSize = with(density) { 9.sp.toPx() }
        }
    }
    val timeLabels = (0..timeIntervals).map { index ->
        if (index == timeIntervals) stringResource(R.string.chassis_now)
        else stringResource(R.string.chassis_seconds_ago, windowSeconds * (timeIntervals - index) / timeIntervals)
    }
    val windowMillis = windowSeconds * 1_000L
    val endTime = series.mapNotNull { it.points.lastOrNull()?.timestampMillis }.maxOrNull() ?: 0L
    val startTime = endTime - windowMillis
    val visibleSeries = remember(series, startTime, endTime) {
        series.map { line ->
            val firstVisible = line.points.indexOfFirst { it.timestampMillis >= startTime }
            // Keep the preceding sample so the line crosses the left edge without a gap.
            line.copy(points = if (firstVisible < 0) emptyList()
                else line.points.subList((firstVisible - 1).coerceAtLeast(0), line.points.size))
        }
    }
    Canvas(modifier.testTag(tag).semantics {
        contentDescription = description
        text = AnnotatedString(timeLabels.joinToString(" "))
    }) {
        val fontHeight = labelPaint.fontMetrics.run { descent - ascent }
        val left = maxOf(42.dp.toPx(), labelPaint.measureText(range.start.toInt().toString()) + 8.dp.toPx(),
            labelPaint.measureText(range.endInclusive.toInt().toString()) + 8.dp.toPx())
        val right = size.width - 6.dp.toPx()
        val top = maxOf(7.dp.toPx(), fontHeight / 2)
        val bottom = size.height - maxOf(20.dp.toPx(), fontHeight * 1.5f + 4.dp.toPx())
        if (right <= left || bottom <= top) return@Canvas
        val width = right - left
        val height = bottom - top
        val gridColor = Color(0xFF7D99AA).copy(alpha = .22f)
        val intervals = when {
            height >= (fontHeight + 4.dp.toPx()) * 4 -> 4
            height >= (fontHeight + 4.dp.toPx()) * 2 -> 2
            else -> 1
        }
        for (i in 0..intervals) {
            val y = top + height * i / intervals
            val value = range.endInclusive - (range.endInclusive - range.start) * i / intervals
            drawLine(
                gridColor,
                Offset(left, y),
                Offset(right, y),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(3.dp.toPx(), 4.dp.toPx()))
            )
            labelPaint.textAlign = Paint.Align.RIGHT
            val label = if (value % 1f == 0f) value.toInt().toString() else String.format(Locale.US, "%.1f", value)
            drawContext.canvas.nativeCanvas.drawText(label, left - 6.dp.toPx(), y + labelPaint.textSize / 3, labelPaint)
        }
        for (i in 0..timeIntervals) {
            val x = left + width * i / timeIntervals
            drawLine(gridColor, Offset(x, top), Offset(x, bottom))
            labelPaint.textAlign = if (i == timeIntervals) Paint.Align.RIGHT else Paint.Align.CENTER
            drawContext.canvas.nativeCanvas.drawText(timeLabels[i], x, size.height - 4.dp.toPx(), labelPaint)
        }
        clipRect(left, top, right + 3.dp.toPx(), bottom) {
            visibleSeries.forEach { line ->
                val path = Path()
                var lastPoint: Offset? = null
                line.points.forEachIndexed { index, point ->
                    val x = left + (point.timestampMillis - startTime).toFloat() / windowMillis * width
                    val y = bottom - (point.value.coerceIn(range.start, range.endInclusive) - range.start) /
                        (range.endInclusive - range.start) * height
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

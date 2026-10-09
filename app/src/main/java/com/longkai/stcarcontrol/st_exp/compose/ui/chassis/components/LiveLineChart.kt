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

@Composable
fun LiveLineChart(
    points: List<ChartPoint>,
    range: ClosedFloatingPointRange<Float>,
    color: Color,
    reading: String,
    unit: String,
    description: String,
    tag: String,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val labelPaint = remember(density) {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = Color(0xFF91A9BB).toArgb()
            textSize = with(density) { 9.sp.toPx() }
        }
    }
    val readingPaint = remember(density, color) {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color.toArgb()
            textSize = with(density) { 16.sp.toPx() }
        }
    }
    val unitPaint = remember(density) {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = Color(0xFFADC0CC).toArgb()
            textSize = with(density) { 10.sp.toPx() }
        }
    }
    val timeLabels = listOf(
        stringResource(R.string.chassis_seconds_ago, 30),
        stringResource(R.string.chassis_seconds_ago, 15),
        stringResource(R.string.chassis_now)
    )
    Canvas(modifier.testTag(tag).semantics {
        contentDescription = "$description: $reading $unit"
        text = AnnotatedString(reading)
    }) {
        val fontHeight = labelPaint.fontMetrics.run { descent - ascent }
        val readingHeight = readingPaint.fontMetrics.run { descent - ascent }
        val left = maxOf(42.dp.toPx(), labelPaint.measureText(range.start.toInt().toString()) + 8.dp.toPx(),
            labelPaint.measureText(range.endInclusive.toInt().toString()) + 8.dp.toPx())
        val valueWidth = readingPaint.measureText(reading)
        val unitGap = 4.dp.toPx()
        val pointGap = 6.dp.toPx()
        // Reserve only the measured label width, rather than a fixed empty column.
        val right = size.width - 4.dp.toPx() - valueWidth - unitGap - unitPaint.measureText(unit) - pointGap
        val top = maxOf(7.dp.toPx(), readingHeight / 2)
        val bottom = size.height - maxOf(20.dp.toPx(), readingHeight / 2 + fontHeight + 4.dp.toPx())
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
        for (i in 0..2) {
            val x = left + width * i / 2
            drawLine(gridColor, Offset(x, top), Offset(x, bottom))
            labelPaint.textAlign = if (i == 2) Paint.Align.RIGHT else Paint.Align.CENTER
            drawContext.canvas.nativeCanvas.drawText(timeLabels[i], x, size.height - 4.dp.toPx(), labelPaint)
        }
        val lastValue = points.lastOrNull()?.value
        val readingY = if (lastValue == null) (top + bottom) / 2
            else bottom - (lastValue.coerceIn(range.start, range.endInclusive) - range.start) /
                (range.endInclusive - range.start) * height
        val baseline = readingY - readingPaint.fontMetrics.run { (ascent + descent) / 2 }
        drawContext.canvas.nativeCanvas.drawText(reading, right + pointGap, baseline, readingPaint)
        drawContext.canvas.nativeCanvas.drawText(unit, right + pointGap + valueWidth + unitGap, baseline, unitPaint)
        if (points.isEmpty()) return@Canvas
        val endTime = points.last().timestampMillis
        val startTime = endTime - 30_000L
        val visiblePoints = points.filter { it.timestampMillis >= startTime }
        val path = Path()
        var lastPoint: Offset? = null
        visiblePoints.forEachIndexed { index, point ->
            val x = left + (point.timestampMillis - startTime).toFloat() / 30_000f * width
            val y = bottom - (point.value.coerceIn(range.start, range.endInclusive) - range.start) /
                (range.endInclusive - range.start) * height
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
            lastPoint = Offset(x, y)
        }
        clipRect(left, top, right + 3.dp.toPx(), bottom) {
            drawPath(path, color, style = Stroke(width = 2.dp.toPx()))
            lastPoint?.let { drawCircle(color, 2.5.dp.toPx(), it) }
        }
    }
}

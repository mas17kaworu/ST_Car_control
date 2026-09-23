package com.longkai.stcarcontrol.st_exp.compose.ui.chassis.components

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.longkai.stcarcontrol.st_exp.R
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun SpeedGauge(speedKph: Float?, modifier: Modifier = Modifier) {
    val density = LocalDensity.current
    val paint = remember(density) { Paint(Paint.ANTI_ALIAS_FLAG).apply { textAlign = Paint.Align.CENTER } }
    val valueText = speedKph?.roundToInt()?.toString() ?: stringResource(R.string.chassis_no_data)
    val unit = stringResource(R.string.chassis_unit_speed)
    val description = stringResource(R.string.chassis_speed)
    Canvas(modifier.testTag("chassis-speed-gauge").semantics { contentDescription = "$description: $valueText $unit" }) {
        val center = Offset(size.width / 2, size.height - 24.dp.toPx())
        val radius = min(size.width / 2 - 30.dp.toPx(), center.y - 18.dp.toPx())
        if (radius <= 0f) return@Canvas
        val arcStart = Offset(center.x - radius, center.y - radius)
        val arcSize = Size(radius * 2, radius * 2)
        val stroke = Stroke(8.dp.toPx(), cap = StrokeCap.Round)
        drawArc(Color(0xFF192832), 180f, 180f, false, arcStart, arcSize, style = stroke)
        val progress = abs(speedKph ?: 0f).coerceIn(0f, 160f) / 160f
        if (progress > 0f) {
            drawArc(Color(0xFF3ABEE5), 180f, progress * 180f, false, arcStart, arcSize, style = stroke)
        }
        paint.color = Color(0xFF92A9BA).toArgb()
        paint.textSize = with(density) { 9.sp.toPx() }
        for (i in 0..4) {
            val angle = Math.PI + Math.PI * i / 4
            val labelRadius = radius + 15.dp.toPx()
            val x = center.x + cos(angle).toFloat() * labelRadius
            val y = center.y + sin(angle).toFloat() * labelRadius
            drawContext.canvas.nativeCanvas.drawText((i * 40).toString(), x, y + paint.textSize / 3, paint)
        }
        paint.color = Color(0xFFF3F7FA).toArgb()
        paint.textSize = min(radius * .65f, with(density) { 48.sp.toPx() })
        drawContext.canvas.nativeCanvas.drawText(valueText, center.x, center.y - 5.dp.toPx(), paint)
        paint.color = Color(0xFF9BB3C4).toArgb()
        paint.textSize = with(density) { 11.sp.toPx() }
        drawContext.canvas.nativeCanvas.drawText(unit, center.x, center.y + 15.dp.toPx(), paint)
    }
}

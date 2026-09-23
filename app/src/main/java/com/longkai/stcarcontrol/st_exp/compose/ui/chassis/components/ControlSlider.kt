package com.longkai.stcarcontrol.st_exp.compose.ui.chassis.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.max
import kotlin.math.min

@Composable
fun ControlSlider(
    label: String,
    value: Float,
    valueLabel: String,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
    startLabel: String,
    endLabel: String,
    testTag: String,
    modifier: Modifier = Modifier,
    steps: Int = 0,
    centered: Boolean = false
) {
    val color = MaterialTheme.colors.secondary
    Column(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(label, color = Color(0xFFB8CAD5), fontSize = 11.sp, modifier = Modifier.weight(1f))
            Text(valueLabel, color = color, fontSize = 12.sp)
        }
        Box(Modifier.fillMaxWidth().height(48.dp)) {
            Canvas(Modifier.matchParentSize()) {
                val inset = 10.dp.toPx()
                val width = (size.width - inset * 2).coerceAtLeast(0f)
                val position = (value - range.start) / (range.endInclusive - range.start)
                val origin = if (centered) (0f - range.start) / (range.endInclusive - range.start) else 0f
                val y = size.height / 2
                drawLine(color.copy(alpha = .25f), Offset(inset, y), Offset(inset + width, y), 5.dp.toPx(), StrokeCap.Round)
                drawLine(
                    color,
                    Offset(inset + width * min(origin, position), y),
                    Offset(inset + width * max(origin, position), y),
                    5.dp.toPx(),
                    StrokeCap.Round
                )
                if (centered) {
                    val zeroX = inset + width * origin
                    drawLine(color, Offset(zeroX, y - 7.dp.toPx()), Offset(zeroX, y + 7.dp.toPx()), 1.dp.toPx())
                }
            }
            Slider(
                value = value,
                onValueChange = onValueChange,
                onValueChangeFinished = onValueChangeFinished,
                valueRange = range,
                steps = steps,
                colors = SliderDefaults.colors(
                    thumbColor = color,
                    activeTrackColor = Color.Transparent,
                    inactiveTrackColor = Color.Transparent,
                    activeTickColor = Color.Transparent,
                    inactiveTickColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
                    .testTag(testTag)
                    .semantics { contentDescription = label }
            )
        }
        Box(Modifier.fillMaxWidth().padding(horizontal = 10.dp)) {
            Text(startLabel, color = Color(0xFF92A8B8), fontSize = 10.sp, modifier = Modifier.align(Alignment.CenterStart))
            if (centered) {
                Text("0", color = Color(0xFFD5E5EA), fontSize = 10.sp, modifier = Modifier.align(Alignment.Center))
            }
            Text(endLabel, color = Color(0xFF92A8B8), fontSize = 10.sp, modifier = Modifier.align(Alignment.CenterEnd))
        }
    }
}

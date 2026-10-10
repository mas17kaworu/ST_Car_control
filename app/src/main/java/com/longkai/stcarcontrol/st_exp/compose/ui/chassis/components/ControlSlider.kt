package com.longkai.stcarcontrol.st_exp.compose.ui.chassis.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun ControlSlider(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
    startLabel: String,
    endLabel: String,
    testTag: String,
    modifier: Modifier = Modifier,
    steps: Int = 0,
    centered: Boolean = false,
    enabled: Boolean = true,
    interactionKey: Long = 0L,
    activeColor: Color = MaterialTheme.colors.secondary
) {
    val color = if (enabled) activeColor else Color(0xFF71838F)
    Row(modifier.height(48.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(startLabel, color = Color(0xFF92A8B8), fontSize = 10.sp)
        Box(Modifier.weight(1f).height(48.dp)) {
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
            key(interactionKey) {
                Slider(
                    enabled = enabled,
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
                        inactiveTickColor = Color.Transparent,
                        disabledThumbColor = color,
                        disabledActiveTrackColor = Color.Transparent,
                        disabledInactiveTrackColor = Color.Transparent,
                        disabledActiveTickColor = Color.Transparent,
                        disabledInactiveTickColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth()
                        .testTag(testTag)
                        .clearAndSetSemantics {
                            contentDescription = label
                            progressBarRangeInfo = ProgressBarRangeInfo(value, range, steps)
                            if (!enabled) disabled()
                            // Material 1.2 does not finish a gesture for accessibility progress.
                            setProgress { requested ->
                                if (!enabled || !requested.isFinite()) false
                                else {
                                    val clamped = requested.coerceIn(range.start, range.endInclusive)
                                    val increment = (range.endInclusive - range.start) / (steps + 1)
                                    val resolved = if (steps > 0) {
                                        range.start + ((clamped - range.start) / increment).roundToInt() * increment
                                    } else clamped
                                    if (resolved == value) false
                                    else {
                                        onValueChange(resolved)
                                        onValueChangeFinished()
                                        true
                                    }
                                }
                            }
                        }
                )
            }
            if (centered) {
                Text("0", color = Color(0xFFD5E5EA), fontSize = 9.sp, modifier = Modifier.align(Alignment.BottomCenter))
            }
        }
        Text(endLabel, color = Color(0xFF92A8B8), fontSize = 10.sp)
    }
}

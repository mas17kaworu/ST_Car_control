package com.longkai.stcarcontrol.st_exp.compose.ui.chassis

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.longkai.stcarcontrol.st_exp.R
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.ChassisControlTab
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.ChassisControlField
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.ChassisError
import com.longkai.stcarcontrol.st_exp.compose.ui.chassis.components.ChartPoint
import com.longkai.stcarcontrol.st_exp.compose.ui.chassis.components.ChartSeries
import com.longkai.stcarcontrol.st_exp.compose.ui.chassis.components.ChassisControlBar
import com.longkai.stcarcontrol.st_exp.compose.ui.chassis.components.ChassisValueInput
import com.longkai.stcarcontrol.st_exp.compose.ui.chassis.components.ControlSlider
import com.longkai.stcarcontrol.st_exp.compose.ui.chassis.components.LiveLineChart
import com.longkai.stcarcontrol.st_exp.compose.ui.chassis.components.SpeedGauge
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun ChassisScreen(
    state: ChassisUiState,
    onControlChanged: (ChassisControlField, Int, Long) -> Unit,
    onControlCommitted: (ChassisControlField, Long) -> Unit,
    onControlInputSubmitted: (ChassisControlField, String, Long) -> Unit,
    onCombinedBrakesChanged: (Int, Long) -> Unit,
    onCombinedBrakesInputSubmitted: (String, Long) -> Unit,
    onControlTabSelected: (ChassisControlTab) -> Unit,
    onEpbToggled: () -> Unit,
    onCurrentOffsetChanged: (Boolean) -> Unit,
    onEmergencyStopToggled: () -> Unit,
    onDismissError: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cyan = MaterialTheme.colors.primary
    val mint = MaterialTheme.colors.secondary
    val ehbColor = Color(0xFF45D6FF)
    val embColor = Color(0xFFFFBC5C)
    val angleSeries = remember(state.history, cyan) {
        listOf(ChartSeries(state.history.map { ChartPoint(it.timestampMillis, it.steeringAngleDegrees) }, cyan))
    }
    val brakeSeries = remember(state.history) {
        listOf(
            ChartSeries(state.history.map { ChartPoint(it.timestampMillis, it.ehbForceN) }, ehbColor, dashed = true),
            ChartSeries(state.history.map { ChartPoint(it.timestampMillis, it.embForceN) }, embColor)
        )
    }
    val angleTitle = stringResource(R.string.chassis_angle)
    val brakeTitle = stringResource(R.string.chassis_braking_force)
    val noData = stringResource(R.string.chassis_no_data)
    val forceUnit = stringResource(R.string.chassis_unit_force)
    val angleRange = state.config.steeringDegrees.let { it.first.toFloat()..it.last.toFloat() }
    val brakeRange = 0f..maxOf(state.config.ehbForceN.last, state.config.embForceN.last).toFloat()

    Column(
        modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0xFF414E57), Color(0xFF1E2530)))
        ).padding(horizontal = 12.dp, vertical = 8.dp).testTag("chassis-screen"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                stringResource(R.string.chassis_title),
                fontSize = 20.sp,
                fontStyle = FontStyle.Italic,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
            Text(
                stringResource(R.string.chassis_ramp, state.telemetry?.rampPercent?.toString() ?: noData),
                fontSize = 14.sp,
                color = Color(0xFFB8CAD5),
                maxLines = 1,
                modifier = Modifier.testTag("chassis-ramp-reading")
            )
            if (state.isDemo) {
                Text(
                    stringResource(R.string.chassis_demo),
                    fontSize = 10.sp,
                    color = mint,
                )
            }
        }
        Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ChassisCard(
                    title = stringResource(R.string.chassis_speed),
                    modifier = Modifier.weight(1f).testTag("chassis-speed-card"),
                    input = {
                        ChassisInput(state, ChassisControlField.Speed, onControlChanged, onControlInputSubmitted,
                            stringResource(R.string.chassis_speed_command), stringResource(R.string.chassis_unit_speed), "chassis-speed-input")
                    },
                    controls = {
                        ChassisSlider(
                            state = state,
                            field = ChassisControlField.Speed,
                            onChanged = onControlChanged,
                            onCommitted = onControlCommitted,
                            label = stringResource(R.string.chassis_speed_command),
                            testTag = "chassis-speed-slider"
                        )
                    }
                ) {
                    SpeedGauge(state.telemetry?.speedKph, 0..state.config.speedKph.last, Modifier.fillMaxSize())
                }
                ChassisCard(
                    title = angleTitle,
                    modifier = Modifier.weight(1f).testTag("chassis-angle-card"),
                    input = {
                        ChassisInput(state, ChassisControlField.Steering, onControlChanged, onControlInputSubmitted,
                            stringResource(R.string.chassis_angle_command), stringResource(R.string.chassis_unit_angle), "chassis-angle-input")
                    },
                    controls = {
                        ChassisSlider(
                            state = state,
                            field = ChassisControlField.Steering,
                            onChanged = onControlChanged,
                            onCommitted = onControlCommitted,
                            label = stringResource(R.string.chassis_angle_command),
                            testTag = "chassis-angle-slider"
                        )
                    }
                ) {
                    ChassisReading(
                        reading = state.telemetry?.steeringAngleDegrees?.let { String.format(Locale.US, "%+.2f", it) } ?: noData,
                        unit = stringResource(R.string.chassis_unit_angle), color = cyan,
                        tag = "chassis-angle-reading", modifier = Modifier.fillMaxWidth().height(24.dp)
                    )
                    LiveLineChart(
                        series = angleSeries, range = angleRange,
                        description = angleTitle, tag = "chassis-angle-chart",
                        modifier = Modifier.fillMaxWidth().weight(1f)
                    )
                }
            }
            ChassisCard(
                title = brakeTitle,
                modifier = Modifier.weight(1f).testTag("chassis-braking-card"),
                input = {},
                controls = {
                    Column {
                        BrakeControlRow(
                            state, ChassisControlField.Ehb, onControlChanged, onControlCommitted, onControlInputSubmitted,
                            stringResource(R.string.chassis_ehb_short), stringResource(R.string.chassis_ehb_control),
                            ehbColor, "ehb"
                        )
                        Divider(color = Color.White.copy(alpha = .06f))
                        BrakeControlRow(
                            state, ChassisControlField.Emb, onControlChanged, onControlCommitted, onControlInputSubmitted,
                            stringResource(R.string.chassis_emb_short), stringResource(R.string.chassis_emb_control),
                            embColor, "emb"
                        )
                        Divider(color = Color.White.copy(alpha = .06f))
                        Row(
                            Modifier.fillMaxWidth().height(48.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val combinedLabel = stringResource(R.string.chassis_combined_brakes)
                            val generation = state.generation(ChassisControlField.Ehb)
                            Text(combinedLabel, color = Color(0xFFD5E4ED), fontSize = 12.sp, maxLines = 1)
                            ChassisValueInput(
                                value = state.controls.ehbForceN.takeIf { it == state.controls.embForceN },
                                range = state.config.combinedBrakeForceN,
                                enabled = state.canControl(ChassisControlField.Ehb),
                                interactionKey = generation,
                                label = combinedLabel, unit = forceUnit,
                                onValueChange = { onCombinedBrakesChanged(it, generation) },
                                onSubmit = { onCombinedBrakesInputSubmitted(it, generation) },
                                modifier = Modifier.testTag("chassis-combined-brakes-input")
                            )
                            Spacer(Modifier.weight(1f))
                            val offsetLabel = stringResource(R.string.chassis_current_offset)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(offsetLabel, color = Color(0xFFB8CAD5), fontSize = 10.sp, maxLines = 1)
                                Switch(
                                    checked = state.currentOffsetEnabled,
                                    onCheckedChange = onCurrentOffsetChanged,
                                    colors = SwitchDefaults.colors(checkedThumbColor = cyan),
                                    modifier = Modifier.testTag("chassis-current-offset")
                                        .semantics { contentDescription = offsetLabel }
                                )
                            }
                        }
                    }
                }
            ) {
                Row(
                    Modifier.fillMaxWidth().height(44.dp).padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    BrakeReading(
                        title = stringResource(R.string.chassis_ehb_short),
                        reading = state.telemetry?.ehbForceN?.let { String.format(Locale.US, "%.0f", it) } ?: noData,
                        color = ehbColor, dashed = true, tag = "chassis-ehb-reading", modifier = Modifier.weight(1f)
                    )
                    BrakeReading(
                        title = stringResource(R.string.chassis_emb_short),
                        reading = state.telemetry?.embForceN?.let { String.format(Locale.US, "%.0f", it) } ?: noData,
                        color = embColor, dashed = false, tag = "chassis-emb-reading", modifier = Modifier.weight(1f)
                    )
                }
                LiveLineChart(
                    series = brakeSeries, range = brakeRange,
                    description = brakeTitle, tag = "chassis-braking-chart",
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    windowSeconds = 3, timeIntervals = 3
                )
            }
        }
        ChassisControlBar(
            enabledControls = state.enabledControls,
            onSelected = onControlTabSelected,
            epbEnabled = state.epbEnabled,
            onEpbToggled = onEpbToggled,
            emergencyStopEnabled = state.emergencyStopEnabled,
            onEmergencyStopToggled = onEmergencyStopToggled,
            modifier = Modifier.fillMaxWidth()
        )
        Text(stringResource(R.string.chassis_control_hint), color = Color(0xFF94AABB), fontSize = 9.sp)
    }
    state.error?.let { error ->
        AlertDialog(
            onDismissRequest = onDismissError,
            text = {
                Text(stringResource(when (error) {
                    ChassisError.InvalidControl -> R.string.chassis_invalid_control
                    ChassisError.NotReady -> R.string.chassis_not_ready
                    ChassisError.CommandFailed -> R.string.chassis_command_failed
                }))
            },
            confirmButton = {
                TextButton(onClick = onDismissError) { Text(stringResource(R.string.chassis_dismiss)) }
            }
        )
    }
}

@Composable
private fun ChassisReading(reading: String, unit: String, color: Color, tag: String, modifier: Modifier = Modifier) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.End) {
        Text(reading, color = color, fontSize = 20.sp, maxLines = 1,
            modifier = Modifier.testTag(tag).semantics { contentDescription = "$reading $unit" })
        Text(unit, color = Color(0xFFADC0CC), fontSize = 10.sp, modifier = Modifier.padding(start = 5.dp))
    }
}

@Composable
private fun BrakeReading(title: String, reading: String, color: Color, dashed: Boolean, tag: String, modifier: Modifier) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Canvas(Modifier.width(24.dp).height(10.dp)) {
            drawLine(color, Offset(0f, size.height / 2), Offset(size.width, size.height / 2), 2.dp.toPx(),
                pathEffect = if (dashed) PathEffect.dashPathEffect(floatArrayOf(8.dp.toPx(), 6.dp.toPx())) else null)
        }
        Text(title, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        ChassisReading(reading, stringResource(R.string.chassis_unit_force), color, tag, Modifier.weight(1f))
    }
}

@Composable
private fun BrakeControlRow(
    state: ChassisUiState,
    field: ChassisControlField,
    onChanged: (ChassisControlField, Int, Long) -> Unit,
    onCommitted: (ChassisControlField, Long) -> Unit,
    onSubmitted: (ChassisControlField, String, Long) -> Unit,
    title: String,
    label: String,
    color: Color,
    tag: String
) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        ChassisSlider(state, field, onChanged, onCommitted, label, "chassis-$tag-slider",
            modifier = Modifier.weight(1f), activeColor = color)
        ChassisInput(state, field, onChanged, onSubmitted, label,
            stringResource(R.string.chassis_unit_force), "chassis-$tag-input")
    }
}

@Composable
private fun ChassisCard(
    title: String,
    modifier: Modifier,
    input: @Composable () -> Unit,
    controls: @Composable () -> Unit,
    body: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier.fillMaxSize(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF303E49).copy(alpha = .85f),
        border = BorderStroke(1.dp, Color(0xFFA9C2D2).copy(alpha = .12f))
    ) {
        Column {
            Row(
                Modifier.fillMaxWidth().height(48.dp).padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium,
                    maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f)
                )
                input()
            }
            Column(Modifier.fillMaxWidth().weight(1f).padding(horizontal = 8.dp), content = body)
            Divider(color = Color.White.copy(alpha = .06f))
            Box(Modifier.fillMaxWidth().background(Color(0xFF22313C).copy(alpha = .45f)).padding(horizontal = 12.dp)) {
                controls()
            }
        }
    }
}

@Composable
private fun ChassisInput(
    state: ChassisUiState,
    field: ChassisControlField,
    onChanged: (ChassisControlField, Int, Long) -> Unit,
    onSubmitted: (ChassisControlField, String, Long) -> Unit,
    label: String,
    unit: String,
    tag: String
) {
    val generation = state.generation(field)
    ChassisValueInput(
        value = state.controls.value(field), range = state.config.range(field),
        enabled = state.canControl(field), interactionKey = generation, label = label, unit = unit,
        onValueChange = { onChanged(field, it, generation) },
        onSubmit = { onSubmitted(field, it, generation) },
        modifier = Modifier.testTag(tag)
    )
}

@Composable
private fun ChassisSlider(
    state: ChassisUiState,
    field: ChassisControlField,
    onChanged: (ChassisControlField, Int, Long) -> Unit,
    onCommitted: (ChassisControlField, Long) -> Unit,
    label: String,
    testTag: String,
    modifier: Modifier = Modifier,
    activeColor: Color = MaterialTheme.colors.secondary,
) {
    val range = state.config.range(field)
    val generation = state.generation(field)
    ControlSlider(
        label = label,
        value = state.controls.value(field).toFloat(),
        range = range.first.toFloat()..range.last.toFloat(),
        onValueChange = { onChanged(field, it.roundToInt(), generation) },
        onValueChangeFinished = { onCommitted(field, generation) },
        startLabel = range.first.toString(),
        endLabel = range.last.toString(),
        steps = range.last - range.first - 1,
        centered = range.first < 0,
        enabled = state.canControl(field),
        interactionKey = generation,
        testTag = testTag,
        modifier = modifier,
        activeColor = activeColor,
    )
}

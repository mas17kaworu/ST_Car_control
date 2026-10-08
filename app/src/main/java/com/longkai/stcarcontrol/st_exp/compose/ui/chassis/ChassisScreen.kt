package com.longkai.stcarcontrol.st_exp.compose.ui.chassis

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.longkai.stcarcontrol.st_exp.compose.ui.chassis.components.ChassisControlBar
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
    onControlTabSelected: (ChassisControlTab) -> Unit,
    onEpbToggled: () -> Unit,
    onCurrentOffsetChanged: (Boolean) -> Unit,
    onEmergencyStopToggled: () -> Unit,
    onDismissError: () -> Unit,
    modifier: Modifier = Modifier
) {
    val anglePoints = remember(state.history) {
        state.history.map { ChartPoint(it.timestampMillis, it.steeringAngleDegrees) }
    }
    val ehbPoints = remember(state.history) {
        state.history.map { ChartPoint(it.timestampMillis, it.ehbForceN) }
    }
    val brakePoints = remember(state.history) {
        state.history.map { ChartPoint(it.timestampMillis, it.embForceN) }
    }
    val cyan = MaterialTheme.colors.primary
    val mint = MaterialTheme.colors.secondary
    val angleTitle = stringResource(R.string.chassis_angle)
    val ehbTitle = stringResource(R.string.chassis_ehb)
    val brakeTitle = stringResource(R.string.chassis_emb)
    val noData = stringResource(R.string.chassis_no_data)
    val angleRange = state.config.steeringDegrees.let { it.first.toFloat()..it.last.toFloat() }
    val ehbRange = state.config.ehbForceN.let { it.first.toFloat()..it.last.toFloat() }
    val embRange = state.config.embForceN.let { it.first.toFloat()..it.last.toFloat() }

    BoxWithConstraints(
        modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0xFF414E57), Color(0xFF1E2530)))
        )
    ) {
        val contentHeight = maxOf(maxHeight, 680.dp)
        Column(
            Modifier.fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .height(contentHeight)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    stringResource(R.string.chassis_title),
                    fontSize = 24.sp,
                    fontStyle = FontStyle.Italic,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
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
                ChassisCard(
                    title = stringResource(R.string.chassis_speed),
                    modifier = Modifier.weight(1f),
                    controls = {
                        ChassisSlider(
                            state = state,
                            field = ChassisControlField.Speed,
                            onChanged = onControlChanged,
                            onCommitted = onControlCommitted,
                            label = stringResource(R.string.chassis_speed_command),
                            unit = stringResource(R.string.chassis_unit_speed),
                            testTag = "chassis-speed-slider"
                        )
                    }
                ) {
                    SpeedGauge(state.telemetry?.speedKph, state.config.speedKph.last, Modifier.fillMaxSize())
                }
                ChassisCard(
                    title = angleTitle,
                    modifier = Modifier.weight(1f),
                    controls = {
                        ChassisSlider(
                            state = state,
                            field = ChassisControlField.Steering,
                            onChanged = onControlChanged,
                            onCommitted = onControlCommitted,
                            label = stringResource(R.string.chassis_angle_command),
                            unit = stringResource(R.string.chassis_unit_angle),
                            testTag = "chassis-angle-slider"
                        )
                    }
                ) {
                    ChartReading(
                        value = state.telemetry?.steeringAngleDegrees?.let { String.format(Locale.US, "%+.2f", it) } ?: noData,
                        unit = stringResource(R.string.chassis_unit_angle),
                        color = cyan,
                        tag = "chassis-angle-reading"
                    )
                    LiveLineChart(
                        points = anglePoints, range = angleRange, color = cyan,
                        description = angleTitle, tag = "chassis-angle-chart",
                        modifier = Modifier.fillMaxWidth().weight(1f)
                    )
                }
            }
            Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ChassisCard(
                    title = ehbTitle,
                    modifier = Modifier.weight(1f),
                    controls = {
                        ChassisSlider(
                            state = state,
                            field = ChassisControlField.Ehb,
                            onChanged = onControlChanged,
                            onCommitted = onControlCommitted,
                            label = stringResource(R.string.chassis_ehb_control),
                            unit = stringResource(R.string.chassis_unit_force),
                            testTag = "chassis-ehb-slider"
                        )
                    }
                ) {
                    ChartReading(
                        value = state.telemetry?.ehbForceN?.let { String.format(Locale.US, "%.0f", it) } ?: noData,
                        unit = stringResource(R.string.chassis_unit_force),
                        color = mint,
                        tag = "chassis-ehb-reading"
                    )
                    LiveLineChart(
                        points = ehbPoints, range = ehbRange, color = mint,
                        description = ehbTitle, tag = "chassis-ehb-chart",
                        modifier = Modifier.fillMaxWidth().weight(1f)
                    )
                }
                ChassisCard(
                    title = brakeTitle,
                    modifier = Modifier.weight(1f),
                    controls = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            ChassisSlider(
                                state = state,
                                field = ChassisControlField.Emb,
                                onChanged = onControlChanged,
                                onCommitted = onControlCommitted,
                                label = stringResource(R.string.chassis_emb_control),
                                unit = stringResource(R.string.chassis_unit_force),
                                testTag = "chassis-emb-slider",
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(Modifier.width(12.dp))
                            val offsetLabel = stringResource(R.string.chassis_current_offset)
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(offsetLabel, color = Color(0xFFB8CAD5), fontSize = 10.sp)
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
                ) {
                    ChartReading(
                        value = state.telemetry?.embForceN?.let { String.format(Locale.US, "%.0f", it) } ?: noData,
                        unit = stringResource(R.string.chassis_unit_force),
                        color = cyan,
                        tag = "chassis-emb-reading"
                    )
                    LiveLineChart(
                        points = brakePoints, range = embRange, color = cyan,
                        description = brakeTitle, tag = "chassis-emb-chart",
                        modifier = Modifier.fillMaxWidth().weight(1f)
                    )
                }
            }
            ChassisControlBar(
                selected = state.selectedControl,
                onSelected = onControlTabSelected,
                epbEnabled = state.epbEnabled,
                onEpbToggled = onEpbToggled,
                emergencyStopEnabled = state.emergencyStopEnabled,
                onEmergencyStopToggled = onEmergencyStopToggled,
                modifier = Modifier.fillMaxWidth()
            )
            Text(stringResource(R.string.chassis_control_hint), color = Color(0xFF94AABB), fontSize = 9.sp)
        }
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
private fun ChassisCard(
    title: String,
    modifier: Modifier,
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
            Text(
                title,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
            )
            Column(Modifier.fillMaxWidth().weight(1f).padding(horizontal = 8.dp), content = body)
            Divider(color = Color.White.copy(alpha = .06f))
            Box(Modifier.fillMaxWidth().background(Color(0xFF22313C).copy(alpha = .45f)).padding(horizontal = 14.dp, vertical = 8.dp)) {
                controls()
            }
        }
    }
}

@Composable
private fun ChartReading(value: String, unit: String, color: Color, tag: String) {
    Row(Modifier.padding(horizontal = 12.dp).height(30.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.width(14.dp).height(2.dp).background(color))
        Text(value, color = color, fontSize = 24.sp, modifier = Modifier.padding(start = 8.dp).testTag(tag))
        Text(unit, color = Color(0xFFADBEC9), fontSize = 11.sp, modifier = Modifier.padding(start = 5.dp))
    }
}

@Composable
private fun ChassisSlider(
    state: ChassisUiState,
    field: ChassisControlField,
    onChanged: (ChassisControlField, Int, Long) -> Unit,
    onCommitted: (ChassisControlField, Long) -> Unit,
    label: String,
    unit: String,
    testTag: String,
    modifier: Modifier = Modifier,
) {
    val range = state.config.range(field)
    val generation = state.controlGeneration
    ControlSlider(
        label = label,
        value = state.controls.value(field).toFloat(),
        valueLabel = "${state.controls.value(field)} $unit",
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
    )
}

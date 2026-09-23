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
    onSpeedStepChanged: (Int) -> Unit,
    onSteeringStepChanged: (Int) -> Unit,
    onEhbLevelChanged: (Float) -> Unit,
    onEmbLevelChanged: (Float) -> Unit,
    onControlsCommitted: () -> Unit,
    onCurrentOffsetChanged: (Boolean) -> Unit,
    onControlTabSelected: (ChassisControlTab) -> Unit,
    onDismissError: () -> Unit,
    modifier: Modifier = Modifier
) {
    val anglePoints = remember(state.history) {
        state.history.map { ChartPoint(it.timestampMillis, it.steeringAngleDegrees) }
    }
    val pressurePoints = remember(state.history) {
        state.history.map { ChartPoint(it.timestampMillis, it.ehbPressureMpa) }
    }
    val brakePoints = remember(state.history) {
        state.history.map { ChartPoint(it.timestampMillis, it.embValue) }
    }
    val cyan = MaterialTheme.colors.primary
    val mint = MaterialTheme.colors.secondary
    val angleTitle = stringResource(R.string.chassis_angle)
    val pressureTitle = stringResource(R.string.chassis_ehb)
    val brakeTitle = stringResource(R.string.chassis_emb)
    val negativeSteps = stringResource(R.string.chassis_negative_steps)
    val positiveSteps = stringResource(R.string.chassis_positive_steps)
    val maximum = stringResource(R.string.chassis_max)
    val noData = stringResource(R.string.chassis_no_data)
    val speedRange = state.config.speedMinStep.toFloat()..state.config.speedMaxStep.toFloat()
    val angleRange = state.config.steeringMinStep.toFloat()..state.config.steeringMaxStep.toFloat()

    BoxWithConstraints(
        modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0xFF414E57), Color(0xFF1E2530)))
        )
    ) {
        val contentHeight = maxOf(maxHeight, 640.dp)
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
                        modifier = Modifier.testTag("chassis-demo-mode")
                    )
                }
            }
            Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ChassisCard(
                    title = stringResource(R.string.chassis_speed),
                    modifier = Modifier.weight(1f),
                    controls = {
                        ControlSlider(
                            label = stringResource(R.string.chassis_speed_command),
                            value = state.controls.speedStep.toFloat(),
                            valueLabel = signedStep(state.controls.speedStep),
                            range = speedRange,
                            onValueChange = { onSpeedStepChanged(it.roundToInt()) },
                            onValueChangeFinished = onControlsCommitted,
                            startLabel = negativeSteps,
                            endLabel = positiveSteps,
                            steps = state.config.speedMaxStep - state.config.speedMinStep - 1,
                            centered = true,
                            testTag = "chassis-speed-slider"
                        )
                    }
                ) {
                    SpeedGauge(state.telemetry?.speedKph, Modifier.fillMaxSize())
                }
                ChassisCard(
                    title = angleTitle,
                    modifier = Modifier.weight(1f),
                    controls = {
                        ControlSlider(
                            label = stringResource(R.string.chassis_angle_command),
                            value = state.controls.steeringStep.toFloat(),
                            valueLabel = signedStep(state.controls.steeringStep),
                            range = angleRange,
                            onValueChange = { onSteeringStepChanged(it.roundToInt()) },
                            onValueChangeFinished = onControlsCommitted,
                            startLabel = negativeSteps,
                            endLabel = positiveSteps,
                            steps = state.config.steeringMaxStep - state.config.steeringMinStep - 1,
                            centered = true,
                            testTag = "chassis-angle-slider"
                        )
                    }
                ) {
                    ChartReading(
                        value = state.telemetry?.steeringAngleDegrees?.let { String.format(Locale.US, "%+.1f", it) } ?: noData,
                        unit = stringResource(R.string.chassis_unit_angle),
                        color = cyan,
                        tag = "chassis-angle-reading"
                    )
                    LiveLineChart(
                        points = anglePoints, range = -30f..30f, color = cyan,
                        description = angleTitle, tag = "chassis-angle-chart",
                        modifier = Modifier.fillMaxWidth().weight(1f)
                    )
                }
            }
            Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ChassisCard(
                    title = pressureTitle,
                    modifier = Modifier.weight(1f),
                    controls = {
                        ControlSlider(
                            label = stringResource(R.string.chassis_ehb_control),
                            value = state.controls.ehbLevel,
                            valueLabel = stringResource(R.string.chassis_demo_level, (state.controls.ehbLevel * 100).roundToInt()),
                            range = 0f..1f,
                            onValueChange = onEhbLevelChanged,
                            onValueChangeFinished = onControlsCommitted,
                            startLabel = "0",
                            endLabel = maximum,
                            testTag = "chassis-ehb-slider"
                        )
                    }
                ) {
                    ChartReading(
                        value = state.telemetry?.ehbPressureMpa?.let { String.format(Locale.US, "%.1f", it) } ?: noData,
                        unit = stringResource(R.string.chassis_unit_pressure),
                        color = mint,
                        tag = "chassis-ehb-reading"
                    )
                    LiveLineChart(
                        points = pressurePoints, range = 0f..12f, color = mint,
                        description = pressureTitle, tag = "chassis-ehb-chart",
                        modifier = Modifier.fillMaxWidth().weight(1f)
                    )
                }
                ChassisCard(
                    title = brakeTitle,
                    modifier = Modifier.weight(1f),
                    controls = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            ControlSlider(
                                label = stringResource(R.string.chassis_emb_control),
                                value = state.controls.embLevel,
                                valueLabel = stringResource(R.string.chassis_demo_level, (state.controls.embLevel * 100).roundToInt()),
                                range = 0f..1f,
                                onValueChange = onEmbLevelChanged,
                                onValueChangeFinished = onControlsCommitted,
                                startLabel = "0",
                                endLabel = maximum,
                                testTag = "chassis-emb-slider",
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(Modifier.width(12.dp))
                            val offsetLabel = stringResource(R.string.chassis_current_offset)
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(offsetLabel, color = Color(0xFFB8CAD5), fontSize = 10.sp)
                                Switch(
                                    checked = state.controls.currentOffsetEnabled,
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
                        value = state.telemetry?.embValue?.let { String.format(Locale.US, "%.1f", it) } ?: noData,
                        unit = stringResource(R.string.chassis_value),
                        color = cyan,
                        tag = "chassis-emb-reading"
                    )
                    LiveLineChart(
                        points = brakePoints, range = 0f..30f, color = cyan,
                        description = brakeTitle, tag = "chassis-emb-chart",
                        modifier = Modifier.fillMaxWidth().weight(1f)
                    )
                }
            }
            ChassisControlBar(state.selectedControl, onControlTabSelected, Modifier.fillMaxWidth())
            if (state.isDemo) {
                Text(stringResource(R.string.chassis_demo_limits), color = Color(0xFF94AABB), fontSize = 9.sp)
            }
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

private fun signedStep(value: Int): String = if (value > 0) "+$value" else value.toString()

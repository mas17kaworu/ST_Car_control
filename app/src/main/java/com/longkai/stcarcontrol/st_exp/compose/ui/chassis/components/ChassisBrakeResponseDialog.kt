package com.longkai.stcarcontrol.st_exp.compose.ui.chassis.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.longkai.stcarcontrol.st_exp.R
import com.longkai.stcarcontrol.st_exp.compose.ui.chassis.ChassisBrakeResponse
import com.longkai.stcarcontrol.st_exp.compose.ui.chassis.ChassisViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChassisBrakeResponseDialog(
    response: ChassisBrakeResponse,
    forceRange: ClosedFloatingPointRange<Float>,
    onDismiss: () -> Unit,
) {
    val configuration = LocalConfiguration.current
    val title = stringResource(R.string.chassis_brake_response_title)
    val closeLabel = stringResource(R.string.chassis_brake_response_close)
    val ehbColor = Color(0xFF45D6FF)
    val embColor = Color(0xFFFFBC5C)
    val series = remember(response) {
        listOf(
            ChartSeries(response.samples.map {
                ChartPoint(it.timestampMillis - response.startedAtMillis, it.ehbForceN)
            }, ehbColor, dashed = true),
            ChartSeries(response.samples.map {
                ChartPoint(it.timestampMillis - response.startedAtMillis, it.embForceN)
            }, embColor)
        )
    }
    val duration = ChassisViewModel.BRAKE_RESPONSE_WINDOW_MILLIS
    val timeLabels = (0..4).map {
        if (it == 4) stringResource(R.string.chassis_milliseconds, duration.toInt())
        else (it * duration / 4).toString()
    }
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            modifier = Modifier
                .width((configuration.screenWidthDp.dp * .84f).coerceAtMost(1060.dp))
                .height((configuration.screenHeightDp.dp * .8f).coerceAtMost(560.dp))
                .testTag("chassis-brake-response-dialog"),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF2B3A45),
            border = BorderStroke(1.dp, Color(0xFF86A7BA).copy(alpha = .33f)),
            elevation = 16.dp
        ) {
            Column(
                Modifier.fillMaxSize()
                    .background(Brush.linearGradient(listOf(Color(0xFF344550), Color(0xFF26343F))))
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        title, color = Color.White, fontSize = 22.sp,
                        maxLines = 1, overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(48.dp)
                            .background(Color(0xFF21313C), RoundedCornerShape(8.dp))
                            .testTag("chassis-brake-response-close")
                            .semantics { contentDescription = closeLabel }
                    ) {
                        Canvas(Modifier.size(18.dp)) {
                            drawLine(Color(0xFFC7D9E4), Offset.Zero, Offset(size.width, size.height), 1.5.dp.toPx())
                            drawLine(Color(0xFFC7D9E4), Offset(size.width, 0f), Offset(0f, size.height), 1.5.dp.toPx())
                        }
                    }
                }
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ChartLegend(stringResource(R.string.chassis_emb_short), embColor, dashed = false)
                    ChartLegend(stringResource(R.string.chassis_ehb_short), ehbColor, dashed = true)
                }
                Surface(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    color = Color(0xFF1E2D37).copy(alpha = .5f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFF8AA7B6).copy(alpha = .17f))
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(
                            stringResource(R.string.chassis_unit_force),
                            color = Color(0xFFA9BFCC), fontSize = 12.sp,
                            modifier = Modifier.padding(start = 42.dp)
                        )
                        Box(Modifier.fillMaxWidth().weight(1f)) {
                            LineChart(
                                series = series, range = forceRange,
                                timeRangeMillis = 0L..duration, timeLabels = timeLabels,
                                description = title, tag = "chassis-brake-response-chart",
                                modifier = Modifier.fillMaxSize(), clampValuesToRange = false
                            )
                            if (response.samples.isEmpty()) {
                                Text(
                                    stringResource(R.string.chassis_brake_response_no_data),
                                    color = Color(0xFF9FB4C1), fontSize = 14.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.align(Alignment.Center).padding(16.dp)
                                        .testTag("chassis-brake-response-status")
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

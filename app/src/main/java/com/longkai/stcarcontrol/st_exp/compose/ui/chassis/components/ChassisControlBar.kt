package com.longkai.stcarcontrol.st_exp.compose.ui.chassis.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.longkai.stcarcontrol.st_exp.R
import com.longkai.stcarcontrol.st_exp.compose.data.chassis.ChassisControlTab

@Composable
fun ChassisControlBar(
    selected: ChassisControlTab?,
    onSelected: (ChassisControlTab) -> Unit,
    epbEnabled: Boolean,
    onEpbToggled: () -> Unit,
    emergencyStopEnabled: Boolean,
    onEmergencyStopToggled: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier.height(76.dp).testTag("chassis-control-bar")) {
        Row(
            Modifier.align(Alignment.Center).padding(horizontal = 80.dp)
                .widthIn(max = 472.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ChassisControlTab.values().forEach { tab ->
                val label = stringResource(
                    when (tab) {
                        ChassisControlTab.Vehicle -> R.string.chassis_vehicle_control
                        ChassisControlTab.Steering -> R.string.chassis_steering_control
                        ChassisControlTab.BrakePedal -> R.string.chassis_brake_pedal
                        ChassisControlTab.Epb -> R.string.chassis_epb
                    }
                )
                val active = if (tab == ChassisControlTab.Epb) epbEnabled else selected == tab
                val color = if (active) Color(0xFF3ABEE5) else Color(0xFFB7C9D5)
                Surface(
                    color = if (active) Color(0xFF2A4D5E) else Color(0xFF303D49),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, color.copy(alpha = if (active) .7f else .15f)),
                    modifier = Modifier.weight(1f).height(48.dp)
                        .testTag("chassis-control-${tab.name}")
                        .selectable(active, role = Role.Button, onClick = {
                            if (tab == ChassisControlTab.Epb) onEpbToggled() else onSelected(tab)
                        })
                ) {
                    Row(
                        Modifier.padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ControlIcon(tab, color, Modifier.size(20.dp))
                        Text(
                            label, color = Color.White, fontSize = 11.sp,
                            maxLines = 1, overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(start = 6.dp)
                        )
                    }
                }
            }
        }
        Box(
            Modifier.align(Alignment.CenterEnd).width(72.dp),
            contentAlignment = Alignment.Center
        ) {
            val stopDescription = stringResource(R.string.chassis_emergency_stop)
            val interactionSource = remember { MutableInteractionSource() }
            val pressed by interactionSource.collectIsPressedAsState()
            val buttonScale by animateFloatAsState(if (pressed) .92f else 1f, tween(100))
            Box(
                Modifier.size(56.dp).testTag("chassis-emergency-stop")
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        role = Role.Button,
                        onClick = onEmergencyStopToggled
                    )
                    .semantics {
                        contentDescription = stopDescription
                        this.selected = emergencyStopEnabled
                    }
            ) {
                Surface(
                    color = when {
                        pressed && emergencyStopEnabled -> Color(0xFF9B272C)
                        pressed -> Color(0xFF202B35)
                        emergencyStopEnabled -> Color(0xFFEF5350)
                        else -> Color(0xFF303D49)
                    },
                    shape = CircleShape,
                    border = if (emergencyStopEnabled) BorderStroke(3.dp, Color.White)
                        else BorderStroke(2.dp, Color(0xFFEF5350)),
                    elevation = if (pressed) 0.dp else 4.dp,
                    modifier = Modifier.fillMaxSize().graphicsLayer {
                        scaleX = buttonScale
                        scaleY = buttonScale
                    }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            stringResource(R.string.chassis_stop),
                            color = if (emergencyStopEnabled) Color.White else Color(0xFFEF5350),
                            fontSize = 12.sp, fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ControlIcon(tab: ChassisControlTab, color: Color, modifier: Modifier) {
    Canvas(modifier) {
        scale(size.width / 24, size.height / 24, pivot = Offset.Zero) {
            val stroke = Stroke(1.5f)
            when (tab) {
                ChassisControlTab.Vehicle -> {
                    val path = Path().apply {
                        moveTo(3f, 10f); lineTo(5f, 9f); lineTo(7f, 4f)
                        lineTo(17f, 4f); lineTo(19f, 9f); lineTo(21f, 10f)
                        lineTo(21f, 18f); lineTo(3f, 18f); close()
                    }
                    drawPath(path, color, style = stroke)
                    drawLine(color, Offset(5f, 9f), Offset(19f, 9f), 1.5f)
                    drawLine(color, Offset(6f, 13f), Offset(8f, 13f), 1.5f)
                    drawLine(color, Offset(16f, 13f), Offset(18f, 13f), 1.5f)
                }
                ChassisControlTab.Steering -> {
                    drawCircle(color, 9f, Offset(12f, 12f), style = stroke)
                    drawCircle(color, 2.5f, Offset(12f, 12f), style = stroke)
                    drawLine(color, Offset(3.5f, 9f), Offset(10f, 11f), 1.5f)
                    drawLine(color, Offset(14f, 11f), Offset(20.5f, 9f), 1.5f)
                    drawLine(color, Offset(12f, 14.5f), Offset(12f, 21f), 1.5f)
                }
                ChassisControlTab.BrakePedal -> {
                    drawRect(color, Offset(3f, 11f), Size(8f, 10f), style = stroke)
                    drawRect(color, Offset(15f, 14f), Size(6f, 7f), style = stroke)
                    drawLine(color, Offset(7f, 11f), Offset(12f, 4f), 1.5f)
                    drawLine(color, Offset(18f, 14f), Offset(18f, 5f), 1.5f)
                }
                ChassisControlTab.Epb -> {
                    drawCircle(color, 7f, Offset(12f, 12f), style = stroke)
                    drawArc(color, 130f, 100f, false, Offset(1f, 1f), Size(22f, 22f), style = stroke)
                    drawArc(color, -50f, 100f, false, Offset(1f, 1f), Size(22f, 22f), style = stroke)
                    val path = Path().apply {
                        moveTo(10f, 16f); lineTo(10f, 8f); lineTo(14f, 8f)
                        lineTo(14f, 12f); lineTo(10f, 12f)
                    }
                    drawPath(path, color, style = stroke)
                }
            }
        }
    }
}

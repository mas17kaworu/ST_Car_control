package com.longkai.stcarcontrol.st_exp.compose.ui.dds

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.longkai.stcarcontrol.st_exp.compose.ui.theme.Typography
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.ExpressService
import com.longkai.stcarcontrol.st_exp.compose.ui.components.CorneredContainer
import com.longkai.stcarcontrol.st_exp.compose.ui.components.HeaderText

@Composable
fun ServiceListCard(
    modifier: Modifier = Modifier,
    services: List<ExpressService>,
    onClickService: (ExpressService) -> Unit,
    onDoubleClickService: (ExpressService) -> Unit,
    onClickCreateService: () -> Unit,
    selectedServiceId: Long? = null,
) {
    CorneredContainer(
        modifier = modifier,
        cornerSize = 24.dp
    ) {
        Column(Modifier.padding(24.dp)) {
            HeaderText(
                modifier = Modifier.fillMaxWidth(),
                text = "Service Express"
            )
            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(8)
            ) {

                items(services.size) { index ->
                    ServiceItemCard(
                        service = services[index],
                        onClick = onClickService,
                        onDoubleClick = onDoubleClickService,
                        selected = (services[index].id == selectedServiceId)
                    )
                }
                item {
                    ServiceCreateItemCard(onClick = onClickCreateService)
                }
            }
        }
    }
}

@Composable
fun ServiceItemCard(
    service: ExpressService,
    onClick: (ExpressService) -> Unit,
    onDoubleClick: (ExpressService) -> Unit,
    selected: Boolean = false
) {
    CorneredContainer(
        modifier = Modifier.aspectRatio(1f),
        outerPadding = 12.dp,
        backgroundColor = MaterialTheme.colors.primaryVariant
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        Box(
            modifier = Modifier
                .background(if (selected) MaterialTheme.colors.primary else Color.Transparent)
                .indication(interactionSource, LocalIndication.current)
                .pointerInput(service) {
                    detectTapGestures(
                        onPress = { offset ->
                            val press = PressInteraction.Press(offset)
                            interactionSource.emit(press)
                            tryAwaitRelease()
                            interactionSource.emit(PressInteraction.Release(press))
                        },
                        onTap = { onClick(service) },
                        onDoubleTap = { onDoubleClick(service) }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = service.name,
                textAlign = TextAlign.Center,
                style = Typography.body1,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

@Composable
fun ServiceCreateItemCard(
    onClick: () -> Unit
) {
    CorneredContainer(
        modifier = Modifier.aspectRatio(1f),
        outerPadding = 12.dp,
        backgroundColor = MaterialTheme.colors.primaryVariant
    ) {
        Box(
            modifier = Modifier.clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier
                    .padding(12.dp)
                    .padding(horizontal = 16.dp),
                text = "+",
                textAlign = TextAlign.Center,
                style = Typography.h3
            )
        }
    }
}
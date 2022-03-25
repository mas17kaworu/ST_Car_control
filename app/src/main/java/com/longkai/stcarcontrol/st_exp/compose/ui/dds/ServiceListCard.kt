package com.longkai.stcarcontrol.st_exp.compose.ui.dds

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.longkai.stcarcontrol.st_exp.compose.ui.theme.Typography
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.ExpressService
import com.longkai.stcarcontrol.st_exp.compose.ui.components.CorneredContainer
import com.longkai.stcarcontrol.st_exp.compose.ui.components.HeaderText

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ServiceListCard(
    modifier: Modifier = Modifier,
    services: List<ExpressService>
) {
    CorneredContainer(
        modifier = modifier,
        cornerSize = 24.dp,
        innerPadding = 24.dp
    ) {
        Column {
            HeaderText(
                modifier = Modifier.fillMaxWidth(),
                text = "Service Express"
            )
            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                cells = GridCells.Fixed(4)
            ) {
                items(services) { service ->
                    ServiceCard(service = service)
                }
                item {
                    ServiceCardCreate()
                }
            }
        }
    }
}

@Composable
fun ServiceCard(
    service: ExpressService
) {
    Box(contentAlignment = Alignment.Center) {
        CorneredContainer(
            modifier = Modifier.aspectRatio(1f),
            outerPadding = 12.dp,
            innerPadding = 12.dp,
            backgroundColor = MaterialTheme.colors.primaryVariant
        ) {
            Text(
                text = service.name,
                textAlign = TextAlign.Center,
                style = Typography.body1
            )
        }
    }
}

@Composable
fun ServiceCardCreate() {
    Box(contentAlignment = Alignment.Center) {
        CorneredContainer(
            modifier = Modifier.aspectRatio(1f),
            outerPadding = 12.dp,
            innerPadding = 12.dp,
            backgroundColor = MaterialTheme.colors.primaryVariant
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = "+",
                textAlign = TextAlign.Center,
                style = Typography.h3
            )
        }
    }
}
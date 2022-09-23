package com.longkai.stcarcontrol.st_exp.compose.ui.dds

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.ServiceAction
import com.longkai.stcarcontrol.st_exp.compose.ui.components.CorneredContainer
import com.longkai.stcarcontrol.st_exp.compose.ui.components.HeaderText
import com.longkai.stcarcontrol.st_exp.compose.ui.components.ListItemText

@Composable
fun AvasServiceActionCard(
    actions: List<ServiceAction.AvasAction>
) {
    ServiceActionsCard(
        header = "AVAS service actions",
        actions = actions.map { it.name }
    )
}

@Composable
fun OledServiceActionCard(
    actions: List<ServiceAction.OledAction>
) {
    ServiceActionsCard(
        header = "OLED service actions",
        actions = actions.map { it.name }
    )
}

@Composable
fun ServiceActionsCard(
    header: String,
    actions: List<String>
) {
    CorneredContainer {
        Column(Modifier.padding(24.dp)) {
            HeaderText(text = header)

            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color.White,
                thickness = 2.dp
            )

            LazyColumn {
                items(actions.size) { index ->
                    ListItemText(text = "\u2022   ${actions[index]}")
                }
            }
        }
    }
}
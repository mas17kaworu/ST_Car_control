package com.longkai.stcarcontrol.st_exp.compose.ui.dds

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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

            Spacer(modifier = Modifier.height(16.dp))

            actions.map {
                ListItemText(text = "\u2022   $it")
            }
        }
    }
}
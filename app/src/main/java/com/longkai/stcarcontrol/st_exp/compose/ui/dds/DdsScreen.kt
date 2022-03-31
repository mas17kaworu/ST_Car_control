package com.longkai.stcarcontrol.st_exp.compose.ui.dds

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.ExpressService
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.TriggerCondition
import com.longkai.stcarcontrol.st_exp.compose.ui.components.ActivityViewContainer

@Composable
fun DdsScreen(
    ddsViewModel: DdsViewModel
) {
    val uiState by ddsViewModel.uiState.collectAsState()

    if (uiState.loading) return

    var focusedService: ExpressService? by remember { mutableStateOf(null) }

    ActivityViewContainer {
        Row(
            modifier = Modifier
                .padding(24.dp)
//                .navigationBarsWithImePadding()
        ) {
            Column(modifier = Modifier.weight(0.5f)) {
                ServiceListCard(
                    modifier = Modifier.heightIn(min = 200.dp, max = 400.dp),
                    services = uiState.expressServices,
                    onClickService = { focusedService = it },
                    onDoubleClickService = {
                        if (it.triggerCondition == TriggerCondition.DoubleClick) {
                            ddsViewModel.executeExpressService(it)
                        }
                    },
                    onClickCreateService = { focusedService = null }
                )

                Row {
                    Box(modifier = Modifier.weight(0.5f)) {
                        AvasServiceActionCard(uiState.avasActions)
                    }
                    Box(modifier = Modifier.weight(0.5f)) {
                        OledServiceActionCard(uiState.oledActions)
                    }
                }
            }

            Spacer(modifier = Modifier.padding(horizontal = 24.dp))

            Column(Modifier.weight(0.5f)) {
                if (focusedService == null) {
                    CreateServiceCard(
                        triggerOptions = TriggerCondition.values().toList(),
                        actionOptions = uiState.actionOptions,
                        onCreateService = { ddsViewModel.createExpressService(it) }
                    )
                } else {
                    EditServiceCard(
                        triggerOptions = TriggerCondition.values().toList(),
                        actionOptions = uiState.actionOptions,
                        serviceInReview = focusedService!!,
                        onUpdateService = {
                            ddsViewModel.updateExpressService(it)
                            focusedService = it
                        },
                        onDeleteService = {
                            ddsViewModel.deleteExpressService(it)
                            focusedService = null
                        }
                    )
                }
            }
        }
    }
}

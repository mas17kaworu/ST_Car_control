package com.longkai.stcarcontrol.st_exp.compose.ui.dds

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.ExpressService
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.TriggerCondition

@Composable
fun ServiceDetailsScreen(
    ddsViewModel: DdsViewModel,
    serviceId: Long?,
    onBack: () -> Unit
) {
    val uiState by ddsViewModel.uiState.collectAsState()

    if (uiState.loading) return

    val currentService = serviceId?.let { id -> uiState.expressServices.firstOrNull { it.id == id } }

    Row {
        Column(modifier = Modifier.weight(0.5f)) {
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
            if (currentService == null) {
                CreateServiceCard(
                    triggerOptions = TriggerCondition.values().toList(),
                    actionOptions = uiState.actionOptions,
                    onCreateService = { ddsViewModel.createExpressService(it) }
                )
            } else {
                EditServiceCard(
                    triggerOptions = TriggerCondition.values().toList(),
                    actionOptions = uiState.actionOptions,
                    serviceInReview = currentService,
                    onUpdateService = {
                        ddsViewModel.updateExpressService(it)
                    },
                    onDeleteService = {
                        ddsViewModel.deleteExpressService(it)
                        onBack()
                    }
                )
            }
        }
    }
}
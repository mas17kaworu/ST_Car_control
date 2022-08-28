package com.longkai.stcarcontrol.st_exp.compose.ui.dds

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.longkai.stcarcontrol.st_exp.R
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.TriggerCondition

@Composable
fun ServiceDetailsScreen(
    ddsViewModel: DdsViewModel,
    serviceId: Long?,
    onBack: () -> Unit,
    showSnackbar: (String) -> Unit
) {
    val uiState by ddsViewModel.uiState.collectAsState()

    if (uiState.loading) return

    val currentService =
        serviceId?.let { id -> uiState.expressServices.firstOrNull { it.id == id } }

    var showDeleteDialog by remember { mutableStateOf(false) }

    Row {
        Image(
            painter = painterResource(id = R.drawable.ic_baseline_arrow_back_24),
            contentDescription = "back button",
            modifier = Modifier
                .padding(12.dp)
                .clickable { onBack() }
        )

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

        Spacer(modifier = Modifier.padding(horizontal = 12.dp))

        Column(Modifier.weight(0.5f)) {
            if (currentService == null) {
                CreateServiceCard(
                    triggerOptions = TriggerCondition.values().toList(),
                    actionOptions = uiState.actionOptions,
                    onCreateService = {
                        ddsViewModel.createExpressService(it)
                        onBack()
                        showSnackbar("Service ${it.name} created")
                    }
                )
            } else {
                EditServiceCard(
                    triggerOptions = TriggerCondition.values().toList(),
                    actionOptions = uiState.actionOptions,
                    serviceInReview = currentService,
                    onUpdateService = {
                        ddsViewModel.updateExpressService(it)
                        showSnackbar("Service ${it.name} updated")
                    },
                    onDeleteService = {
                        showDeleteDialog = true
                    }
                )
            }
        }
    }

    if (showDeleteDialog) {
        DeleteServiceConfirmationDialog(
            showDialog = showDeleteDialog,
            onOK = {
                currentService?.let {
                    ddsViewModel.deleteExpressService(it)
                    onBack()
                    showSnackbar("Service ${it.name} deleted")
                }
            },
            onCancel = {
                showDeleteDialog = false
            }
        )
    }
}
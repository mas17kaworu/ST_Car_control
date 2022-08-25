package com.longkai.stcarcontrol.st_exp.compose.ui.dds

import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.TriggerCondition

@Composable
fun ExpressServicesScreen(
    ddsViewModel: DdsViewModel,
    onCreateService: () -> Unit,
    onViewServiceDetails: (serviceId: Long) -> Unit
) {
    val uiState by ddsViewModel.uiState.collectAsState()

    if (uiState.loading) return

    ServiceListCard(
        modifier = Modifier.heightIn(min = 200.dp, max = 400.dp),
        services = uiState.expressServices,
        onClickService = {
            onViewServiceDetails(it.id)
        },
        onDoubleClickService = {
            if (it.triggerCondition == TriggerCondition.DoubleClick) {
                ddsViewModel.executeExpressService(it)
            }
        },
        onClickCreateService = {
            onCreateService()
        }
    )
}
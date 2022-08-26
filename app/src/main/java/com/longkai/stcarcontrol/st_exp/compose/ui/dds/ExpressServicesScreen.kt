package com.longkai.stcarcontrol.st_exp.compose.ui.dds

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.longkai.stcarcontrol.st_exp.R
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.TriggerCondition

@Composable
fun ExpressServicesScreen(
    ddsViewModel: DdsViewModel,
    onCreateService: () -> Unit,
    onViewServiceDetails: (serviceId: Long) -> Unit,
    showSnackbar: (String) -> Unit
) {
    val uiState by ddsViewModel.uiState.collectAsState()

    if (uiState.loading) return

    var focusedServiceId by rememberSaveable { mutableStateOf(Route.INVALID_SERVICE_ID) }

    Column(
        Modifier.padding(horizontal = 0.dp)
    ) {
        ServiceListCard(
            modifier = Modifier.heightIn(min = 200.dp, max = 400.dp),
            services = uiState.expressServices,
            onClickService = {
//                onViewServiceDetails(it.id)
                focusedServiceId = it.id
            },
            onDoubleClickService = {
                if (it.triggerCondition == TriggerCondition.DoubleClick) {
                    ddsViewModel.executeExpressService(it)
                }
            },
            onClickCreateService = {
                focusedServiceId = Route.INVALID_SERVICE_ID
                onCreateService()
            },
            selectedServiceId = focusedServiceId
        )

        val focusedService = uiState.expressServices.firstOrNull { it.id == focusedServiceId }
        val imageUri = focusedService?.imageUri?.let {
            try {
                Uri.parse(it)
            } catch (e: Exception) {
                null
            }
        }
        imageUri?.let { uri ->
            println("zcf imageUri: $uri")
            Row(
                modifier = Modifier.padding(24.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).apply {
                        fallback(R.drawable.ic_pick_image)
                        error(R.drawable.ic_image_placeholder)
                        data(uri)
                    }.build(),
                    contentDescription = "Service image",
                    modifier = Modifier.fillMaxHeight()
                )
                
                Spacer(modifier = Modifier.width(20.dp))

                Column(
                    modifier = Modifier.align(Alignment.Top)
                ) {
                    Button(
                        onClick = { focusedService.let { onViewServiceDetails(it.id) } },
                    ) {
                        Text(text = "View service details")
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            focusedService.let {
                                ddsViewModel.deleteExpressService(it)
                                showSnackbar("Service ${it.name} deleted")
                            }
                        },
                    ) {
                        Text(text = "Delete service")
                    }
                }
            }
        }
    }
}
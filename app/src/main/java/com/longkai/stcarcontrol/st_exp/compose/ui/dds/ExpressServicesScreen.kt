package com.longkai.stcarcontrol.st_exp.compose.ui.dds

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.longkai.stcarcontrol.st_exp.R
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.TriggerCondition
import com.longkai.stcarcontrol.st_exp.compose.ui.components.CorneredContainer

@Composable
fun ExpressServicesScreen(
    ddsViewModel: DdsViewModel,
    onCreateService: () -> Unit,
    onViewServiceDetails: (serviceId: Long) -> Unit,
    showSnackbar: (String) -> Unit
) {
    val uiState by ddsViewModel.uiState.collectAsState()

    if (uiState.loading) return

    val focusedService = uiState.expressServices.firstOrNull { it.name == uiState.focusedService?.name }

    Column(
        Modifier.padding(horizontal = 0.dp)
    ) {
        ServiceListCard(
            modifier = Modifier.heightIn(min = 200.dp, max = 400.dp),
            services = uiState.expressServices,
            onClickService = {
                ddsViewModel.onSelectService(it)
            },
            onDoubleClickService = {
                if (it.triggerCondition == TriggerCondition.DoubleClick) {
                    ddsViewModel.executeExpressService(it)
                }
            },
            onClickCreateService = {
                ddsViewModel.onSelectService(null)
                onCreateService()
            },
            selectedServiceId = focusedService?.id
        )

        val imageUri = focusedService?.imageUri?.let {
            try {
                Uri.parse(it)
            } catch (e: Exception) {
                null
            }
        }
        imageUri?.let { uri ->
            println("zcf imageUri: $uri")
            CorneredContainer(
                modifier = Modifier.fillMaxSize(),
                cornerSize = 24.dp
            ) {
                Row(
                    modifier = Modifier.padding(24.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current).apply {
                            fallback(R.drawable.ic_pick_image)
                            error(R.drawable.ic_image_error)
                            data(uri)
                        }.build(),
                        contentDescription = "Service image",
                        modifier = Modifier.fillMaxHeight().aspectRatio(1f)
                    )

                    Spacer(modifier = Modifier.width(20.dp))

                    Column(
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = { focusedService.let { onViewServiceDetails(it.id) } },
                        ) {
                            Text(text = "View service details")
                        }

                        Spacer(modifier = Modifier.height(24.dp))

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
}
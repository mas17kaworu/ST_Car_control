package com.longkai.stcarcontrol.st_exp.compose.ui.dds

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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

    val focusedService =
        uiState.expressServices.firstOrNull { it.name == uiState.focusedService?.name }

    var showDeleteDialog by remember { mutableStateOf(false) }

    Row {
        ServiceListCard(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .fillMaxHeight(),
            services = uiState.expressServices,
            onClickService = {
                ddsViewModel.onSelectService(it)
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
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current).apply {
                            fallback(R.drawable.ic_pick_image)
                            error(R.drawable.ic_image_error)
                            data(uri)
                        }.build(),
                        contentDescription = "Service image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_delete),
                            contentDescription = "Delete service",
                            modifier = Modifier
                                .clickable {
                                    showDeleteDialog = true
                                }
                                .padding(4.dp)
                        )

                        Row {
                            if (focusedService.triggerCondition == TriggerCondition.ManuallySend) {
                                Button(
                                    onClick = { ddsViewModel.executeExpressService(focusedService) }
                                ) {
                                    Text(text = "Send")
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Button(
                                onClick = { onViewServiceDetails(focusedService.id) }
                            ) {
                                Text(text = "View service details")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        DeleteServiceConfirmationDialog(
            showDialog = showDeleteDialog,
            onOK = {
                focusedService?.let {
                    ddsViewModel.deleteExpressService(it)
                    showSnackbar("Service ${it.name} deleted")
                }
                showDeleteDialog = false
            },
            onCancel = {
                showDeleteDialog = false
            }
        )
    }
}

@Composable
fun DeleteServiceConfirmationDialog(
    showDialog: Boolean,
    onOK: () -> Unit,
    onCancel: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onCancel,
            confirmButton = {
                Button(onClick = onOK) {
                    Text(text = "OK")
                }
            },
            dismissButton = {
                Button(onClick = onCancel) {
                    Text(text = "Cancel")
                }
            },
            text = {
                Text(
                    text = "Do you want to delete the service?"
                )
            },
            backgroundColor = Color.DarkGray,
            contentColor = Color.White
        )
    }
}
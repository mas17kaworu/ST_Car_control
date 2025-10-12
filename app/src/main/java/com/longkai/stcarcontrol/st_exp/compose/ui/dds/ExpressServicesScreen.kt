package com.longkai.stcarcontrol.st_exp.compose.ui.dds

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.longkai.stcarcontrol.st_exp.R
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.TriggerCondition
import com.longkai.stcarcontrol.st_exp.compose.ui.components.CorneredContainer
import com.longkai.stcarcontrol.st_exp.mockMessage.MockMessageServiceImpl
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

@Composable
fun ExpressServicesScreen(
    ddsViewModel: DdsViewModel,
    onCreateService: () -> Unit,
    onViewServiceDetails: (serviceId: Long) -> Unit,
    showSnackbar: (String) -> Unit,
) {
    val uiState by ddsViewModel.uiState.collectAsState()

    if (uiState.loading) return

    val focusedService =
        uiState.expressServices.firstOrNull { it.name == uiState.focusedService?.name }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDDSSettingsDialog by remember { mutableStateOf(false) }
    var showLinkStatePanel by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)

    var currentMaxValue by remember {
        mutableStateOf(sharedPreferences.getInt("currentMaxValue", 25))
    }
    var voltageMaxValue by remember {
        mutableStateOf(sharedPreferences.getInt("voltageMaxValue", 25))
    }

    LaunchedEffect(Unit) {
        ddsViewModel.registerZCUCommandListener()
        ddsViewModel.registerFangjiaListener()
        MockMessageServiceImpl.getService().StartService(DdsViewModel::class.java.toString())
    }

    DisposableEffect(Unit) {
        onDispose {
            ddsViewModel.unregisterZCUCommandListener()
        }
    }
    if (showDDSSettingsDialog) {
        DDSSettingsDialog(
            onDismissRequest = {
                showDDSSettingsDialog = false
            },
            selectedLanguage = uiState.aiLanguage,
            waveMaxValue = currentMaxValue,
            temperatureMaxValue = voltageMaxValue,
            serviceCount = uiState.expressServices.size,
            aiSDKInitState = uiState.isAISDKInitSuccess,
            cnKeywords = uiState.cnKeyWords,
            enKeywords = uiState.enKeyWords,
            onCurrentMaxValueChange = {
                currentMaxValue = it
                val editor = sharedPreferences.edit()
                editor.putInt("currentMaxValue", currentMaxValue)
                editor.apply()
            },
            onVoltageMaxValueChange = {
                voltageMaxValue = it
                val editor = sharedPreferences.edit()
                editor.putInt("voltageMaxValue", voltageMaxValue)
                editor.apply()
            },
            onAIChooseLanguage = {
                ddsViewModel.selectAiLanguage(it)
            },
            onKeyWordsChange = { index, value -> ddsViewModel.updateRecordKeyWords(index, value) },
            updateKeywords = { cnList, enList -> ddsViewModel.updateRecordKeyWords(cnList, enList) }
        )
    }

    var showWavePannel by remember {
        mutableStateOf(false)
    }

    var showFangjiaPannel by remember {
        mutableStateOf(false)
    }

    Row {
        Column {
            ServiceListCard(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .weight(1.0f),
                services = uiState.expressServices,
                onClickService = {
                    showWavePannel = false
                    showLinkStatePanel = false
                    ddsViewModel.onSelectService(it)
                },
                onClickCreateService = {
                    ddsViewModel.onSelectService(null)
                    onCreateService()
                },
                selectedServiceId = focusedService?.id
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Hide Music player
//                MusicPlayer(
//                    modifier = Modifier.padding(vertical = 8.dp)
//                )
                IconButton(
                    modifier = Modifier.background(color = MaterialTheme.colors.background),
                    onClick = { showDDSSettingsDialog = true }) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "setting",
                        tint = Color.White,
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                IconButton(
                    modifier = Modifier.background(
                        color = MaterialTheme.colors.background
                    ),
                    onClick = {
                        showWavePannel = !showWavePannel
                        if (showWavePannel) {
                            showLinkStatePanel = false
                        }
                    }) {
                    Icon(
                        modifier = Modifier.size(36.dp),
                        painter = painterResource(id = R.drawable.wave),
                        contentDescription = "wave",
                        tint = Color.White,
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))

                IconButton(
                    modifier = Modifier.background(color = MaterialTheme.colors.background),
                    onClick = {
                        showLinkStatePanel = !showLinkStatePanel
                        if (showLinkStatePanel) {
                            showWavePannel = false
                        }
                    }) {
                    Icon(
                        painter = painterResource(id = R.drawable.network),
                        contentDescription = "LinkState",
                        tint = Color.White,
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                IconButton(
                    modifier = Modifier.background(
                        color = if (uiState.listening)
                            MaterialTheme.colors.primary
                        else
                            MaterialTheme.colors.background
                    ),
                    onClick = {
                        ddsViewModel.startListen()
                    }) {
                    Icon(
                        painter = painterResource(id = R.drawable.microphone),
                        contentDescription = "mic",
                        tint = Color.White,
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                IconButton(
                    modifier = Modifier,
                    onClick = {
                        showFangjiaPannel = !showFangjiaPannel
                    },
                ) {
                    Icon(
//                        painter = painterResource(id = R.mipmap.fangjia_icon),
                        painter = painterResource(id = R.drawable.microphone),
                        contentDescription = "mic",
                        tint = Color.White,
                    )
                }

                if (uiState.listening) {
                    AIDialog(
                        aiListenResult = uiState.aiListenResult,
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Box(modifier = Modifier) {
                    Text(
                        text = uiState.aiListenResult,
                        color = Color.White
                    )
                }
            }
        }

        Box {
            val imageUri = focusedService?.imageUri?.let {
                try {
                    Uri.parse(it)
                } catch (e: Exception) {
                    null
                }
            }
            if (!showWavePannel && !showLinkStatePanel) {
                imageUri?.let { uri ->
                    println("zcf imageUri: $uri")
                    var feedbackMessage by remember { mutableStateOf("") }
                    val feedbackMessageScope = rememberCoroutineScope()
                    Column {
                        CorneredContainer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
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
                                                onClick = {
                                                    ddsViewModel.executeExpressService(
                                                        focusedService
                                                    )
                                                    feedbackMessage =
                                                        "Service ${focusedService.name} sent!"
                                                    feedbackMessageScope.coroutineContext.cancelChildren()
                                                    feedbackMessageScope.launch {
                                                        delay(2000)
                                                        feedbackMessage = ""
                                                    }
                                                }
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
                        Text(
                            text = feedbackMessage,
                            modifier = Modifier.fillMaxWidth(),
                            color = Color.White,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }

            if (showWavePannel) {
                // For test
                val fakeFlow = flow {
                    while (true) {
                        emit((0..200).random() / 10f)
                        delay(500) // 每0.5秒生成一次
                    }
                }

                val fakeFlow2 = flow {
                    while (true) {
                        emit((0..20).random()) // 随机生成一个整数
                        delay(500) // 每0.5秒生成一次
                    }
                }

                WaveCardView(
                    modifier = Modifier
                        .fillMaxSize(),
                    loadStatus = uiState.loadStatus,
                    currentMaxValue = currentMaxValue,
                    voltageMaxValue = voltageMaxValue,
                    currentFlow = uiState.currentFlow, //fakeFlow, //uiState.currentFlow,
                    voltageFlow = uiState.voltageFlow, //fakeFlow2,
                    tempDeviceValue = uiState.tempDevice,
                    tempMosValue = uiState.tempMos,
                )
            }

            if (showLinkStatePanel) {
                LinkStateView(
                    modifier = Modifier
                        .fillMaxSize(),
                    uiState = uiState,
                )
            }

            if (showFangjiaPannel) {
                FangjiaCard(
                    modifier = Modifier
                        .fillMaxSize(),
                    fangjiaState = uiState.fangjiaState,
                )
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
package com.longkai.stcarcontrol.st_exp.compose.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.longkai.stcarcontrol.st_exp.R
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.ServiceAction
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.TriggerCondition
import com.longkai.stcarcontrol.st_exp.compose.ui.components.HeaderText
import com.longkai.stcarcontrol.st_exp.compose.ui.components.ListItemText
import com.longkai.stcarcontrol.st_exp.compose.ui.dds.AvasServiceActionCard
import com.longkai.stcarcontrol.st_exp.compose.ui.dds.DdsViewModel
import com.longkai.stcarcontrol.st_exp.compose.ui.dds.OledServiceActionCard
import com.longkai.stcarcontrol.st_exp.compose.ui.dds.ServiceList

@Composable
fun DdsScreen(
    ddsViewModel: DdsViewModel
) {
    val uiState by ddsViewModel.uiState.collectAsState()

    if (uiState.loading) return

    ActivityViewContainer {
        Row(
            modifier = Modifier
                .padding(24.dp)
//                .navigationBarsWithImePadding()
        ) {
            Column(modifier = Modifier.weight(0.5f)) {
                ServiceList(
                    modifier = Modifier.heightIn(min = 200.dp, max = 400.dp),
                    services = uiState.expressServices
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
                CreateService(
                    triggerOptions = TriggerCondition.values().toList(),
                    actionOptions = uiState.actionOptions
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CreateService(
    triggerOptions: List<TriggerCondition>,
    actionOptions: List<ServiceAction>,
    modifier: Modifier = Modifier
) {
    var serviceName by remember { mutableStateOf("") }
    var triggerCondition by remember { mutableStateOf(triggerOptions[0]) }
    var actions by remember { mutableStateOf(listOf(actionOptions[0]))}

    CorneredContainer(
        modifier = modifier.fillMaxWidth(),
        cornerSize = 24.dp,
        innerPadding = 24.dp
    ) {

        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 36.dp)
        ) {
            HeaderText(text = "Create a new Service")

            Spacer(modifier = Modifier.height(16.dp))

            Column(Modifier.fillMaxWidth(0.8f)) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = serviceName,
                    onValueChange = { serviceName = it },
                    label = {
                        Text(text = "Service name: ")
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                DropDownList(
                    label = "Trigger condition",
                    options = triggerOptions,
                    selectedOption = triggerCondition,
                    onValueChange = {
                        triggerCondition = it
                    }
                )
            }

            
            Spacer(modifier = Modifier.height(16.dp))

            ListItemText(text = "Actions")

            Spacer(modifier = Modifier.height(16.dp))

            Column {
                actions.mapIndexed { index, action ->
                    ActionItem(
                        sequence = index,
                        actionOptions = actionOptions,
                        actionSelection = action,
                        onSelectionChanged = { newAction ->
                            actions = actions.toMutableList().apply { this[index] = newAction }
                        },
                        onActionRemoved = { indexToRemove ->
                            actions = actions.toMutableList().apply { removeAt(indexToRemove) }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row {
                Text(
                    text = "Add another action ...",
                    modifier = Modifier.clickable {
                        actions = actions + actionOptions[0]
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = { /*TODO*/ }) {
                Text(text = "Create")
            }
        }
    }
}

@Composable
fun ActionItem(
    sequence: Int,
    actionOptions: List<ServiceAction>,
    actionSelection: ServiceAction,
    onSelectionChanged: (ServiceAction) -> Unit,
    onActionRemoved: (Int) -> Unit
) {

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${sequence + 1}",
            modifier = Modifier.width(30.dp)
        )

//        var actionSelection by remember { mutableStateOf(initialSelection) }

        Column(
            modifier = Modifier.weight(weight = 1f),
            horizontalAlignment = Alignment.End
        ) {
            DropDownList(
                label = "Action",
                options = actionOptions,
                selectedOption = actionSelection,
                onValueChange = {
//                    actionSelection = it
                    onSelectionChanged(it)
                }
            )
            if (actionSelection is ServiceAction.Delay) {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = (actionSelection as ServiceAction.Delay).seconds.toString(),
                    onValueChange = {
                        if (it.length <= 3) {
                            val delay = Integer.parseInt(it)
                            val newSelection = ServiceAction.Delay(seconds = delay)
                            onSelectionChanged(newSelection)
                        }
                    },
                    trailingIcon = {
                        Text(
                            text = "Seconds",
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    )
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Image(
            painter = painterResource(id = R.drawable.ic_baseline_remove_circle_outline_24),
            contentDescription = "Add or remove action",
            modifier = Modifier.clickable {
                onActionRemoved(sequence)
            }
        )
    }
}

package com.longkai.stcarcontrol.st_exp.compose.ui.dds

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.longkai.stcarcontrol.st_exp.R
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.ExpressService
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.ServiceAction
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.TriggerCondition
import com.longkai.stcarcontrol.st_exp.compose.ui.components.CorneredContainer
import com.longkai.stcarcontrol.st_exp.compose.ui.components.DropDownList
import com.longkai.stcarcontrol.st_exp.compose.ui.components.HeaderText
import com.longkai.stcarcontrol.st_exp.compose.ui.components.ListItemText

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CreateServiceCard(
    modifier: Modifier = Modifier,
    triggerOptions: List<TriggerCondition>,
    actionOptions: List<ServiceAction>,
    onCreateService: (ExpressService) -> Unit
) {
    var serviceName by remember { mutableStateOf("") }
    var triggerCondition by remember { mutableStateOf(triggerOptions[0]) }
    var actions by remember { mutableStateOf(listOf(actionOptions[0])) }

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

            Column(
                modifier = Modifier.width(IntrinsicSize.Min)
            ) {
                Text(
                    text = "Add another action ...",
                    modifier = Modifier
                        .width(IntrinsicSize.Max)
                        .clickable {
                            actions = actions + actionOptions[0]
                        }
                        .fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    onCreateService(
                        ExpressService(
                            name = serviceName,
                            triggerCondition = triggerCondition,
                            actions = actions
                        )
                    )
                },
                enabled = serviceName.isNotEmpty()
            ) {
                Text(
                    text = "Create",
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
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

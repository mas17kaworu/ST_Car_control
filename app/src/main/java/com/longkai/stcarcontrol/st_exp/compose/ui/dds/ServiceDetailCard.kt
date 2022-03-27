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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.longkai.stcarcontrol.st_exp.R
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.ExpressService
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.ExpressServiceParam
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.ServiceAction
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.TriggerCondition
import com.longkai.stcarcontrol.st_exp.compose.ui.components.CorneredContainer
import com.longkai.stcarcontrol.st_exp.compose.ui.components.DropDownList
import com.longkai.stcarcontrol.st_exp.compose.ui.components.HeaderText
import com.longkai.stcarcontrol.st_exp.compose.ui.components.ListItemText


@Composable
fun CreateServiceCard(
    triggerOptions: List<TriggerCondition>,
    actionOptions: List<ServiceAction>,
    modifier: Modifier = Modifier,
    onCreateService: ((ExpressServiceParam) -> Unit)
) {
    ServiceDetailCard(
        triggerOptions = triggerOptions,
        actionOptions = actionOptions,
        modifier = modifier,
        serviceInReview = null,
        onCreateService = onCreateService,
        onUpdateService = null,
        onDeleteService = null
    )
}

@Composable
fun EditServiceCard(
    triggerOptions: List<TriggerCondition>,
    actionOptions: List<ServiceAction>,
    modifier: Modifier = Modifier,
    serviceInReview: ExpressService,
    onUpdateService: ((ExpressService) -> Unit),
    onDeleteService: ((ExpressService) -> Unit)
) {
    ServiceDetailCard(
        triggerOptions = triggerOptions,
        actionOptions = actionOptions,
        modifier = modifier,
        serviceInReview = serviceInReview,
        onCreateService = null,
        onUpdateService = onUpdateService,
        onDeleteService = onDeleteService
    )
}

/**
 * @param serviceInReview
 *  - null: Create mode
 *  - else: Edit mode
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ServiceDetailCard(
    triggerOptions: List<TriggerCondition>,
    actionOptions: List<ServiceAction>,
    modifier: Modifier,
    serviceInReview: ExpressService?,
    onCreateService: ((ExpressServiceParam) -> Unit)?,
    onUpdateService: ((ExpressService) -> Unit)?,
    onDeleteService: ((ExpressService) -> Unit)?
) {
    val cardTitle = if (serviceInReview == null) "Create a new Service" else "Service details"

    var serviceName by remember(serviceInReview) {
        mutableStateOf(serviceInReview?.name ?: "")
    }
    var triggerCondition by remember(serviceInReview) {
        mutableStateOf(serviceInReview?.triggerCondition ?: triggerOptions[0])
    }
    var actions by remember(serviceInReview) {
        mutableStateOf(serviceInReview?.actions ?: listOf(actionOptions[0]))
    }

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
            HeaderText(text = cardTitle)

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


            if (serviceInReview == null) {
                Button(
                    onClick = {
                        onCreateService?.invoke(
                            ExpressServiceParam(
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
            } else {
                Row {
                    Button(
                        onClick = {
                            onUpdateService?.invoke(
                                ExpressService(
                                    id = serviceInReview.id,
                                    name = serviceName,
                                    triggerCondition = triggerCondition,
                                    actions = actions
                                )
                            )
                        },
                        enabled = serviceName.isNotEmpty()
                    ) {
                        Text(
                            text = "Update",
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                    Button(
                        onClick = {
                            onDeleteService?.invoke(serviceInReview)
                        },
                        enabled = serviceName.isNotEmpty()
                    ) {
                        Text(
                            text = "Delete",
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
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

        Column(
            modifier = Modifier.weight(weight = 1f),
            horizontalAlignment = Alignment.End
        ) {
            DropDownList(
                label = "Action",
                options = actionOptions,
                selectedOption = actionSelection,
                onValueChange = {
                    onSelectionChanged(it)
                }
            )
            if (actionSelection is ServiceAction.Delay) {
//                var delayInput by remember(actionSelection) { mutableStateOf(actionSelection.seconds.toString()) }

                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = actionSelection.seconds.toString(),
                    onValueChange = { input ->
                        val delayInput = input.filter { it.isDigit() }.take(3)
                        if (delayInput.isNotBlank()) {
                            val delay = Integer.parseInt(delayInput)
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

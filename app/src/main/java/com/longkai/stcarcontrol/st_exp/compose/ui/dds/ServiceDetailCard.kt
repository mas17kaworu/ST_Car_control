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
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.ExpressServiceParam
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.ServiceAction
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.TriggerCondition
import com.longkai.stcarcontrol.st_exp.compose.ui.components.CorneredContainer
import com.longkai.stcarcontrol.st_exp.compose.ui.components.DropDownList
import com.longkai.stcarcontrol.st_exp.compose.ui.components.HeaderText
import com.longkai.stcarcontrol.st_exp.compose.ui.components.ListItemText
import java.lang.NumberFormatException


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

data class ActionItem(
    val serviceAction: ServiceAction,
    val delay: String?
) {
    override fun toString(): String {
        return serviceAction.toString()
    }
}

fun ServiceAction.toActionItem() = ActionItem(
    serviceAction = this,
    delay = if (this is ServiceAction.Delay) this.seconds.toString() else null
)

fun ActionItem.toServiceAction() = if (this.serviceAction is ServiceAction.Delay) {
    val parsedDelay = this.delay?.let {
        try {
            Integer.parseInt(this.delay)
        } catch (e : NumberFormatException) {
            0
        }
    } ?: 0
    ServiceAction.Delay(seconds = parsedDelay)
} else this.serviceAction

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

    val actionItemOptions = actionOptions.map { it.toActionItem() }
    var actionItems by remember(serviceInReview) {
        val actions = serviceInReview?.actions ?: listOf(actionOptions[0])
        mutableStateOf(actions.map { it.toActionItem() })
    }

    CorneredContainer(
        modifier = modifier.fillMaxWidth(),
        cornerSize = 24.dp,
    ) {

        Column(
            modifier = modifier
                .padding(24.dp)
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
                actionItems.mapIndexed { index, actionItem ->
                    ActionItem(
                        sequence = index,
                        actionItemOptions = actionItemOptions,
                        actionItemSelection = actionItem,
                        onSelectionChanged = { newActionItem ->
                            actionItems =
                                actionItems.toMutableList().apply { this[index] = newActionItem }
                        },
                        onActionRemoved = { indexToRemove ->
                            actionItems =
                                actionItems.toMutableList().apply { removeAt(indexToRemove) }
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
                            actionItems = actionItems + actionItemOptions[0]
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
                                actions = actionItems.map { it.toServiceAction() }
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
                                    actions = actionItems.map { it.toServiceAction() }
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

                    Spacer(modifier = Modifier.width(20.dp))
                    
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
    actionItemOptions: List<ActionItem>,
    actionItemSelection: ActionItem,
    onSelectionChanged: (ActionItem) -> Unit,
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
                options = actionItemOptions,
                selectedOption = actionItemSelection,
                onValueChange = {
                    onSelectionChanged(it)
                }
            )
            if (actionItemSelection.serviceAction is ServiceAction.Delay) {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = actionItemSelection.delay ?: "",
                    onValueChange = { input ->
                        val delayInput = input.filter { it.isDigit() }.take(3)
                        val newSelection = ActionItem(
                            serviceAction = ServiceAction.Delay(seconds = 0), // this seconds is no use, will be replaced when create or update.
                            delay = delayInput
                        )
                        onSelectionChanged(newSelection)
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

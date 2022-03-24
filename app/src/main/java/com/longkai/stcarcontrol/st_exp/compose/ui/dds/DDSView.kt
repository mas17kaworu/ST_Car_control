package com.longkai.stcarcontrol.st_exp.compose.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.composestart.ui.theme.Typography
import com.longkai.stcarcontrol.st_exp.R
import kotlin.random.Random


@Composable
fun DDSView() {
    ActivityViewContainer {
        Row(
            modifier = Modifier
                .padding(24.dp)
//                .navigationBarsWithImePadding()
        ) {
            Column(
                modifier = Modifier.weight(0.5f)
            ) {
                ServiceList()

                Row {
                    Box(modifier = Modifier.weight(0.5f)) {
                        AvasServiceActions()
                    }
                    Box(modifier = Modifier.weight(0.5f)) {
                        OledServiceActions()
                    }
                }
            }

            Spacer(modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxHeight()
                .background(Color.White.copy(alpha = 0.3f)))

            Column(Modifier.weight(0.5f)) {
                CreateService()
            }
        }
    }
}

@Composable
fun ServiceList(
    modifier: Modifier = Modifier
) {
    CorneredContainer(
        modifier = modifier,
        cornerSize = 24.dp,
        innerPadding = 24.dp
    ) {
        Column {
            HeaderText(
                modifier = Modifier.fillMaxWidth(),
                text = "Service Express"
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row {
                CorneredContainer(
                    modifier = Modifier
                        .width(150.dp)
                        .height(150.dp),
                    outerPadding = 16.dp,
                    backgroundColor = MaterialTheme.colors.primaryVariant
                ) {
                    Text(
                        text = "Welcome service",
//                    modifier = Modifier.padding(horizontal = 16.dp),
                        textAlign = TextAlign.Center,
                        style = Typography.body1
                    )
                }

                CorneredContainer(
                    modifier = Modifier
                        .width(150.dp)
                        .height(150.dp),
                    outerPadding = 16.dp,
                    backgroundColor = MaterialTheme.colors.primaryVariant
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = "+",
                        textAlign = TextAlign.Center,
                        style = Typography.h3
                    )
                }
            }
        }
    }
}

@Composable
fun AvasServiceActions() {
    CorneredContainer {
        Column {
            HeaderText(text = "AVAS Service actions")

            Spacer(modifier = Modifier.height(16.dp))

            val actions = listOf("Sound Effect 1", "Sound Effect 2", "Sound Effect 3")
            actions.map { 
                ListItemText(text = it)
            }
        }
    }
}

@Composable
fun OledServiceActions() {
    CorneredContainer {
        Column {
            HeaderText(text = "OLED Service actions")

            Spacer(modifier = Modifier.height(16.dp))

            val actions = listOf("Light Effect 1", "Light Effect 2", "Light Effect 3")
            actions.map {
                ListItemText(text = it)
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CreateService(
    modifier: Modifier = Modifier
) {

    CorneredContainer(
        modifier = modifier,
        cornerSize = 24.dp,
        innerPadding = 24.dp
    ) {

        Column(modifier = modifier) {
            HeaderText(text = "Create a new Service")

            Spacer(modifier = Modifier.height(16.dp))

            var name by remember { mutableStateOf("") }
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = {
                    Text(text = "Service name: ")
                },
//                placeholder = {
//                    Text(text = "Please input a service name")
//                }
            )
            
            
            Spacer(modifier = Modifier.height(16.dp))


            val triggerConditions = listOf("Double click service", "Digital key unlock door")
            DropDownList(
                label = "Trigger condition",
                options = triggerConditions,
                selectedOption = triggerConditions[0],
                onValueChange = {}
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            ListItemText(text = "Actions")
            Spacer(modifier = Modifier.height(16.dp))

            ActionItem(sequence = 1, initialSelection = actionOptions[Random.nextInt(actionOptions.size)])
            ActionItem(sequence = 2, initialSelection = actionOptions[Random.nextInt(actionOptions.size)])
            ActionItem(sequence = 3, initialSelection = actionOptions[Random.nextInt(actionOptions.size)])

            Spacer(modifier = Modifier.height(24.dp))
            Row {
                Text(text = "Add another action ...")
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
    initialSelection: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = sequence.toString(),
            modifier = Modifier.width(40.dp)
        )

        var actionSelection by remember { mutableStateOf(initialSelection) }
        var delay by remember {
            mutableStateOf("5")
        }
        Column(
            horizontalAlignment = Alignment.End
        ) {
            DropDownList(
                label = "Action",
                options = actionOptions,
                selectedOption = actionSelection,
                onValueChange = {
                    actionSelection = it
                }
            )
            if (actionSelection == delayAction) {
                TextField(
                    value = delay,
                    onValueChange = {
                        delay = it
                    },
                    trailingIcon = {
                        Text(text = "Seconds")
                    }
                )
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Image(
            painter = painterResource(id = R.drawable.ic_baseline_remove_circle_outline_24),
            contentDescription = "Add or remove action"
        )
    }
}

val avasActions = listOf("Sound Effect 1", "Sound Effect 2", "Sound Effect 3")
val oledActions = listOf("Light Effect 1", "Light Effect 2", "Light Effect 3")
const val delayAction = "Delay"
val actionOptions = avasActions + delayAction + oledActions



@Composable
fun HeaderText(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        modifier = modifier.fillMaxWidth(),
        text = text,
        color = MaterialTheme.colors.onBackground,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.h5
    )
}

@Composable
fun ListItemText(
    text: String
) {
    Text(
        modifier = Modifier.padding(vertical = 8.dp),
        text = text,
        color = MaterialTheme.colors.onBackground,
        style = MaterialTheme.typography.body1
    )
}

@Composable
fun VerticalDivider(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.12f),
    thickness: Dp = 2.dp
) {
    Box(
        modifier
            .fillMaxHeight()
            .width(thickness)
            .background(color = color)
    )
}


package com.longkai.stcarcontrol.st_exp.compose.ui

import androidx.compose.material.*
import androidx.compose.runtime.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DropDownList(
    label: String,
    options: List<String>,
    selectedOption: String,
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
//    var selectedOptionText by remember { mutableStateOf(initialSelection) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        TextField(
            readOnly = true,
            value = selectedOption,
            onValueChange = {},
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    onClick = {
//                        selectedOptionText = selectionOption
                        onValueChange(selectionOption)
                        expanded = false
                    }
                ) {
                    Text(text = selectionOption)
                }
            }
        }
    }
}
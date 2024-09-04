package com.longkai.stcarcontrol.st_exp.compose.ui.dds

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties


@Composable
fun DDSSettingsDialog(
    onDismissRequest: () -> Unit,
    waveMaxValue: Int,
    onWaveMacValueChange: (Int) -> Unit,
) {
    Dialog(
        properties = DialogProperties(
            dismissOnClickOutside = true,
        ),
        onDismissRequest = onDismissRequest,
    ) {
        DDSSettings(
            modifier = Modifier
                .size(width = 300.dp, height = 200.dp)
                .background(color = MaterialTheme.colors.background),
            waveMaxValue = waveMaxValue,
            onWaveMacValueChange = onWaveMacValueChange,
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DDSSettings(
    modifier: Modifier,
    waveMaxValue: Int,
    onWaveMacValueChange: (Int) -> Unit,
) {
    Column(modifier = modifier) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            Text(
                modifier = Modifier,
                text = "WaveMaxValue",
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.primary,
            )
            Spacer(modifier = Modifier.width(16.dp))
            TextField(
                modifier = Modifier,
                value = if (waveMaxValue > 0) waveMaxValue.toString() else "",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
                onValueChange = { text ->
                    try {
                        val newValue = text.toInt()
                        onWaveMacValueChange(newValue)
                    } catch (_: Exception) {
                        onWaveMacValueChange(0)
                    }

                },
                colors = ExposedDropdownMenuDefaults.textFieldColors(
                    textColor = MaterialTheme.colors.primary,
                )
            )
        }
    }
}
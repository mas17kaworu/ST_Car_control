package com.longkai.stcarcontrol.st_exp.compose.ui.dds

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.longkai.stcarcontrol.st_exp.compose.ui.components.CorneredContainer
import com.longkai.stcarcontrol.st_exp.compose.ui.components.SimpleTextField
import com.longkai.stcarcontrol.st_exp.compose.ui.theme.STCarTheme


@Composable
fun DDSSettingsDialog(
    waveMaxValue: Int,
    temperatureMaxValue: Int,
    serviceCount: Int,
    selectedLanguage: DdsViewModel.Language,
    onDismissRequest: () -> Unit,
    aiSDKInitState: Boolean,
    keywords: List<String> = emptyList(),
    onCurrentMaxValueChange: (Int) -> Unit,
    onVoltageMaxValueChange: (Int) -> Unit,
    onAIChooseLanguage: (DdsViewModel.Language) -> Unit,
    onKeyWordsChange: (Int, String) -> Unit,
    updateKeywords: (List<String>) -> Unit,
) {
    Dialog(
        properties = DialogProperties(
            dismissOnClickOutside = true,
        ),
        onDismissRequest = onDismissRequest,
    ) {
        DDSSettings(
            modifier = Modifier
                .sizeIn(
                    minHeight = 200.dp, minWidth = 400.dp, maxHeight = 500.dp
                ),
            selectedLanguage = selectedLanguage,
            currentMaxValue = waveMaxValue,
            temperatureMaxValue = temperatureMaxValue,
            serviceCount = serviceCount,
            aiSDKInitState = aiSDKInitState,
            onCurrentMaxValueChange = onCurrentMaxValueChange,
            onVoltageMaxValueChange = onVoltageMaxValueChange,
            onAIChooseLanguage = onAIChooseLanguage,
            keywords = keywords,
            onKeyWordsChange = onKeyWordsChange,
            updateKeywords = updateKeywords,
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DDSSettings(
    modifier: Modifier,
    selectedLanguage: DdsViewModel.Language,
    currentMaxValue: Int,
    temperatureMaxValue: Int,
    serviceCount: Int,
    aiSDKInitState: Boolean = false,
    keywords: List<String> = emptyList(),
    onCurrentMaxValueChange: (Int) -> Unit,
    onVoltageMaxValueChange: (Int) -> Unit,
    onAIChooseLanguage: (DdsViewModel.Language) -> Unit,
    onKeyWordsChange: (Int, String) -> Unit,
    updateKeywords: (List<String>) -> Unit,
) {
    CorneredContainer(
        modifier = modifier,
        backgroundColor = MaterialTheme.colors.background,
        cornerSize = 12.dp,
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            val textColor = Color.White
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically,
            ) {

                Text(
                    modifier = Modifier,
                    text = "CurrentMaxValue",
                    style = MaterialTheme.typography.body1,
                    color = textColor,
                )
                Spacer(modifier = Modifier.width(16.dp))
                SimpleTextField(
                    modifier = Modifier
                        .width(150.dp),
                    value = if (currentMaxValue > 0) currentMaxValue.toString() else "",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done,
                    ),
                    onValueChange = { text ->
                        try {
                            val newValue = text.toInt()
                            onCurrentMaxValueChange(newValue)
                        } catch (_: Exception) {
                            onCurrentMaxValueChange(0)
                        }
                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(
                        textColor = textColor,
                    )
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically,
            ) {

                Text(
                    modifier = Modifier,
                    text = "VoltageMaxValue",
                    style = MaterialTheme.typography.body1,
                    color = textColor,
                )
                Spacer(modifier = Modifier.width(16.dp))
                SimpleTextField(
                    modifier = Modifier.width(150.dp),
                    value = if (temperatureMaxValue > 0) temperatureMaxValue.toString() else "",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done,
                    ),
                    onValueChange = { text ->
                        try {
                            val newValue = text.toInt()
                            onVoltageMaxValueChange(newValue)
                        } catch (_: Exception) {
                            onVoltageMaxValueChange(0)
                        }
                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(
                        textColor = textColor,
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                modifier = Modifier,
                text = "讯飞SDK初始化结果: $aiSDKInitState",
                style = MaterialTheme.typography.body1,
                color = textColor,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedLanguage == DdsViewModel.Language.Chinese,
                    onClick = {
                        onAIChooseLanguage(DdsViewModel.Language.Chinese)
                    },
                    colors = androidx.compose.material.RadioButtonDefaults.colors(
//                    selectedColor = androidx.compose.ui.graphics.Color.Blue,
//                    unselectedColor = androidx.compose.ui.graphics.Color.Gray
                    )
                )
                Text(text = "中文", color = textColor)

                RadioButton(
                    selected = selectedLanguage == DdsViewModel.Language.English,
                    onClick = {
                        onAIChooseLanguage(DdsViewModel.Language.English)
                    },
                    colors = androidx.compose.material.RadioButtonDefaults.colors()
                )
                Text(text = "英文", color = textColor)
            }
            Spacer(modifier = Modifier.height(16.dp))
            val textValues = remember { mutableStateListOf(*Array(serviceCount) { "" }) }

            // Ensure the textValues list is updated when initialValues changes
            LaunchedEffect(Unit) {
                textValues.clear()
                textValues.addAll(List(serviceCount) { index ->
                    keywords.getOrNull(index) ?: ""  // Use initialValues or empty string
                })
            }

            for (index in 1..serviceCount) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Key word $index", color = textColor)
                    Spacer(modifier = Modifier.width(8.dp))
                    SimpleTextField(
                        modifier = Modifier.width(200.dp),
                        value = textValues[index - 1],
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done,
                        ),
                        onValueChange = { text ->
                            textValues[index - 1] = text
                        },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(
                            textColor = textColor,
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
//                    Button(
//                        modifier = Modifier.width(60.dp),
//                        onClick = {
//                            onKeyWordsChange(index - 1, textValues[index - 1])
//                        },
//                    ) {
//                        Text(text = "OK")
//                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    updateKeywords(textValues)
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.White,
                )
            ) {
                Text(text = "Save", color = Color.Black)
            }

            Text(
                modifier = Modifier.sizeIn(maxWidth = 350.dp),
                text = "说明：科大讯飞离线语音识别SDK 第一次初始化需要联网。" +
                        "\n第一次初始化成功后可不再联网。\n语言文件放置平板\\sdcard\\ai\\esr" +
                        "目录下面，区分中英文文件夹。不可修改文件名",
                style = MaterialTheme.typography.body1,
                color = textColor,
            )
        }
    }
}

@Composable
@Preview
private fun DDSSettingsDialogPreview() {
    STCarTheme {
        DDSSettingsDialog(
            selectedLanguage = DdsViewModel.Language.Chinese,
            onDismissRequest = {},
            waveMaxValue = 10,
            temperatureMaxValue = 30,
            serviceCount = 2,
            aiSDKInitState = true,
            onCurrentMaxValueChange = {},
            onVoltageMaxValueChange = {},
            onAIChooseLanguage = {},
            onKeyWordsChange = { index, value -> },
            updateKeywords = {},
        )
    }
}
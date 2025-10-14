package com.longkai.stcarcontrol.st_exp.compose.ui.dds

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import coil.compose.AsyncImage
import com.longkai.stcarcontrol.st_exp.R
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
    cnKeywords: List<String> = emptyList(),
    enKeywords: List<String> = emptyList(),
    onCurrentMaxValueChange: (Int) -> Unit,
    onVoltageMaxValueChange: (Int) -> Unit,
    onAIChooseLanguage: (DdsViewModel.Language) -> Unit,
    onKeyWordsChange: (Int, String) -> Unit,
    updateKeywords: (List<String>, List<String>) -> Unit,
    // New image selection related params
    imageUris: List<String?>,
    onPickImage: (Int) -> Unit,
    onClearImage: (Int) -> Unit,
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
            cnKeywords = cnKeywords,
            enKeywords = enKeywords,
            onKeyWordsChange = onKeyWordsChange,
            updateKeywords = updateKeywords,
            imageUris = imageUris,
            onPickImage = onPickImage,
            onClearImage = onClearImage,
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
    cnKeywords: List<String> = emptyList(),
    enKeywords: List<String> = emptyList(),
    onCurrentMaxValueChange: (Int) -> Unit,
    onVoltageMaxValueChange: (Int) -> Unit,
    onAIChooseLanguage: (DdsViewModel.Language) -> Unit,
    onKeyWordsChange: (Int, String) -> Unit,
    updateKeywords: (List<String>, List<String>) -> Unit,
    // New image selection related params
    imageUris: List<String?>,
    onPickImage: (Int) -> Unit,
    onClearImage: (Int) -> Unit,
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
                Spacer(modifier = Modifier.width(100.dp))
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
                Spacer(modifier = Modifier.width(100.dp))
                RadioButton(
                    selected = selectedLanguage == DdsViewModel.Language.English,
                    onClick = {
                        onAIChooseLanguage(DdsViewModel.Language.English)
                    },
                    colors = androidx.compose.material.RadioButtonDefaults.colors()
                )
                Text(text = "English", color = textColor)
            }
            Spacer(modifier = Modifier.height(16.dp))
            val cnTextValues = remember { mutableStateListOf(*Array(serviceCount) { "" }) }
            val enTextValues = remember { mutableStateListOf(*Array(serviceCount) { "" }) }

            // Ensure the textValues list is updated when initialValues changes
            LaunchedEffect(Unit) {
                cnTextValues.clear()
                enTextValues.clear()
                cnTextValues.addAll(List(serviceCount) { index ->
                    cnKeywords.getOrNull(index) ?: ""  // Use initialValues or empty string
                })
                enTextValues.addAll(List(serviceCount) { index ->
                    enKeywords.getOrNull(index) ?: ""  // Use initialValues or empty string
                })
            }

            for (index in 1..serviceCount) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Key word $index", color = textColor)
                    Spacer(modifier = Modifier.weight(1f))
                    SimpleTextField(
                        modifier = Modifier.width(200.dp),
                        value = cnTextValues[index - 1],
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done,
                        ),
                        onValueChange = { text ->
                            cnTextValues[index - 1] = text
                        },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(
                            textColor = textColor,
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    SimpleTextField(
                        modifier = Modifier.width(200.dp),
                        value = enTextValues[index - 1],
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done,
                        ),
                        onValueChange = { text ->
                            enTextValues[index - 1] = text
                        },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(
                            textColor = textColor,
                        )
                    )
                    //
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
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val buttonColor = if (isPressed) Color.Gray else Color.White
            Button(
                modifier = Modifier.fillMaxWidth(),
                interactionSource = interactionSource,
                onClick = {
                    updateKeywords(cnTextValues, enTextValues)
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = buttonColor,
                )
            ) {
                Text(text = "Save", color = Color.Black)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "图片配置", color = textColor, style = MaterialTheme.typography.h6
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                modifier = Modifier.sizeIn(maxWidth = 350.dp),
                text = "说明：科大讯飞离线语音识别SDK 第一次初始化需要联网。" +
                    "\n第一次初始化成功后可不再联网。\n语言文件放置平板\\sdcard\\ai\\esr" +
                    "目录下面，区分中英文文件夹。不可修改文件名",
                style = MaterialTheme.typography.body1,
                color = textColor,
            )
            Spacer(modifier = Modifier.height(8.dp))
            for (i in 0 until 10) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Button(
                        onClick = { onPickImage(i) },
                        modifier = Modifier.width(110.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.DarkGray
                        )
                    ) { Text(text = "选择图片${i + 1}") }
                    Spacer(modifier = Modifier.width(12.dp))
                    val uriString = imageUris.getOrNull(i)
                    if (uriString.isNullOrBlank().not()) {
                        AsyncImage(
                            model = uriString,
                            contentDescription = "预览${i + 1}",
                            modifier = Modifier
                                .width(80.dp)
                                .height(80.dp)
                        )
                    } else {
                        // simple placeholder box using Text
                        androidx.compose.material.Surface(
                            modifier = Modifier
                                .width(80.dp)
                                .height(80.dp),
                            color = Color.Gray.copy(alpha = 0.3f)
                        ) {
                            androidx.compose.material.Text(
                                text = "无",
                                color = Color.White,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    if (uriString != null) {
                        Button(
                            onClick = { onClearImage(i) },
                            modifier = Modifier.width(70.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red.copy(alpha = 0.6f))
                        ) { Text(text = "清除", color = Color.White) }
                    }
                }
            }
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
            updateKeywords = {c, e ->},
            imageUris = List(10) { null },
            onPickImage = {},
            onClearImage = {},
        )
    }
}
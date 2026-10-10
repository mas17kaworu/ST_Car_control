package com.longkai.stcarcontrol.st_exp.compose.ui.chassis.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.longkai.stcarcontrol.st_exp.R

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChassisValueInput(
    value: Int?,
    range: IntRange,
    enabled: Boolean,
    interactionKey: Long,
    label: String,
    unit: String,
    onValueChange: (Int) -> Unit,
    onSubmit: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    key(interactionKey) {
        var draft by remember { mutableStateOf(value?.toString().orEmpty()) }
        var focused by remember { mutableStateOf(false) }
        val focusManager = LocalFocusManager.current
        val keyboard = LocalSoftwareKeyboardController.current
        val invalid = draft.toIntOrNull()?.let { it !in range } ?: true
        val showError = invalid && (value != null || draft.isNotEmpty() || focused)
        val errorMessage = stringResource(R.string.chassis_input_range, range.first, range.last)
        val placeholder = stringResource(R.string.chassis_no_data)
        val shape = RoundedCornerShape(6.dp)
        LaunchedEffect(value) {
            if (draft.toIntOrNull() != value) draft = value?.toString().orEmpty()
        }
        fun submit() {
            if (!enabled) return
            onSubmit(draft)
            if (!invalid) {
                keyboard?.hide()
                focusManager.clearFocus()
            }
        }
        BasicTextField(
            value = draft,
            onValueChange = { text ->
                draft = text
                text.toIntOrNull()?.takeIf { it in range }?.let(onValueChange)
            },
            enabled = enabled,
            singleLine = true,
            textStyle = TextStyle(color = if (enabled) Color(0xFFE1EDF4) else Color(0xFF8196A4),
                fontSize = 14.sp, textAlign = TextAlign.End),
            cursorBrush = SolidColor(Color(0xFF39BEE4)),
            keyboardOptions = KeyboardOptions(
                keyboardType = if (range.first < 0) KeyboardType.Ascii else KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { submit() }),
            modifier = modifier.width(116.dp).height(48.dp)
                .onFocusChanged { focused = it.isFocused }
                .onPreviewKeyEvent {
                    if (it.key == Key.Enter || it.key == Key.NumPadEnter) {
                        if (it.type == KeyEventType.KeyUp) submit()
                        true
                    } else false
                }
                .semantics {
                    contentDescription = "$label ($unit)"
                    if (showError) error(errorMessage)
                },
            decorationBox = { innerTextField ->
                Box(contentAlignment = Alignment.Center) {
                    Row(
                        Modifier.fillMaxWidth().height(32.dp)
                            .background(Color(0xFF1E2C36), shape)
                            .border(1.dp, when {
                                showError -> Color(0xFFEF5350)
                                focused -> Color(0xFF39BEE4)
                                else -> Color(0xFF809AAA).copy(alpha = .4f)
                            }, shape)
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
                            if (draft.isEmpty() && !focused) {
                                Text(placeholder, color = Color(0xFF8196A4), fontSize = 14.sp)
                            }
                            innerTextField()
                        }
                        Text(unit, color = Color(0xFFADC0CC), fontSize = 10.sp, modifier = Modifier.padding(start = 6.dp))
                    }
                }
            }
        )
    }
}

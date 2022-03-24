package com.longkai.stcarcontrol.st_exp.compose.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


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

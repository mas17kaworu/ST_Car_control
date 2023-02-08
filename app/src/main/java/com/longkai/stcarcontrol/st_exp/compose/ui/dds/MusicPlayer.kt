package com.longkai.stcarcontrol.st_exp.compose.ui.dds

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.longkai.stcarcontrol.st_exp.R
import com.longkai.stcarcontrol.st_exp.music.STPlayer

@Composable
fun MusicPlayer(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val stPlayer = remember { STPlayer(context) }
    var isPlaying by remember { mutableStateOf(false) }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.clickable {
                stPlayer.previous()
            },
            painter = painterResource(id = R.mipmap.ic_previous),
            tint = Color.White,
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            modifier = Modifier.clickable {
                if (isPlaying) {
                    stPlayer.stop()
                } else {
                    stPlayer.playMusic()
                }
                isPlaying = !isPlaying

            },
            painter = if (isPlaying) {
                painterResource(id = R.mipmap.ic_stop)
            } else {
                painterResource(id = R.mipmap.ic_play)
            },
            tint = Color.White,
            contentDescription = null,
        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            modifier = Modifier.clickable {
                stPlayer.next()
            },
            painter = painterResource(id = R.mipmap.ic_next),
            tint = Color.White,
            contentDescription = null
        )
    }

}
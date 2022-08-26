package com.longkai.stcarcontrol.st_exp.compose.ui.dds

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.longkai.stcarcontrol.st_exp.R

@Composable
fun ServiceImagePicker(
    modifier: Modifier = Modifier,
    currentImageUri: Uri? = null,
    onImagePicked: ((uri: Uri?) -> Unit),
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) {
        onImagePicked(it)
    }

    println("zcf imagePicker: $currentImageUri")
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current).apply {
            fallback(R.drawable.ic_pick_image)
            data(currentImageUri)
        }.build(),
        contentDescription = "Service image",
        modifier = modifier
            .clickable { launcher.launch(arrayOf("image/*")) },
        contentScale = ContentScale.Crop
    )
}
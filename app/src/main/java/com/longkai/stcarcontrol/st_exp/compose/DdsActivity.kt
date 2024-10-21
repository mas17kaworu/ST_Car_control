package com.longkai.stcarcontrol.st_exp.compose

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.ProvideWindowInsets
import com.longkai.stcarcontrol.st_exp.STCarApplication
import com.longkai.stcarcontrol.st_exp.ai.IFlytekAbilityManager
import com.longkai.stcarcontrol.st_exp.compose.data.dds.notification.DigitalKeyUnlockService
import com.longkai.stcarcontrol.st_exp.compose.ui.components.ActivityViewContainer
import com.longkai.stcarcontrol.st_exp.compose.ui.dds.DdsScreen
import com.longkai.stcarcontrol.st_exp.compose.ui.dds.DdsViewModel
import com.longkai.stcarcontrol.st_exp.compose.ui.theme.STCarTheme

class DdsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val appContainer = (application as STCarApplication).appContainer
        setContent {
            STCarTheme {
                ProvideWindowInsets(windowInsetsAnimationsEnabled = true) {
                    ActivityViewContainer {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp)
                        ) {
                            val ddsViewModel: DdsViewModel = viewModel(
                                factory = DdsViewModel.provideFactory(
                                    appContainer.ddsRepo,
                                    appContainer.aiRepo,
                                )
                            )
                            DdsScreen(ddsViewModel = ddsViewModel)
                        }
                    }
                }
            }
        }

        startService(Intent(this, DigitalKeyUnlockService::class.java))

        // Init AI
        activityResultLauncher.launch(
            arrayListOf(Manifest.permission.RECORD_AUDIO).apply {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    add(Manifest.permission.BLUETOOTH_CONNECT)
                }
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                    //蓝牙设备权限
                    add(Manifest.permission.READ_EXTERNAL_STORAGE)
                } else {
                    add(Manifest.permission.READ_MEDIA_IMAGES)
                    add(Manifest.permission.READ_MEDIA_VIDEO)
                    add(Manifest.permission.READ_MEDIA_AUDIO)
                }
            }.toTypedArray()
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
        }
    }

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val allGranted = permissions.all { it.value }
            val appContainer = (application as STCarApplication).appContainer
            if (allGranted) {
                IFlytekAbilityManager.getInstance().initializeSdk(
                    context = this,
                    aiRepo = appContainer.aiRepo,
                )
            }
            permissions.entries.forEach {
                val permissionName = it.key
                val isGranted = it.value
                if (isGranted) {
                    // Permission is granted
                } else {
                    // Permission is denied
                    Toast.makeText(
                        this,
                        "${permissionName}被拒绝了，请在应用设置里打开权限",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
}

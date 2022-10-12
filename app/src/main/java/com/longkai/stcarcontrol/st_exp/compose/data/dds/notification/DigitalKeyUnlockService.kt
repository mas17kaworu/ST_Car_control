package com.longkai.stcarcontrol.st_exp.compose.data.dds.notification

import android.app.*
import android.app.PendingIntent.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.longkai.stcarcontrol.st_exp.R
import com.longkai.stcarcontrol.st_exp.STCarApplication
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.TriggerCondition
import com.longkai.stcarcontrol.st_exp.compose.data.dds.service.DdsService
import com.longkai.stcarcontrol.st_exp.compose.data.dds.test.ScreenLog
import com.longkai.stcarcontrol.st_exp.compose.data.dds.test.TAG_DDS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DigitalKeyUnlockService: Service() {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        if (intent?.action.equals(ACTION_CANCEL)) {
            stopForeground(true)
            stopSelf()
            return START_NOT_STICKY
        }

        startForeground()

        val ddsRepo = (application as STCarApplication).appContainer.ddsRepo
        scope.launch {
            val unlockServiceFlow = ddsRepo.expressServices()
                .map { expressServices ->
                    expressServices.firstOrNull { it.triggerCondition == TriggerCondition.DigitalKeyUnlock }
                }
                .distinctUntilChanged()
            val lockServiceFlow = ddsRepo.expressServices()
                .map { expressServices ->
                    expressServices.firstOrNull { it.triggerCondition == TriggerCondition.DigitalKeyLock }
                }
                .distinctUntilChanged()

            combine(
                ddsRepo.digitalKeyState,
                unlockServiceFlow,
                lockServiceFlow
            ) { keyState, unlockService, lockService ->
                when (keyState) {
                    DdsService.DigitalKeyState.LockDoor -> {
                        Log.i(TAG_DDS, "unlock service: $unlockService")
                        unlockService?.let {
                            Log.i(TAG_DDS, "execute unlock service")
                            ddsRepo.executeExpressService(unlockService)
                        }
                    }
                    DdsService.DigitalKeyState.UnlockDoor -> {
                        Log.i(TAG_DDS, "lock service: $lockService")
                        lockService?.let {
                            Log.i(TAG_DDS, "execute lock service")
                            ddsRepo.executeExpressService(lockService)
                        }
                    }
                    DdsService.DigitalKeyState.Reset -> {}
                }
            }
                .catch { e ->
                    e.printStackTrace()
                }
                .collect()
        }
        return START_STICKY
    }

    private fun startForeground() {
        val cancelIntent = Intent(this, DigitalKeyUnlockService::class.java)
            .let { notificationIntent ->
                notificationIntent.action = ACTION_CANCEL
                getService(this, 0, notificationIntent, FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT)
            }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentText("Digital key unlock service is running")
            .setSmallIcon(R.mipmap.ic_launcher)
            .addAction(android.R.drawable.ic_delete, "Cancel", cancelIntent)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val name = applicationContext.getString(R.string.app_name)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance)

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        const val CHANNEL_ID = "STCarControl"
        const val NOTIFICATION_ID = 10000
        const val ACTION_CANCEL = "cancel"
    }
}
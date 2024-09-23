package com.longkai.stcarcontrol.st_exp.media

import android.content.Context
import android.media.AudioManager
import android.os.Handler
import android.os.Looper


/**
 * @Desc:  简易音频设备管理类
 */
class AudioDeviceManager(private val context: Context) {

    companion object {
        private const val TAG = "AudioDeviceManager"
    }

    private val audioManager: AudioManager by lazy {
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }


    fun autoConnectBluetoothSco() {
        if (isBluetoothHeadsetConnected()) audioManager.startBluetoothSco()
    }


    fun startBluetoothSco() {
        fun openBleMic() {
            audioManager.apply {
                isSpeakerphoneOn = false
                isBluetoothScoOn = true
                if (audioManager.mode == AudioManager.MODE_NORMAL) {
                    stopBluetoothSco()
                    startBluetoothSco()
                }
            }
        }
        openBleMic()
        Handler(Looper.getMainLooper()).postDelayed({
            val isBluetoothScoOn = audioManager.isBluetoothScoOn
            if (!isBluetoothScoOn) openBleMic()
        }, 2000)
    }

    fun stopBluetoothSco() {
        audioManager.apply {
            stopBluetoothSco()
            isBluetoothScoOn = false
            isSpeakerphoneOn = true
            mode = AudioManager.MODE_NORMAL
        }
    }

    private fun isBluetoothHeadsetConnected(): Boolean {
        return audioManager.isBluetoothScoAvailableOffCall
    }

}
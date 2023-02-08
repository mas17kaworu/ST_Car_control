package com.longkai.stcarcontrol.st_exp.music

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.util.Log
import com.longkai.stcarcontrol.st_exp.R
import com.longkai.stcarcontrol.st_exp.Utils.FileUtils
import com.longkai.stcarcontrol.st_exp.Utils.FileUtils10.getFilesUnderDownloadST
import com.longkai.stcarcontrol.st_exp.Utils.FileUtils10.readSoundsInfoFile
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDSound.CMDSoundsInfo
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter
import com.longkai.stcarcontrol.st_exp.model.SoundsInfo
import com.longkai.stcarcontrol.st_exp.music.MyMediaPlayer.PlayState

class STPlayer constructor(
    val context: Context
) {
    private var isPlaying = false

    private var mediaPlayer: MyMediaPlayer = MyMediaPlayer.getInstance(context)

    private val soundsList: MutableList<Triple<String, Uri?, SoundsInfo?>> = getFilesUnderDownloadST(context).toMutableList()

    private var playingIndex = 0

    private val handler = Handler()

    lateinit var sendCMDTask : SendSoundsInfoRegular

    init {
        try {
            if (soundsList.isEmpty()) {
                soundsList.add(
                    0, Triple(
                        "st01-default",
                        FileUtils.getResUri(R.raw.st01_wav, context),
                        readSoundsInfoFile(
                            FileUtils.getResUri(R.raw.st01_json,context), context
                        )
                    )
                )
            }
        } catch (e: Exception) {
            println(e)
        }
    }

    fun playMusic() {
        val uri = soundsList[playingIndex].second
        val soundsInfo = soundsList[playingIndex].third
        isPlaying = true
        mediaPlayer.play(uri)
        mediaPlayer.setPlayStateListener { state ->
            if (state == PlayState.STATE_PLAYING) {
                isPlaying = true
                sendCMDTask = SendSoundsInfoRegular(soundsInfo)
                handler.post(sendCMDTask)
            } else if (state == PlayState.STATE_PAUSE || state == PlayState.STATE_IDLE) {
                isPlaying= false
                handler.removeCallbacks(sendCMDTask)
            }
        }
    }

    fun stop() {
        mediaPlayer.stop()
    }

    fun next() {
        if (isPlaying) {
            mediaPlayer.stop()
            playingIndex++
            if (playingIndex >= soundsList.size) {
                playingIndex = 0
            }
            playMusic()
        }
    }

    fun previous() {
        if (isPlaying) {
            mediaPlayer.stop()
            playingIndex--
            if (playingIndex < 0) {
                playingIndex = soundsList.size - 1
            }
            playMusic()
        }
    }



    inner class SendSoundsInfoRegular constructor(private val soundsInfo: SoundsInfo?) :
        Runnable {
        private var infoIndex = 0
        private val doNothing: CommandListenerAdapter<BaseResponse> = CommandListenerAdapter()
        override fun run() {

            if (soundsInfo != null) {
                Log.d(
                    "STPlayer", "longkai22 frequency " + soundsInfo.frequency[infoIndex] +
                            " amptitude " + soundsInfo.amplitude[infoIndex]
                )
                // Send command
                ServiceManager.getInstance().sendCommandToCar(
                    CMDSoundsInfo(
                        soundsInfo.frequency[infoIndex],
                        soundsInfo.amplitude[infoIndex]
                    ), doNothing
                )
                infoIndex++
                if (infoIndex < soundsInfo.frequency.size && infoIndex < soundsInfo.amplitude.size) {
                    handler.postDelayed(this, 500)
                }
            }
        }
    }
}
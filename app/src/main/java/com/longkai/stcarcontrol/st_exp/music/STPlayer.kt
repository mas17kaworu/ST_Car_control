package com.longkai.stcarcontrol.st_exp.music

import android.content.Context
import android.net.Uri
import android.util.Log
import com.longkai.stcarcontrol.st_exp.Utils.FileUtils
import com.longkai.stcarcontrol.st_exp.Utils.FileUtils10.getFilesUnderDownloadST
import com.longkai.stcarcontrol.st_exp.Utils.FileUtils10.readSoundsInfoFile
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDSound.CMDSoundsInfo
import com.longkai.stcarcontrol.st_exp.fragment.CarBackOLED2Fragment
import com.longkai.stcarcontrol.st_exp.model.SoundsInfo
import java.util.*

class STPlayer constructor(
    val context: Context
) {
//    private var isPlaying = false
//
//    private lateinit var mediaPlayer: MyMediaPlayer
//
//    private val soundsList: MutableList<Triple<String, Uri, SoundsInfo>>
//
//    private val sendCMDTask: SendSoundsInfoRegular? = null
//
//    fun init() {
//        mediaPlayer = MyMediaPlayer.getInstance(context)
//
//        soundsList = getFilesUnderDownloadST(context)
//        try {
//            if (soundsList.isEmpty()) soundsList = LinkedList()
//            soundsList.add(
//                0, Triple(
//                    "st01-default",
//                    FileUtils.getResUri(R.raw.st01_wav, this.getContext()),
//                    readSoundsInfoFile(
//                        FileUtils.getResUri(R.raw.st01_json, this.getContext()), this.getContext()
//                    )
//                )
//            )
//        } catch (e: Exception) {
//            println(e)
//        }
//    }
//
//    fun playMusic(uri: Uri, soundsInfo: SoundsInfo) {
//        isPlaying = true
//
//    }
//
//
//    internal class SendSoundsInfoRegular constructor(private val soundsInfo: SoundsInfo) :
//        Runnable {
//        private var infoIndex = 0
//        override fun run() {
//            // Send command
//            Log.d(
//                CarBackOLED2Fragment.TAG, "longkai22 frequency " + soundsInfo.frequency[infoIndex] +
//                        " amptitude " + soundsInfo.amplitude[infoIndex]
//            )
//            ServiceManager.getInstance().sendCommandToCar(
//                CMDSoundsInfo(
//                    soundsInfo.frequency[infoIndex],
//                    soundsInfo.amplitude[infoIndex]
//                ), doNothing
//            )
//            infoIndex++
//            if (infoIndex < soundsInfo.frequency.size && infoIndex < soundsInfo.amplitude.size) {
//                handler.postDelayed(this, 500)
//            }
//        }
//    }
}
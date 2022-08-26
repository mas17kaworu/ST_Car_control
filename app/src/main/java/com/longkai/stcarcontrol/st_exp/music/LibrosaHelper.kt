package com.longkai.stcarcontrol.st_exp.music;

import ai.djl.ndarray.NDArray
import ai.djl.ndarray.NDManager
import ai.djl.ndarray.types.Shape
import com.jlibrosa.audio.JLibrosa
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LibrosaHelper {
  private val jLibrosa: JLibrosa = JLibrosa()

  fun loadWAVFile(audioFilePath: String) {
    GlobalScope.launch(Dispatchers.Default) {
      val defaultSampleRate = 24000;        //-1 value implies the method to use default sample rate
      val defaultAudioDuration = -1;    //-1 value implies the method to process complete audio duration

      // To read the magnitude values of audio files - equivalent to librosa.load('../audioFiles/audio.wav', sr=None) function
      val audioFeatureValues =
        jLibrosa.loadAndRead(audioFilePath, defaultSampleRate, defaultAudioDuration)

      // val audioFeatureValuesList =
      //   jLibrosa.loadAndReadAsList(audioFilePath, defaultSampleRate, defaultAudioDuration)

      // To read sample rate of audio file
      val sr = jLibrosa.sampleRate
      println("Sample Rate: $sr")


    }
    // jLibrosa.generateInvSTFTFeatures()
  }
}

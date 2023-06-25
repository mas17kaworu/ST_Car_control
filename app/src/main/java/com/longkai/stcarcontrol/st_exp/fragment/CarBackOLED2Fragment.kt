package com.longkai.stcarcontrol.st_exp.fragment

import android.media.audiofx.Visualizer
import android.media.audiofx.Visualizer.OnDataCaptureListener
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.longkai.stcarcontrol.st_exp.ConstantData
import com.longkai.stcarcontrol.st_exp.R
import com.longkai.stcarcontrol.st_exp.Utils.FileUtils
import com.longkai.stcarcontrol.st_exp.Utils.FileUtils10.getFilesUnderDownloadST
import com.longkai.stcarcontrol.st_exp.Utils.FileUtils10.readSoundsInfoFile
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDOLEDBackList.CMDOLEDAuto1
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDOLEDBackList.CMDOLEDAuto2
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDOLEDBackList.CMDOLEDAuto3
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDOLEDBackList.CMDOLEDBase
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDOLEDBackList.CMDOLEDBrake
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDOLEDBackList.CMDOLEDPosition
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDOLEDBackList.CMDOLEDReversing
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDOLEDBackList.CMDOLEDStopAll
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDOLEDBackList.CMDOLEDTurnLeft
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDOLEDBackList.CMDOLEDTurnRight
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDSound.CMDSoundsInfo
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter
import com.longkai.stcarcontrol.st_exp.customView.oled2.OLED2Controller
import com.longkai.stcarcontrol.st_exp.databinding.FragmentCarBackOled2Binding
import com.longkai.stcarcontrol.st_exp.model.SoundsInfo
import com.longkai.stcarcontrol.st_exp.music.AudioVisualConverter
import com.longkai.stcarcontrol.st_exp.music.MyMediaPlayer
import com.longkai.stcarcontrol.st_exp.music.MyMediaPlayer.PlayState
import com.longkai.stcarcontrol.st_exp.music.MyMediaPlayer.PlayStateListener
import java.util.LinkedList
import java.util.Locale

class CarBackOLED2Fragment : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentCarBackOled2Binding
    private val mView get() = binding.root
    private val ivReversing get() = binding.btnBackOledReversing
    private val ivBrake get() = binding.btnBackOledBreak
    private val ivPosition get() = binding.btnBackOledPosition
    private val ivTurnLeft get() = binding.btnBackOledLeft
    private val ivTurnRight get() = binding.btnBackOledRight
    private val ivAuto1 get() = binding.btnBackOledA1
    private val ivAuto2 get() = binding.btnBackOledA2
    private val ivAuto3 get() = binding.btnBackOledA3
    private val ivPlayOrPause get() = binding.btnPlayAudio
    private val ivPlayNext get() = binding.btnPlayNext
    private val ivPlayPrevious get() = binding.btnPlayPrevious
    private val ivStop get() = binding.btnBackOledStop

    private lateinit var oledController: OLED2Controller
    private lateinit var mediaPlayer: MyMediaPlayer
    private lateinit var soundsList: MutableList<Triple<String, Uri?, SoundsInfo?>>

    private val audioVisualConverter = AudioVisualConverter()

    private var isVisualizerInit = false
    private val stopSendCMD = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mediaPlayer = MyMediaPlayer.getInstance(this.context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCarBackOled2Binding.inflate(layoutInflater, container, false)
        ivReversing.setOnClickListener(this)
        ivBrake.setOnClickListener(this)
        ivPosition.setOnClickListener(this)
        ivTurnLeft.setOnClickListener(this)
        ivTurnRight.setOnClickListener(this)
        ivAuto1.setOnClickListener(this)
        ivAuto2.setOnClickListener(this)
        ivAuto3.setOnClickListener(this)
        ivPlayOrPause.setOnClickListener(this)
        ivPlayPrevious.setOnClickListener(this)
        ivPlayNext.setOnClickListener(this)
        ivStop.setOnClickListener(this)

        oledController = OLED2Controller(this, ivReversing, ivBrake, ivPosition)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        refreshUI()
        soundsList = getFilesUnderDownloadST(requireContext()).toMutableList()
        try {
            soundsList.add(
                0, Triple<String, Uri, SoundsInfo?>(
                    "st01-default",
                    FileUtils.getResUri(R.raw.st01_wav, this.context),
                    readSoundsInfoFile(
                        FileUtils.getResUri(R.raw.st01_json, this.context), requireContext()
                    )
                )
            )
        } catch (e: Exception) {
            println(e)
        }
    }

    private var sendCMDTask: SendSoundsInfoRegular? = null
    private fun playMusic(uri: Uri?, soundsInfo: SoundsInfo?) {
        isVisualizerInit = false
        mediaPlayer = MyMediaPlayer.getInstance(this.context)
        mediaPlayer.play(uri)
        mediaPlayer.setPlayStateListener(PlayStateListener { state ->
            if (state == PlayState.STATE_PLAYING) {
                isPlaying = true
                soundsInfo?.let {
                    sendCMDTask = SendSoundsInfoRegular(soundsInfo)
                    handler.post(sendCMDTask!!)
                }
                doWhenStartPlaying()
                //initVisualizer();
            } else if (state == PlayState.STATE_PAUSE || state == PlayState.STATE_IDLE) {
                sendCMDTask?.let {
                    handler.removeCallbacks(it)
                }
                doWhenStopPlaying()
            }
        })
    }

    private var visualizer: Visualizer? = null
    private val sampleIndex = 0
    private val sum = 0
    private val dataCaptureListener: OnDataCaptureListener = object : OnDataCaptureListener {
        override fun onWaveFormDataCapture(
            visualizer: Visualizer,
            waveform: ByteArray,
            samplingRate: Int
        ) {
            //Log.d(TAG, "waveform samplingRate " + samplingRate + " waveform length " + waveform.length);
            //audioView.post(new Runnable() {
            //    @Override
            //    public void run() {
            //        audioView.setWaveData(waveform);
            //    }
            //});
        }

        override fun onFftDataCapture(visualizer: Visualizer, fft: ByteArray, samplingRate: Int) {
            //Log.d(TAG, "onFftDataCapture samplingRate" + samplingRate + " FftData " + fft.length);
            //audioView2.post(new Runnable() {
            //    @Override
            //    public void run() {
            //        audioView2.setWaveData(fft);
            Log.d(
                TAG,
                String.format(
                    Locale.getDefault(),
                    "当前分贝: %s db",
                    audioVisualConverter.getVoiceSize(fft)
                )
            )
            //sum += audioVisualConverter.getVoiceSizeGoogle(fft);
            //if (sampleIndex < SAMPLES_COUNT_PER_500_MS) {
            //    sampleIndex ++;
            //} else {
            //    Log.d(TAG, String.format(Locale.getDefault(), "当前分贝: %s db", sum / SAMPLES_COUNT_PER_500_MS));
            //    sum = 0;
            //    sampleIndex = 0;
            //}
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        super.onDestroy()
        if (visualizer != null) {
            visualizer!!.release()
        }
        mediaPlayer.stop()
        mediaPlayer.release()
    }

    private fun initVisualizer() {
        if (isVisualizerInit) {
            return
        }
        isVisualizerInit = true
        //audioVisualConverter = new AudioVisualConverter();
        Log.d(TAG, "initVisualizer()")
        try {
            val mediaPlayerId = mediaPlayer.mediaPlayerId
            Log.i(TAG, "mediaPlayerId: $mediaPlayerId")
            if (visualizer != null) {
                visualizer!!.release()
            }
            visualizer = Visualizer(mediaPlayerId)
            val captureSize = 128
            val captureRate = 2000 * SAMPLES_COUNT_PER_500_MS //2000 -- 2hz
            Log.d(TAG, "精度: $captureSize")
            Log.d(TAG, "刷新频率: $captureRate")
            visualizer!!.captureSize = captureSize
            visualizer!!.setDataCaptureListener(dataCaptureListener, captureRate, true, true)
            visualizer!!.scalingMode = Visualizer.SCALING_MODE_NORMALIZED
            visualizer!!.enabled = true
        } catch (e: Exception) {
            Log.e(TAG, "请检查录音权限 $e")
            isVisualizerInit = false
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_back_oled_reversing -> clickBtn(
                ConstantData.sBackOLEDReverse,
                ivReversing,
                CMDOLEDReversing()
            )

            R.id.btn_back_oled_break -> clickBtn(
                ConstantData.sBackOLEDBreak,
                ivBrake,
                CMDOLEDBrake()
            )

            R.id.btn_back_oled_position -> clickBtn(
                ConstantData.sBackOLEDPosition,
                ivPosition,
                CMDOLEDPosition()
            )

            R.id.btn_back_oled_left -> clickBtn(
                ConstantData.sBackOLEDTurnLeft,
                ivTurnLeft,
                CMDOLEDTurnLeft()
            )

            R.id.btn_back_oled_right -> clickBtn(
                ConstantData.sBackOLEDTurnRight,
                ivTurnRight,
                CMDOLEDTurnRight()
            )

            R.id.btn_back_oled_a1 -> clickBtn(ConstantData.sBackOLEDAuto1, ivAuto1, CMDOLEDAuto1())
            R.id.btn_back_oled_a2 -> clickBtn(ConstantData.sBackOLEDAuto2, ivAuto2, CMDOLEDAuto2())
            R.id.btn_back_oled_a3 -> clickBtn(ConstantData.sBackOLEDAuto3, ivAuto3, CMDOLEDAuto3())
            R.id.btnPlayAudio -> playOrPause()
            R.id.btnPlayNext -> playNext()
            R.id.btnPlayPrevious -> playPrevious()
            R.id.btn_back_oled_stop -> clickBtn(
                ConstantData.sBackOLEDStopOLED,
                ivStop,
                CMDOLEDStopAll()
            )
        }
        refreshUI()
    }

    private fun clickBtn(index: Int, view: ImageView?, command: CMDOLEDBase) {
        if (command !is CMDOLEDStopAll && CMDOLEDBase.stopAll) {
            return
        }
        if (ConstantData.sBackOLEDStatus[index] == 1) {
            //ConstantData.sBackOLEDStatus[index] = 0;
            command.turnOff()
            //view.setSelected(false);
        } else {
            //ConstantData.sBackOLEDStatus[index] = 1;
            command.turnOn()
            //playMusic(FileUtils.getResUri(R.raw.st01, this.getContext()));
            //view.setSelected(true);
        }
        ServiceManager.getInstance().sendCommandToCar(command, CommandListenerAdapter<CMDOLEDBase.Response>())
    }

    private fun refreshUI() {
        oledController.updateState(updateStateByCMD())
        val cmdPayload = CMDOLEDBase.getPayload()
        if (cmdPayload[0].toInt() and CMDOLEDBase.Reversing.toInt() != 0) {
            ivReversing.isSelected = true
            ConstantData.sBackOLEDStatus[ConstantData.sBackOLEDReverse] = 1
        } else {
            ivReversing.isSelected = false
            ConstantData.sBackOLEDStatus[ConstantData.sBackOLEDReverse] = 0
        }
        if (cmdPayload[0].toInt() and CMDOLEDBase.Brake.toInt() != 0) {
            ivBrake.isSelected = true
            ConstantData.sBackOLEDStatus[ConstantData.sBackOLEDBreak] = 1
        } else {
            ivBrake.isSelected = false
            ConstantData.sBackOLEDStatus[ConstantData.sBackOLEDBreak] = 0
        }
        if (cmdPayload[0].toInt() and CMDOLEDBase.Position.toInt() != 0) {
            ivPosition.isSelected = true
            ConstantData.sBackOLEDStatus[ConstantData.sBackOLEDPosition] = 1
        } else {
            ivPosition.isSelected = false
            ConstantData.sBackOLEDStatus[ConstantData.sBackOLEDPosition] = 0
        }
        if (cmdPayload[0].toInt() and CMDOLEDBase.TurnLeft.toInt() != 0) {
            ivTurnLeft.isSelected = true
            ConstantData.sBackOLEDStatus[ConstantData.sBackOLEDTurnLeft] = 1
        } else {
            ivTurnLeft.isSelected = false
            ConstantData.sBackOLEDStatus[ConstantData.sBackOLEDTurnLeft] = 0
        }
        if (cmdPayload[0].toInt() and CMDOLEDBase.TurnRight.toInt() != 0) {
            ivTurnRight.isSelected = true
            ConstantData.sBackOLEDStatus[ConstantData.sBackOLEDTurnRight] = 1
        } else {
            ivTurnRight.isSelected = false
            ConstantData.sBackOLEDStatus[ConstantData.sBackOLEDTurnRight] = 0
        }
        if (cmdPayload[0].toInt() and CMDOLEDBase.AutoRun1.toInt() != 0) {
            ivAuto1.isSelected = true
            ConstantData.sBackOLEDStatus[ConstantData.sBackOLEDAuto1] = 1
        } else {
            ivAuto1.isSelected = false
            ConstantData.sBackOLEDStatus[ConstantData.sBackOLEDAuto1] = 0
        }
        if (cmdPayload[0].toInt() and CMDOLEDBase.AutoRun2.toInt() != 0) {
            ivAuto2.isSelected = true
            ConstantData.sBackOLEDStatus[ConstantData.sBackOLEDAuto2] = 1
        } else {
            ivAuto2.isSelected = false
            ConstantData.sBackOLEDStatus[ConstantData.sBackOLEDAuto2] = 0
        }
        if (cmdPayload[0].toInt() and CMDOLEDBase.AutoRun3.toInt() != 0) {
            ivAuto3.isSelected = true
            ConstantData.sBackOLEDStatus[ConstantData.sBackOLEDAuto3] = 1
        } else {
            ivAuto3.isSelected = false
            ConstantData.sBackOLEDStatus[ConstantData.sBackOLEDAuto3] = 0
        }
        if (CMDOLEDBase.stopAll) {
            ivStop.isSelected = true
            ConstantData.sBackOLEDStatus[ConstantData.sBackOLEDStopOLED] = 1
        } else {
            ivStop.isSelected = false
            ConstantData.sBackOLEDStatus[ConstantData.sBackOLEDStopOLED] = 0
        }
    }

    private var isPlaying = false
    private var soundsIndex = 0
    var handler = Handler()
    private fun playOrPause() {
        if (!isPlaying) {
            mediaPlayer.stop()
            val (_, second, third) = soundsList[soundsIndex] // todo
            playMusic(second, third)
        } else {
            mediaPlayer.stop()
        }
    }

    private fun doWhenStartPlaying() {
        mView.post { ivPlayOrPause.setImageResource(R.mipmap.ic_stop) }
    }

    private fun doWhenStopPlaying() {
        isPlaying = false
        if (view != null) {
            mView.post { ivPlayOrPause.setImageResource(R.mipmap.ic_play) }
        }
    }

    private fun playNext() {
        if (isPlaying) {
            soundsIndex++
            if (soundsIndex >= soundsList.size) {
                soundsIndex = 0
            }
            mediaPlayer.stop()
            val (_, second, third) = soundsList[soundsIndex] // todo
            playMusic(second, third)
        }
    }

    private fun playPrevious() {
        if (isPlaying) {
            soundsIndex--
            if (soundsIndex < 0) {
                soundsIndex = soundsList.size - 1
            }
            mediaPlayer.stop()
            val (_, second, third) = soundsList[soundsIndex] // todo
            playMusic(second, third)
        }
    }

    private inner class SendSoundsInfoRegular internal constructor(private val soundsInfo: SoundsInfo) :
        Runnable {
        private var infoIndex = 0
        override fun run() {
            // Send command
            Log.d(
                TAG, "longkai22 frequency " + soundsInfo.frequency[infoIndex] +
                        " amptitude " + soundsInfo.amplitude[infoIndex]
            )
            ServiceManager.getInstance().sendCommandToCar(
                CMDSoundsInfo(
                    soundsInfo.frequency[infoIndex],
                    soundsInfo.amplitude[infoIndex]
                ),
                CommandListenerAdapter<CMDSoundsInfo.Response>()
            )
            infoIndex++
            if (infoIndex < soundsInfo.frequency.size && infoIndex < soundsInfo.amplitude.size) {
                handler.postDelayed(this, 500)
            }
        }
    }

    /**
     * protected static final byte TurnLeft = (byte)0x10;
     * protected static final byte TurnRight = (byte)0x08;
     * protected static final byte Position = (byte)0x04;
     * protected static final byte Brake    = (byte)0x02;
     * protected static final byte Reversing = (byte)0x01;
     *
     * protected static final byte AutoRun1 = (byte)0x20;
     * protected static final byte AutoRun2 = (byte)0x40;
     * protected static final byte AutoRun3 = (byte)0x80;
     *
     * @return
     */
    private fun updateStateByCMD(): OLED2Controller.OLEDState {
        val cmdPayload = CMDOLEDBase.getPayload()
        return OLED2Controller.OLEDState(
            cmdPayload[0].toInt() and CMDOLEDBase.Reversing.toInt() != 0,
            cmdPayload[0].toInt() and CMDOLEDBase.Brake.toInt() != 0,
            cmdPayload[0].toInt() and CMDOLEDBase.Position.toInt() != 0,
            cmdPayload[0].toInt() and CMDOLEDBase.TurnLeft.toInt() != 0,
            cmdPayload[0].toInt() and CMDOLEDBase.TurnRight.toInt() != 0
        )
    }

    companion object {
        private val TAG = CarBackOLED2Fragment::class.java.simpleName
        private const val SAMPLES_COUNT_PER_500_MS = 10
    }
}
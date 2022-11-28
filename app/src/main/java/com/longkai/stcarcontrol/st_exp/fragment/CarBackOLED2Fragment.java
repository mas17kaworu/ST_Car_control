package com.longkai.stcarcontrol.st_exp.fragment;

import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.longkai.stcarcontrol.st_exp.ConstantData;
import com.longkai.stcarcontrol.st_exp.R;
import com.longkai.stcarcontrol.st_exp.Utils.FileUtils;
import com.longkai.stcarcontrol.st_exp.Utils.FileUtils10;
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDOLEDBackList.CMDOLEDAuto1;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDOLEDBackList.CMDOLEDAuto2;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDOLEDBackList.CMDOLEDAuto3;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDOLEDBackList.CMDOLEDBase;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDOLEDBackList.CMDOLEDBrake;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDOLEDBackList.CMDOLEDPosition;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDOLEDBackList.CMDOLEDReversing;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDOLEDBackList.CMDOLEDStopAll;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDOLEDBackList.CMDOLEDTurnLeft;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDOLEDBackList.CMDOLEDTurnRight;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDSound.CMDSoundsInfo;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter;
import com.longkai.stcarcontrol.st_exp.customView.oled2.OLED2Controller;
import com.longkai.stcarcontrol.st_exp.model.SoundsInfo;
import com.longkai.stcarcontrol.st_exp.music.AudioVisualConverter;
import com.longkai.stcarcontrol.st_exp.music.MyMediaPlayer;

import static com.longkai.stcarcontrol.st_exp.ConstantData.sBackOLEDAuto1;
import static com.longkai.stcarcontrol.st_exp.ConstantData.sBackOLEDAuto2;
import static com.longkai.stcarcontrol.st_exp.ConstantData.sBackOLEDAuto3;
import static com.longkai.stcarcontrol.st_exp.ConstantData.sBackOLEDBreak;
import static com.longkai.stcarcontrol.st_exp.ConstantData.sBackOLEDPosition;
import static com.longkai.stcarcontrol.st_exp.ConstantData.sBackOLEDReverse;
import static com.longkai.stcarcontrol.st_exp.ConstantData.sBackOLEDStopOLED;
import static com.longkai.stcarcontrol.st_exp.ConstantData.sBackOLEDTurnLeft;
import static com.longkai.stcarcontrol.st_exp.ConstantData.sBackOLEDTurnRight;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import kotlin.Pair;
import kotlin.Triple;

public class CarBackOLED2Fragment extends Fragment implements View.OnClickListener {
    private static final String TAG = CarBackOLED2Fragment.class.getSimpleName();

    private static int SAMPLES_COUNT_PER_500_MS = 10;

    private View mView;

    private ImageView ivReversing, ivBrake, ivPosition, ivTurnLeft, ivTurnRight, ivAuto1, ivAuto2,
        ivAuto3, ivPlayOrPause, ivPlayNext, ivPlayPrevious, ivStop;

    private OLED2Controller oledController;

    private MyMediaPlayer mediaPlayer;

    private Boolean isVisualizerInit = false;

    private AudioVisualConverter audioVisualConverter = new AudioVisualConverter();

    private Boolean stopSendCMD = false;

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_car_back_oled2, container, false);
        ivReversing = (ImageView) mView.findViewById(R.id.btn_back_oled_reversing);
        ivReversing.setOnClickListener(this);
        ivBrake = (ImageView) mView.findViewById(R.id.btn_back_oled_break);
        ivBrake.setOnClickListener(this);
        ivPosition = (ImageView) mView.findViewById(R.id.btn_back_oled_position);
        ivPosition.setOnClickListener(this);
        ivTurnLeft = (ImageView) mView.findViewById(R.id.btn_back_oled_left);
        ivTurnLeft.setOnClickListener(this);
        ivTurnRight = (ImageView) mView.findViewById(R.id.btn_back_oled_right);
        ivTurnRight.setOnClickListener(this);
        ivAuto1 = (ImageView) mView.findViewById(R.id.btn_back_oled_a1);
        ivAuto1.setOnClickListener(this);
        ivAuto2 = (ImageView) mView.findViewById(R.id.btn_back_oled_a2);
        ivAuto2.setOnClickListener(this);
        ivAuto3 = (ImageView) mView.findViewById(R.id.btn_back_oled_a3);
        ivAuto3.setOnClickListener(this);
        ivPlayOrPause = mView.findViewById(R.id.btnPlayAudio);
        ivPlayOrPause.setOnClickListener(this);
        ivPlayPrevious = mView.findViewById(R.id.btnPlayPrevious);
        ivPlayPrevious.setOnClickListener(this);
        ivPlayNext = mView.findViewById(R.id.btnPlayNext);
        ivPlayNext.setOnClickListener(this);
        ivStop = (ImageView) mView.findViewById(R.id.btn_back_oled_stop);
        ivStop.setOnClickListener(this);
        mediaPlayer = MyMediaPlayer.getInstance(this.getContext());

        return mView;
    }

    private List<Triple<String, Uri, SoundsInfo>> soundsList;
    @Override public void onStart() {
        super.onStart();
        oledController = new OLED2Controller(
                this,
                (ImageView)mView.findViewById(R.id.iv_oled_reverse),
                (ImageView)mView.findViewById(R.id.iv_oled_break),
                (ImageView)mView.findViewById(R.id.iv_oled_position)
        );

        refreshUI();
        soundsList = FileUtils10.INSTANCE.getFilesUnderDownloadST(getActivity());
        try {
            if (soundsList.isEmpty()) soundsList = new LinkedList<>();
            soundsList.add(0, new Triple<String, Uri, SoundsInfo>(
                "st01-default",
                FileUtils.getResUri(R.raw.st01_wav, this.getContext()),
                FileUtils10.INSTANCE.readSoundsInfoFile(
                    FileUtils.getResUri(R.raw.st01_json, this.getContext()), this.getContext()
                )
            ));
        } catch (Exception e) {
            System.out.println(e);
        }

        //Log.i(TAG, )
    }
    private SendSoundsInfoRegular sendCMDTask;
    private void playMusic(Uri uri, SoundsInfo soundsInfo) {
        isVisualizerInit = false;
        mediaPlayer = MyMediaPlayer.getInstance(this.getContext());
        mediaPlayer.play(uri);
        mediaPlayer.setPlayStateListener(new MyMediaPlayer.PlayStateListener() {
            @Override
            public void onStateChange(MyMediaPlayer.PlayState state) {
                if (state == MyMediaPlayer.PlayState.STATE_PLAYING) {
                    isPlaying = true;
                    sendCMDTask = new SendSoundsInfoRegular(soundsInfo);
                    handler.post(sendCMDTask);
                    doWhenStartPlaying();
                    //initVisualizer();
                } else if (state == MyMediaPlayer.PlayState.STATE_PAUSE || state == MyMediaPlayer.PlayState.STATE_IDLE) {
                    handler.removeCallbacks(sendCMDTask);
                    doWhenStopPlaying();
                }
            }
        });
    }

    private Visualizer visualizer;
    private int sampleIndex=0;
    private int sum = 0;
    private Visualizer.OnDataCaptureListener dataCaptureListener = new Visualizer.OnDataCaptureListener() {
        @Override
        public void onWaveFormDataCapture(Visualizer visualizer, final byte[] waveform, int samplingRate) {
            //Log.d(TAG, "waveform samplingRate " + samplingRate + " waveform length " + waveform.length);
            //audioView.post(new Runnable() {
            //    @Override
            //    public void run() {
            //        audioView.setWaveData(waveform);
            //    }
            //});

        }

        @Override
        public void onFftDataCapture(Visualizer visualizer, final byte[] fft, int samplingRate) {
            //Log.d(TAG, "onFftDataCapture samplingRate" + samplingRate + " FftData " + fft.length);
            //audioView2.post(new Runnable() {
            //    @Override
            //    public void run() {
            //        audioView2.setWaveData(fft);
            Log.d(TAG, String.format(Locale.getDefault(), "当前分贝: %s db", audioVisualConverter.getVoiceSize(fft)));
            //sum += audioVisualConverter.getVoiceSizeGoogle(fft);
            //if (sampleIndex < SAMPLES_COUNT_PER_500_MS) {
            //    sampleIndex ++;
            //} else {
            //    Log.d(TAG, String.format(Locale.getDefault(), "当前分贝: %s db", sum / SAMPLES_COUNT_PER_500_MS));
            //    sum = 0;
            //    sampleIndex = 0;
            //}
        }
    };



    @Override public void onDestroy() {
        super.onDestroy();
        super.onDestroy();
        if (visualizer != null) {
            visualizer.release();
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    private void initVisualizer() {
        if (isVisualizerInit) {
            return;
        }
        isVisualizerInit = true;
        //audioVisualConverter = new AudioVisualConverter();
        Log.d(TAG, "initVisualizer()");
        try {
            int mediaPlayerId = mediaPlayer.getMediaPlayerId();
            Log.i(TAG, "mediaPlayerId: " + mediaPlayerId);
            if (visualizer != null) {
                visualizer.release();
            }
            visualizer = new Visualizer(mediaPlayerId);

            int captureSize = 128;
            int captureRate = 2000 * SAMPLES_COUNT_PER_500_MS; //2000 -- 2hz
            Log.d(TAG, "精度: " + captureSize);
            Log.d(TAG, "刷新频率: " + captureRate);

            visualizer.setCaptureSize(captureSize);
            visualizer.setDataCaptureListener(dataCaptureListener, captureRate, true, true);
            visualizer.setScalingMode(Visualizer.SCALING_MODE_NORMALIZED);
            visualizer.setEnabled(true);
        } catch (Exception e) {
            Log.e(TAG, "请检查录音权限 " + e);
            isVisualizerInit = false;
        }
    }

    @Override public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_back_oled_reversing:
                clickBtn(sBackOLEDReverse, ivReversing, new CMDOLEDReversing());
                //ServiceManager.getInstance().sendCommandToCar(new CMDOLEDReversing(), new CommandListenerAdapter());
                break;
            case R.id.btn_back_oled_break:
                clickBtn(sBackOLEDBreak, ivBrake, new CMDOLEDBrake());
                //ServiceManager.getInstance().sendCommandToCar(new CMDOLEDBrake(), new CommandListenerAdapter());
                break;
            case R.id.btn_back_oled_position:
                clickBtn(sBackOLEDPosition, ivPosition, new CMDOLEDPosition());
                //ServiceManager.getInstance().sendCommandToCar(new CMDOLEDPosition(), new CommandListenerAdapter());
                break;
            case R.id.btn_back_oled_left:
                clickBtn(sBackOLEDTurnLeft, ivTurnLeft, new CMDOLEDTurnLeft());
                //ServiceManager.getInstance().sendCommandToCar(new CMDOLEDTurnLeft(), new CommandListenerAdapter());
                break;
            case R.id.btn_back_oled_right:
                clickBtn(sBackOLEDTurnRight, ivTurnRight, new CMDOLEDTurnRight());
                //ServiceManager.getInstance().sendCommandToCar(new CMDOLEDTurnRight(), new CommandListenerAdapter());
                break;
            case R.id.btn_back_oled_a1:
                clickBtn(sBackOLEDAuto1, ivAuto1, new CMDOLEDAuto1());
                //ServiceManager.getInstance().sendCommandToCar(new CMDOLEDAuto1(), new CommandListenerAdapter());
                break;
            case R.id.btn_back_oled_a2:
                clickBtn(sBackOLEDAuto2, ivAuto2, new CMDOLEDAuto2());
                //ServiceManager.getInstance().sendCommandToCar(new CMDOLEDAuto2(), new CommandListenerAdapter());
                break;
            case R.id.btn_back_oled_a3:
                clickBtn(sBackOLEDAuto3, ivAuto3, new CMDOLEDAuto3());
                //ServiceManager.getInstance().sendCommandToCar(new CMDOLEDAuto3(), new CommandListenerAdapter());
                break;
            case R.id.btnPlayAudio:
                playOrPause();
                break;
            case R.id.btnPlayNext:
                playNext();
                break;
            case R.id.btnPlayPrevious:
                playPrevious();
                break;
            case R.id.btn_back_oled_stop:
                clickBtn(sBackOLEDStopOLED, ivStop, new CMDOLEDStopAll());
                break;
        }

        refreshUI();
    }

    private void clickBtn(int index, ImageView view, BaseCommand command) {
        if (!(command instanceof CMDOLEDStopAll) && CMDOLEDBase.stopAll) {
            return;
        }

        if (ConstantData.sBackOLEDStatus[index] == 1) {
            //ConstantData.sBackOLEDStatus[index] = 0;
            command.turnOff();
            //view.setSelected(false);
        } else {
            //ConstantData.sBackOLEDStatus[index] = 1;
            command.turnOn();
            //playMusic(FileUtils.getResUri(R.raw.st01, this.getContext()));
            //view.setSelected(true);
        }
        ServiceManager.getInstance().sendCommandToCar(command, new CommandListenerAdapter());
    }

    private void refreshUI(){
        oledController.updateState(updateStateByCMD());

        byte[] cmdPayload = CMDOLEDBase.getPayload();
        if ((cmdPayload[0] & CMDOLEDBase.Reversing) != 0) {
            ivReversing.setSelected(true);
            ConstantData.sBackOLEDStatus[sBackOLEDReverse] = 1;
        } else{
            ivReversing.setSelected(false);
            ConstantData.sBackOLEDStatus[sBackOLEDReverse] = 0;
        }


        if ((cmdPayload[0] & CMDOLEDBase.Brake) != 0) {
            ivBrake.setSelected(true);
            ConstantData.sBackOLEDStatus[sBackOLEDBreak] = 1;
        } else {
            ivBrake.setSelected(false);
            ConstantData.sBackOLEDStatus[sBackOLEDBreak] = 0;
        }

        if((cmdPayload[0] & CMDOLEDBase.Position) !=0) {
            ivPosition.setSelected(true);
            ConstantData.sBackOLEDStatus[sBackOLEDPosition] = 1;
        } else {
            ivPosition.setSelected(false);
            ConstantData.sBackOLEDStatus[sBackOLEDPosition] = 0;
        }

        if((cmdPayload[0] & CMDOLEDBase.TurnLeft) != 0) {
            ivTurnLeft.setSelected(true);
            ConstantData.sBackOLEDStatus[sBackOLEDTurnLeft] = 1;
        } else {
            ivTurnLeft.setSelected(false);
            ConstantData.sBackOLEDStatus[sBackOLEDTurnLeft] = 0;
        }

        if ((cmdPayload[0] & CMDOLEDBase.TurnRight) != 0) {
            ivTurnRight.setSelected(true);
            ConstantData.sBackOLEDStatus[sBackOLEDTurnRight] = 1;
        } else {
            ivTurnRight.setSelected(false);
            ConstantData.sBackOLEDStatus[sBackOLEDTurnRight] = 0;
        }

        if ((cmdPayload[0] & CMDOLEDBase.AutoRun1) != 0) {
            ivAuto1.setSelected(true);
            ConstantData.sBackOLEDStatus[sBackOLEDAuto1] = 1;
        } else {
            ivAuto1.setSelected(false);
            ConstantData.sBackOLEDStatus[sBackOLEDAuto1] = 0;
        }

        if ((cmdPayload[0] & CMDOLEDBase.AutoRun2) != 0) {
            ivAuto2.setSelected(true);
            ConstantData.sBackOLEDStatus[sBackOLEDAuto2] = 1;
        } else {
            ivAuto2.setSelected(false);
            ConstantData.sBackOLEDStatus[sBackOLEDAuto2] = 0;
        }

        if ((cmdPayload[0] & CMDOLEDBase.AutoRun3) != 0) {
            ivAuto3.setSelected(true);
            ConstantData.sBackOLEDStatus[sBackOLEDAuto3] = 1;
        } else {
            ivAuto3.setSelected(false);
            ConstantData.sBackOLEDStatus[sBackOLEDAuto3] = 0;
        }

        if (CMDOLEDBase.stopAll) {
            ivStop.setSelected(true);
            ConstantData.sBackOLEDStatus[sBackOLEDStopOLED] = 1;
        } else {
            ivStop.setSelected(false);
            ConstantData.sBackOLEDStatus[sBackOLEDStopOLED] = 0;
        }
    }
    private Boolean isPlaying = false;
    private int soundsIndex = 0;
    Handler handler = new Handler();

    private void playOrPause() {
        if (!isPlaying) {
            mediaPlayer.stop();
            Triple<String, Uri, SoundsInfo> result = soundsList.get(soundsIndex); // todo
            playMusic(result.getSecond(), result.getThird());
        } else {
            mediaPlayer.stop();
        }
    }

    private void doWhenStartPlaying() {
        getView().post(() -> ivPlayOrPause.setImageResource(R.mipmap.ic_stop));
    }

    private void doWhenStopPlaying() {
        isPlaying = false;
        if (getView() != null) {
            getView().post(() -> ivPlayOrPause.setImageResource(R.mipmap.ic_play));
        }

    }

    private void playNext() {
        if (isPlaying) {
            soundsIndex++;
            if (soundsIndex >= soundsList.size()) {
                soundsIndex = 0;
            }
            mediaPlayer.stop();
            Triple<String, Uri, SoundsInfo> result = soundsList.get(soundsIndex); // todo
            playMusic(result.getSecond(), result.getThird());
        }
    }

    private void playPrevious() {
        if (isPlaying) {
            soundsIndex--;
            if (soundsIndex < 0) {
                soundsIndex = soundsList.size() - 1;
            }
            mediaPlayer.stop();
            Triple<String, Uri, SoundsInfo> result = soundsList.get(soundsIndex); // todo
            playMusic(result.getSecond(), result.getThird());
        }
    }

    private final CommandListenerAdapter doNothing = new CommandListenerAdapter();
    private class SendSoundsInfoRegular implements Runnable {
        SendSoundsInfoRegular(SoundsInfo soundsInfo) {
            this.soundsInfo = soundsInfo;
        }
        private int infoIndex = 0;
        private SoundsInfo soundsInfo;

        @Override public void run() {
            // Send command
            Log.d(TAG, "longkai22 frequency " + soundsInfo.component1().get(infoIndex) +
                " amptitude " + soundsInfo.component2().get(infoIndex));
            ServiceManager.getInstance().sendCommandToCar(new CMDSoundsInfo(
                soundsInfo.component1().get(infoIndex),
                soundsInfo.component2().get(infoIndex)
            ), doNothing);

            infoIndex++;
            if (infoIndex < soundsInfo.component1().size() && infoIndex < soundsInfo.component2().size()) {
                handler.postDelayed(this, 500);
            }

        }
    }

    /**
     *     protected static final byte TurnLeft = (byte)0x10;
     *     protected static final byte TurnRight = (byte)0x08;
     *     protected static final byte Position = (byte)0x04;
     *     protected static final byte Brake    = (byte)0x02;
     *     protected static final byte Reversing = (byte)0x01;
     *
     *     protected static final byte AutoRun1 = (byte)0x20;
     *     protected static final byte AutoRun2 = (byte)0x40;
     *     protected static final byte AutoRun3 = (byte)0x80;
     *
     * @return
     */
    private OLED2Controller.OLEDState updateStateByCMD(){
        byte[] cmdPayload = CMDOLEDBase.getPayload();
        return new OLED2Controller.OLEDState(
                (cmdPayload[0] & CMDOLEDBase.Reversing) != 0,
                (cmdPayload[0] & CMDOLEDBase.Brake) != 0,
                (cmdPayload[0] & CMDOLEDBase.Position) != 0,
                (cmdPayload[0] & CMDOLEDBase.TurnLeft) != 0,
                (cmdPayload[0] & CMDOLEDBase.TurnRight) != 0
        );
    }
}

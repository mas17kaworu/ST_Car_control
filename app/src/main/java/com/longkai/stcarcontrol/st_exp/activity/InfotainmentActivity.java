package com.longkai.stcarcontrol.st_exp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.longkai.stcarcontrol.st_exp.R;
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDInfoteinmentList.CMDInfoteinmentEngineVoice;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDInfoteinmentList.CMDInfoteinmentVoiceVolume;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDInfoteinmentList.CMDNewInfoteinmentEngineVoice;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/8/4.
 */

public class InfotainmentActivity extends BaseActivity implements View.OnClickListener {

    private ImageView ivSoundA, ivSoundB, ivSoundC, ivSoundD;
    private SeekBar sbVolume;
    private List<ImageView> soundBtnList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_infotainment);

        ivSoundA = (ImageView) findViewById(R.id.iv_infot_engine_s_a);
        ivSoundA.setOnClickListener(this);
        ivSoundB = (ImageView) findViewById(R.id.iv_infot_engine_s_b);
        ivSoundB.setOnClickListener(this);
        ivSoundC = (ImageView) findViewById(R.id.iv_infot_engine_s_c);
        ivSoundC.setOnClickListener(this);
        ivSoundD = (ImageView) findViewById(R.id.iv_infot_engine_s_d);
        ivSoundD.setOnClickListener(this);
        soundBtnList.add(ivSoundA);
        soundBtnList.add(ivSoundB);
        soundBtnList.add(ivSoundC);
        soundBtnList.add(ivSoundD);

        sbVolume = (SeekBar) findViewById(R.id.seekBar_infot_sound);
        sbVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                CMDInfoteinmentVoiceVolume cmd = new CMDInfoteinmentVoiceVolume();
                cmd.setVolume(progress);
                ServiceManager.getInstance().sendCommandToCar(cmd, new CommandListenerAdapter());

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        CMDNewInfoteinmentEngineVoice command = new CMDNewInfoteinmentEngineVoice();
        switch (v.getId()){
            case R.id.iv_infot_engine_s_a:
                command.changeVoiceTo(1);
                clickSoundBtn((ImageView)v);
                break;
            case R.id.iv_infot_engine_s_b:
                command.changeVoiceTo(2);
                clickSoundBtn((ImageView)v);
                break;
            case R.id.iv_infot_engine_s_c:
                command.changeVoiceTo(3);
                clickSoundBtn((ImageView)v);
                break;
            case R.id.iv_infot_engine_s_d:
                command.changeVoiceTo(4);
                clickSoundBtn((ImageView)v);
                break;
        }
        ServiceManager.getInstance().sendCommandToCar(command, new CommandListenerAdapter());

    }

    private void clickSoundBtn(ImageView targetIV){
        for (ImageView  iv: soundBtnList) {
            if (iv == targetIV) {
                iv.setSelected(true);
            } else {
                iv.setSelected(false);
            }
        }
    }
}

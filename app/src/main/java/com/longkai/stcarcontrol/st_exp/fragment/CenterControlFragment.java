package com.longkai.stcarcontrol.st_exp.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.longkai.stcarcontrol.st_exp.ConstantData;
import com.longkai.stcarcontrol.st_exp.R;
import com.longkai.stcarcontrol.st_exp.activity.MainActivity;
import com.longkai.stcarcontrol.st_exp.communication.MessageReceivedListener;
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDControlCenterList.CMDControlCenterCentralUnlockOff;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDControlCenterList.CMDControlCenterCentralUnlockOn;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDControlCenterList.CMDControlCenterCentrallockOff;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDControlCenterList.CMDControlCenterCentrallockOn;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDControlCenterList.CMDControlCenterDomeLightOff;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDControlCenterList.CMDControlCenterDomeLightOn;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDControlCenterList.CMDControlCenterFuelTankUnlockOff;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDControlCenterList.CMDControlCenterFuelTankUnlockOn;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDControlCenterList.CMDControlCenterWiperFastOff;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDControlCenterList.CMDControlCenterWiperFastOn;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDControlCenterList.CMDControlCenterWiperSlowOff;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDControlCenterList.CMDControlCenterWiperSlowOn;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDHVACList.CMDHVACBlowerCodeSet;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDHVACList.CMDHVACFlapCodeSet;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter;
import com.longkai.stcarcontrol.st_exp.customView.AirconditionDiscView;
import com.longkai.stcarcontrol.st_exp.customView.CoverWindView;

/**
 * Created by Administrator on 2017/8/11.
 */

public class CenterControlFragment extends Fragment implements View.OnClickListener{
    private View mView;
    private AirconditionDiscView avWindPower, avWindAngle;
    private CoverWindView mCoverWindView;
    private AlphaAnimation alphaAnimation;

    private ImageView ivWiperState1,ivWiperState2;
    private ImageView ivCenterLock, ivCenterUnlock;
    private ImageView ivFuelTank, ivDomeLight;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_center_control, container, false);
        mCoverWindView = (CoverWindView) mView.findViewById(R.id.cover_wind_view);
        //// TODO: 2017/11/15 send command
        mView.findViewById(R.id.iv_center_control_yugua_gray).setOnClickListener(this);
        ivWiperState1 = (ImageView) mView.findViewById(R.id.iv_center_control_wiper_stage_1);
        ivWiperState2 = (ImageView) mView.findViewById(R.id.iv_center_control_wiper_stage_2);
        ivCenterLock = (ImageView) mView.findViewById(R.id.iv_center_control_center_lock);
        ivCenterUnlock = (ImageView) mView.findViewById(R.id.iv_center_control_center_unlock);

        mView.findViewById(R.id.iv_center_control_center_lock).setOnClickListener(this);

        mView.findViewById(R.id.iv_center_control_safe_belt_gray).setOnClickListener(this);

        mView.findViewById(R.id.iv_center_control_sun_shade_gray).setOnClickListener(this);

        ivCenterLock.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        ivCenterLock.setImageResource(R.mipmap.ic_center_control_center_lock_green);
                        ServiceManager.getInstance().sendCommandToCar(new CMDControlCenterCentrallockOn(),
                                new CommandListenerAdapter());
                        break;
                    case MotionEvent.ACTION_UP:
                        ivCenterLock.setImageResource(R.mipmap.ic_center_control_center_lock_gray);
                        ServiceManager.getInstance().sendCommandToCar(new CMDControlCenterCentrallockOff(),
                                new CommandListenerAdapter());
                        break;
                }
                return true;
            }
        });

        ivCenterUnlock.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        ivCenterUnlock.setImageResource(R.mipmap.ic_center_control_center_unlock_green);
                        ServiceManager.getInstance().sendCommandToCar(new CMDControlCenterCentralUnlockOn(),
                                new CommandListenerAdapter());
                        break;
                    case MotionEvent.ACTION_UP:
                        ivCenterUnlock.setImageResource(R.mipmap.ic_center_control_center_unlock_gray);
                        ServiceManager.getInstance().sendCommandToCar(new CMDControlCenterCentralUnlockOff(),
                                new CommandListenerAdapter());
                        break;
                }
                return true;
            }
        });
//        ivCenterLock.setOnClickListener(this);

        ivFuelTank = (ImageView)mView.findViewById(R.id.iv_center_control_youxiang_gray);
        ivFuelTank.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ivFuelTank.setImageResource(R.mipmap.ic_center_control_youxiang_green);
                        ServiceManager.getInstance().sendCommandToCar(new CMDControlCenterFuelTankUnlockOn(),
                                new CommandListenerAdapter());
                        break;
                    case MotionEvent.ACTION_UP:
                        ivFuelTank.setImageResource(R.mipmap.ic_center_control_youxiang_gray);
                        ServiceManager.getInstance().sendCommandToCar(new CMDControlCenterFuelTankUnlockOff(),
                                new CommandListenerAdapter());
                        break;
                }
                return true;
            }
        });

        ivDomeLight = (ImageView) mView.findViewById(R.id.iv_center_control_dome_light);
        ivDomeLight.setOnClickListener(this);
        //....

        avWindAngle = (AirconditionDiscView) mView.findViewById(R.id.aircondition_angle);
        avWindPower = (AirconditionDiscView) mView.findViewById(R.id.aircondition_power);
        avWindAngle.setProgressChangeListener(mAngleProgressChangeListener);
        avWindPower.setProgressChangeListener(mPowerProgressChangeListener);

        alphaAnimation = new AlphaAnimation(1.0f,0.1f);
        alphaAnimation.setDuration(1000);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mCoverWindView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mCoverWindView.setAnimation(alphaAnimation);
        alphaAnimation.setRepeatCount(Animation.ABSOLUTE);
        alphaAnimation.start();

        mView.findViewById(R.id.tv_HVAC_diagram).setOnClickListener(this);

//
        refreshUI();

        return mView;
    }

    private int angleRecord, powerRecord;
    private boolean sendAngleFlag = false, sendPowerFlag = false;
    AirconditionDiscView.ProgressChangeListener mAngleProgressChangeListener = new AirconditionDiscView.ProgressChangeListener(){
        @Override
        public void onProgressChangeListener(int progress) {
            //0~240  -30~30
            mCoverWindView.setAngle(progress *60 / 240 - 30);
            ConstantData.sCenterControlStatus[ConstantData.sCenterControlWindAngle] = progress;
            mCoverWindView.setVisibility(View.VISIBLE);
            setAnimation();
            //0~240 to 0~255
            angleRecord = progress*255/240;
            if (!sendAngleFlag){
                sendAngleFlag = true;
                mHandler.sendEmptyMessageDelayed(1, 500);
            }

        }
    };

    AirconditionDiscView.ProgressChangeListener mPowerProgressChangeListener = new AirconditionDiscView.ProgressChangeListener(){
        @Override
        public void onProgressChangeListener(int progress) {
            //0~240  0.5~1
            float scale = progress / 480.0f + 0.5f;
            mCoverWindView.setPower(scale);
            ConstantData.sCenterControlStatus[ConstantData.sCenterControlWindPower] = progress;
            mCoverWindView.setVisibility(View.VISIBLE);
            setAnimation();
//            0~240 to 0~255
            powerRecord = progress*255/240;
            if (!sendPowerFlag){
                sendPowerFlag = true;
                mHandler.sendEmptyMessageDelayed(2, 500);
            }
        }
    };

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    ServiceManager.getInstance().sendCommandToCar(new CMDHVACFlapCodeSet(angleRecord), new CommandListenerAdapter());
                    sendAngleFlag = false;
                    break;
                case 2:
                    ServiceManager.getInstance().sendCommandToCar(new CMDHVACBlowerCodeSet(powerRecord), new CommandListenerAdapter());
                    sendPowerFlag = false;
                    break;
            }
        }
    };

    private void refreshUI(){
        avWindAngle.setProgress(ConstantData.sCenterControlStatus[ConstantData.sCenterControlWindAngle]);
        avWindPower.setProgress(ConstantData.sCenterControlStatus[ConstantData.sCenterControlWindPower]);
        mCoverWindView.setAngle(avWindAngle.getProgress() *60 / 240 - 30);
        float scale = avWindPower.getProgress() / 480.0f + 0.5f;
        mCoverWindView.setPower(scale);

        if (ConstantData.sCenterControlStatus[ConstantData.sCenterControlWiper] == 0) {
            ivWiperState1.setImageResource(R.mipmap.ic_seat_stage_gray);
            ivWiperState2.setImageResource(R.mipmap.ic_seat_stage_gray);
        } else if (ConstantData.sCenterControlStatus[ConstantData.sCenterControlWiper] == 1){
            ivWiperState1.setImageResource(R.mipmap.ic_seat_stage_green);
            ivWiperState2.setImageResource(R.mipmap.ic_seat_stage_gray);
            ConstantData.sCenterControlStatus[ConstantData.sCenterControlWiper] = 2;
        } else if (ConstantData.sCenterControlStatus[ConstantData.sCenterControlWiper] == 2){
            ivWiperState1.setImageResource(R.mipmap.ic_seat_stage_green);
            ivWiperState2.setImageResource(R.mipmap.ic_seat_stage_green);
        }

        if (ConstantData.sCenterControlStatus[ConstantData.sCenterControlDomeLight] == 0){
            ivDomeLight.setImageResource(R.mipmap.ic_center_control_doom_light_gray);
        } else if (ConstantData.sCenterControlStatus[ConstantData.sCenterControlDomeLight] == 1){
            ivDomeLight.setImageResource(R.mipmap.ic_center_control_doom_light_green);

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_center_control_yugua_gray:
                if (ConstantData.sCenterControlStatus[ConstantData.sCenterControlWiper] == 0) {
                    ServiceManager.getInstance().sendCommandToCar(new CMDControlCenterWiperSlowOn(), new CommandListenerAdapter());
                    ServiceManager.getInstance().sendCommandToCar(new CMDControlCenterWiperFastOff(), new CommandListenerAdapter());
                    ivWiperState1.setImageResource(R.mipmap.ic_seat_stage_green);
                    ivWiperState2.setImageResource(R.mipmap.ic_seat_stage_gray);
                    ConstantData.sCenterControlStatus[ConstantData.sCenterControlWiper] = 1;
                } else if (ConstantData.sCenterControlStatus[ConstantData.sCenterControlWiper] == 1){
                    ServiceManager.getInstance().sendCommandToCar(new CMDControlCenterWiperSlowOff(), new CommandListenerAdapter());
                    ServiceManager.getInstance().sendCommandToCar(new CMDControlCenterWiperFastOn(), new CommandListenerAdapter());
                    ivWiperState1.setImageResource(R.mipmap.ic_seat_stage_green);
                    ivWiperState2.setImageResource(R.mipmap.ic_seat_stage_green);
                    ConstantData.sCenterControlStatus[ConstantData.sCenterControlWiper] = 2;
                } else if (ConstantData.sCenterControlStatus[ConstantData.sCenterControlWiper] == 2){
                    ServiceManager.getInstance().sendCommandToCar(new CMDControlCenterWiperSlowOff(), new CommandListenerAdapter());
                    ServiceManager.getInstance().sendCommandToCar(new CMDControlCenterWiperFastOff(), new CommandListenerAdapter());
                    ivWiperState1.setImageResource(R.mipmap.ic_seat_stage_gray);
                    ivWiperState2.setImageResource(R.mipmap.ic_seat_stage_gray);
                    ConstantData.sCenterControlStatus[ConstantData.sCenterControlWiper] = 0;
                }
                break;

            case R.id.tv_HVAC_diagram:
                ((MainActivity)getActivity()).showDiagram(ConstantData.HVAC_DIAGRAM);
                break;
            /*case R.id.iv_center_control_center_lock:
                if (ConstantData.sCenterControlStatus[ConstantData.sCenterControlCentralLock]==0) {
                    ivCenterLock.setImageResource(R.mipmap.ic_center_control_center_lock_green);
                    ConstantData.sCenterControlStatus[ConstantData.sCenterControlCentralLock]=1;
//                    ServiceManager.getInstance().sendCommandToCar(new );
                }else {
                    ConstantData.sCenterControlStatus[ConstantData.sCenterControlCentralLock]=0;
                    ivCenterLock.setImageResource(R.mipmap.ic_center_control_center_lock_gray);
                }
                break;*/
            case R.id.iv_center_control_dome_light:
                if (ConstantData.sCenterControlStatus[ConstantData.sCenterControlDomeLight]==0) {
                    ivDomeLight.setImageResource(R.mipmap.ic_center_control_doom_light_green);
                    ConstantData.sCenterControlStatus[ConstantData.sCenterControlDomeLight]=1;
                    ServiceManager.getInstance().sendCommandToCar(new CMDControlCenterDomeLightOn(),
                            new CommandListenerAdapter());
                }else {
                    ConstantData.sCenterControlStatus[ConstantData.sCenterControlDomeLight]=0;
                    ivDomeLight.setImageResource(R.mipmap.ic_center_control_doom_light_gray);
                    ServiceManager.getInstance().sendCommandToCar(new CMDControlCenterDomeLightOff(),
                            new CommandListenerAdapter());
                }
                break;

        }
    }
    private void setAnimation(){
        alphaAnimation = new AlphaAnimation(1.0f,0.1f);
        alphaAnimation.setDuration(1000);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mCoverWindView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mCoverWindView.setAnimation(alphaAnimation);
        alphaAnimation.setRepeatCount(Animation.ABSOLUTE);
        alphaAnimation.start();
    }

}

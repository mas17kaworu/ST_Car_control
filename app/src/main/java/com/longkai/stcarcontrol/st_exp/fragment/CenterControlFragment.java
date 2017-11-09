package com.longkai.stcarcontrol.st_exp.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.longkai.stcarcontrol.st_exp.ConstantData;
import com.longkai.stcarcontrol.st_exp.R;
import com.longkai.stcarcontrol.st_exp.communication.MessageReceivedListener;
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager;
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
    private AirconditionDiscView avWindPower,avWindAngle;
    private CoverWindView mCoverWindView;
    private AlphaAnimation alphaAnimation;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_center_control, container, false);
        mCoverWindView = (CoverWindView) mView.findViewById(R.id.cover_wind_view);

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
//
        refreshUI();

        return mView;
    }

    AirconditionDiscView.ProgressChangeListener mAngleProgressChangeListener = new AirconditionDiscView.ProgressChangeListener(){
        @Override
        public void onProgressChangeListener(int progress) {
            //0~240  -30~30
            mCoverWindView.setAngle(progress *60 / 240 - 30);
            ConstantData.sCenterControlStatus[ConstantData.sCenterControlWindAngle] = progress;
            mCoverWindView.setVisibility(View.VISIBLE);
            setAnimation();
            ServiceManager.getInstance().sendCommandToCar(new CMDHVACFlapCodeSet(progress*255/240), new CommandListenerAdapter());

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
            ServiceManager.getInstance().sendCommandToCar(new CMDHVACBlowerCodeSet(progress*255/240), new CommandListenerAdapter());
        }
    };

    private void refreshUI(){
        avWindAngle.setProgress(ConstantData.sCenterControlStatus[ConstantData.sCenterControlWindAngle]);
        avWindPower.setProgress(ConstantData.sCenterControlStatus[ConstantData.sCenterControlWindPower]);
        mCoverWindView.setAngle(avWindAngle.getProgress() *60 / 240 - 30);
        float scale = avWindPower.getProgress() / 480.0f + 0.5f;
        mCoverWindView.setPower(scale);
    }

    @Override
    public void onClick(View v) {

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

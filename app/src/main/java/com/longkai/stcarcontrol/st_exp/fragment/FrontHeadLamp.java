package com.longkai.stcarcontrol.st_exp.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.longkai.stcarcontrol.st_exp.ConstantData;
import com.longkai.stcarcontrol.st_exp.R;
import com.longkai.stcarcontrol.st_exp.activity.BaseActivity;
import com.longkai.stcarcontrol.st_exp.activity.MainActivity;
import com.longkai.stcarcontrol.st_exp.bluetoothComm.commandList.BTCMDLEDHeadLamp;
import com.longkai.stcarcontrol.st_exp.bluetoothComm.commandList.BaseBTResponse;
import com.longkai.stcarcontrol.st_exp.bluetoothComm.commandList.CommandListenerAdapter;
import com.longkai.stcarcontrol.st_exp.bluetoothComm.old.BTServer;

/**
 * Created by Administrator on 2017/7/10.
 */

public class FrontHeadLamp extends Fragment implements View.OnClickListener{
    private View mView;
    private ImageView ivLampDadeng, ivLampJingguangdeng, ivLampJiaodeng, ivLampRixingdeng, ivLampTurnLeft, ivLampTurnRight;

    private BTServer mBTServer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_front_lamp, container, false);
        mView.findViewById(R.id.iv_lamp_dadeng_click).setOnClickListener(this);
        mView.findViewById(R.id.iv_lamp_jinguangdeng_click).setOnClickListener(this);
        mView.findViewById(R.id.iv_lamp_jiaodeng_click).setOnClickListener(this);
        mView.findViewById(R.id.iv_lamp_rixingdeng_click).setOnClickListener(this);
        mView.findViewById(R.id.iv_lamp_turnleft_click).setOnClickListener(this);
        mView.findViewById(R.id.iv_lamp_turnright_click).setOnClickListener(this);
        mView.findViewById(R.id.iv_lamp_jump).setOnClickListener(this);

        ivLampDadeng = (ImageView) mView.findViewById(R.id.iv_lamp_dadeng_on);
        ivLampJingguangdeng = (ImageView) mView.findViewById(R.id.iv_lamp_jinguangdeng_on);
        ivLampJiaodeng = (ImageView) mView.findViewById(R.id.iv_lamp_jiaodeng_on);
        ivLampRixingdeng = (ImageView) mView.findViewById(R.id.iv_lamp_rixingdeng_on);
        ivLampTurnLeft = (ImageView) mView.findViewById(R.id.iv_lamp_turnleft_on);
        ivLampTurnRight = (ImageView) mView.findViewById(R.id.iv_lamp_turnright_on);
        refreshUI();
        mBTServer = ((BaseActivity)getActivity()).mBtServer;

        return mView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_lamp_dadeng_click:
                if (ConstantData.sLampDadengStatus == 0) {
                    BTCMDLEDHeadLamp.DRLLightOn();
                    mBTServer.sendCommend(new BTCMDLEDHeadLamp(), new CommandListenerAdapter(){
                        @Override
                        public void onSuccess(BaseBTResponse response) {
                            super.onSuccess(response);
                            ConstantData.sLampDadengStatus = 1;
                            ivLampDadeng.setVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    BTCMDLEDHeadLamp.DRLLightOff();
                    mBTServer.sendCommend(new BTCMDLEDHeadLamp(), new CommandListenerAdapter(){
                        @Override
                        public void onSuccess(BaseBTResponse response) {
                            super.onSuccess(response);
                            ConstantData.sLampDadengStatus = 0;
                            ivLampDadeng.setVisibility(View.INVISIBLE);
                        }
                    });
                }
                break;
            case R.id.iv_lamp_jinguangdeng_click:
                if (ConstantData.sLampJinguangdengStatus == 0) {
                    ConstantData.sLampJinguangdengStatus = 1;
                    ivLampJingguangdeng.setVisibility(View.VISIBLE);
                } else {
                    ConstantData.sLampJinguangdengStatus = 0;
                    ivLampJingguangdeng.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.iv_lamp_jiaodeng_click:
                if (ConstantData.sLampJiaodengStatus == 0) {
                    ConstantData.sLampJiaodengStatus = 1;
                    ivLampJiaodeng.setVisibility(View.VISIBLE);
                } else {
                    ConstantData.sLampJiaodengStatus = 0;
                    ivLampJiaodeng.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.iv_lamp_rixingdeng_click:
                if (ConstantData.sLampRixingdengStatus == 0) {
                    ConstantData.sLampRixingdengStatus = 1;
                    ivLampRixingdeng.setVisibility(View.VISIBLE);
                } else {
                    ConstantData.sLampRixingdengStatus = 0;
                    ivLampRixingdeng.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.iv_lamp_turnleft_click:
                if (ConstantData.sLampTurnLeftStatus == 0){
                    ConstantData.sLampTurnLeftStatus = 1;
                    ivLampTurnLeft.setVisibility(View.VISIBLE);
                    setBlink(ivLampTurnLeft);

                    //左右转向灯不要同时闪烁
                    ConstantData.sLampTurnRightStatus = 0;
                    ivLampTurnRight.setAnimation(null);
                    ivLampTurnRight.setVisibility(View.INVISIBLE);

                } else {
                    ConstantData.sLampTurnLeftStatus = 0;
                    ivLampTurnLeft.setAnimation(null);
                    ivLampTurnLeft.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.iv_lamp_turnright_click:
                if (ConstantData.sLampTurnRightStatus == 0){
                    ConstantData.sLampTurnRightStatus = 1;
                    ivLampTurnRight.setVisibility(View.VISIBLE);
                    setBlink(ivLampTurnRight);

                    //左右转向灯不要同时闪烁
                    ConstantData.sLampTurnLeftStatus = 0;
                    ivLampTurnLeft.setAnimation(null);
                    ivLampTurnLeft.setVisibility(View.INVISIBLE);
                } else {
                    ConstantData.sLampTurnRightStatus = 0;
                    ivLampTurnRight.setAnimation(null);
                    ivLampTurnRight.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.iv_lamp_jump:
                ((MainActivity)getActivity()).setSelect(5);
                break;
        }
    }

    private void refreshUI(){
        if (ConstantData.sLampDadengStatus == 1) {
            ivLampDadeng.setVisibility(View.VISIBLE);
        } else {
            ivLampDadeng.setVisibility(View.INVISIBLE);
        }
        if (ConstantData.sLampJinguangdengStatus == 1) {
            ivLampJingguangdeng.setVisibility(View.VISIBLE);
        } else {
            ivLampJingguangdeng.setVisibility(View.INVISIBLE);
        }
        if (ConstantData.sLampJiaodengStatus == 1) {
            ivLampJiaodeng.setVisibility(View.VISIBLE);
        } else {
            ivLampJiaodeng.setVisibility(View.INVISIBLE);
        }
        if (ConstantData.sLampRixingdengStatus == 1) {
            ivLampRixingdeng.setVisibility(View.VISIBLE);
        } else {
            ivLampRixingdeng.setVisibility(View.INVISIBLE);
        }
        if (ConstantData.sLampTurnLeftStatus == 1){
            ivLampTurnLeft.setVisibility(View.VISIBLE);
            setBlink(ivLampTurnLeft);
        } else {
            ivLampTurnLeft.setAnimation(null);
            ivLampTurnLeft.setVisibility(View.INVISIBLE);
        }
        if (ConstantData.sLampTurnRightStatus == 1){
            ivLampTurnRight.setVisibility(View.VISIBLE);
            setBlink(ivLampTurnRight);
        } else {
            ivLampTurnRight.setAnimation(null);
            ivLampTurnRight.setVisibility(View.INVISIBLE);
        }
    }

    private void setBlink(View view){
        //闪烁
        AlphaAnimation alphaAnimation1 = new AlphaAnimation(0.1f, 1.0f);
        alphaAnimation1.setDuration(500);
        alphaAnimation1.setRepeatCount(Animation.INFINITE);
        alphaAnimation1.setRepeatMode(Animation.REVERSE);
        view.setAnimation(alphaAnimation1);
        alphaAnimation1.start();
    }
}

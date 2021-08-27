package com.longkai.stcarcontrol.st_exp.fragment;

import android.os.Handler;
import android.os.Message;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
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
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager;
import com.longkai.stcarcontrol.st_exp.communication.btComm.BTServer;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList.CMDLEDHeadLampDRLLight;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList.CMDLEDHeadLampHBAll;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList.CMDLEDHeadLampLeftCorner;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList.CMDLEDHeadLampLowBeam1;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList.CMDLEDHeadLampLowBeam2;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList.CMDLEDHeadLampPosition;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList.CMDLEDHeadLampRightCorner;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList.CMDLEDHeadLampTurnLeft;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList.CMDLEDHeadLampTurnRight;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter;

import static com.longkai.stcarcontrol.st_exp.ConstantData.sLampJinguangdengStatus;

/**
 * Created by Administrator on 2017/7/10.
 */

public class FrontHeadLamp extends Fragment implements View.OnClickListener{
    private View mView;
    private ImageView ivLampDadeng, ivLampJingguangdeng, ivLampJiaodengRight, ivLampJiaodengLeft, ivLampRixingdeng, ivLampTurnLeft, ivLampTurnRight;

    private BTServer mBTServer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_front_lamp, container, false);
        mView.findViewById(R.id.iv_lamp_dadeng_click).setOnClickListener(this);
        mView.findViewById(R.id.iv_lamp_jinguangdeng_click).setOnClickListener(this);
        mView.findViewById(R.id.iv_lamp_jiaodeng_right_click).setOnClickListener(this);
        mView.findViewById(R.id.iv_lamp_jiaodeng_left_click).setOnClickListener(this);
        mView.findViewById(R.id.iv_lamp_rixingdeng_click).setOnClickListener(this);
        mView.findViewById(R.id.iv_lamp_turnleft_click).setOnClickListener(this);
        mView.findViewById(R.id.iv_lamp_turnright_click).setOnClickListener(this);
        mView.findViewById(R.id.iv_lamp_jump).setOnClickListener(this);
        mView.findViewById(R.id.tv_car_front_lamp).setOnClickListener(this);

        ivLampDadeng = (ImageView) mView.findViewById(R.id.iv_lamp_dadeng_on);
        ivLampJingguangdeng = (ImageView) mView.findViewById(R.id.iv_lamp_jinguangdeng_on);
        ivLampJiaodengRight = (ImageView) mView.findViewById(R.id.iv_lamp_jiaodeng_right_on);
        ivLampJiaodengLeft = (ImageView) mView.findViewById(R.id.iv_lamp_jiaodeng_left_on);
        ivLampRixingdeng = (ImageView) mView.findViewById(R.id.iv_lamp_rixingdeng_on);
        ivLampTurnLeft = (ImageView) mView.findViewById(R.id.iv_lamp_turnleft_on);
        ivLampTurnRight = (ImageView) mView.findViewById(R.id.iv_lamp_turnright_on);
        mView.findViewById(R.id.tv_Head_Lamp_diagram).setOnClickListener(this);
        refreshUI();
        mBTServer = ((BaseActivity)getActivity()).mBtServer;

        return mView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_lamp_dadeng_click:
                clickLamp(ConstantData.sLampDadengStatus, ivLampDadeng, new CMDLEDHeadLampHBAll());
                break;
            case R.id.iv_lamp_jinguangdeng_click:
//                clickLamp(sLampJinguangdengStatus, ivLampJingguangdeng, new CMDLEDHeadLampLowBeam1());
                if (ConstantData.sFrontLampFragmentStatus[sLampJinguangdengStatus] == 0){
                    ivLampJingguangdeng.setVisibility(View.VISIBLE);
                    ConstantData.sFrontLampFragmentStatus[sLampJinguangdengStatus] = 1;
                    BaseCommand cmdTmp = new CMDLEDHeadLampLowBeam1();
                    cmdTmp.turnOn();
                    cmdTmp = new CMDLEDHeadLampLowBeam2();
                    cmdTmp.turnOn();
                    ServiceManager.getInstance().sendCommandToCar(cmdTmp, new CommandListenerAdapter());

                } else {
                    ivLampJingguangdeng.setVisibility(View.INVISIBLE);
                    ConstantData.sFrontLampFragmentStatus[sLampJinguangdengStatus] = 0;
                    BaseCommand cmdTmp = new CMDLEDHeadLampLowBeam1();
                    cmdTmp.turnOff();
                    cmdTmp = new CMDLEDHeadLampLowBeam2();
                    cmdTmp.turnOff();
                    ServiceManager.getInstance().sendCommandToCar(cmdTmp, new CommandListenerAdapter());
                }


                break;
            case R.id.iv_lamp_jiaodeng_right_click:
                clickLamp(ConstantData.sLampJiaodengRightStatus, ivLampJiaodengRight, new CMDLEDHeadLampRightCorner());
                break;
            case R.id.iv_lamp_jiaodeng_left_click:
                clickLamp(ConstantData.sLampJiaodengLeftStatus, ivLampJiaodengLeft, new CMDLEDHeadLampLeftCorner());
                break;
            case R.id.iv_lamp_rixingdeng_click:
//                clickLamp(ConstantData.sLampRixingdengStatus, ivLampRixingdeng, new CMDLEDHeadLampPosition());
                if (ConstantData.sFrontLampFragmentStatus[ConstantData.sLampRixingdengStatus] == 0) {
                    ivLampRixingdeng.setImageResource(R.mipmap.ic_lamp_position_on);
                    ivLampRixingdeng.setVisibility(View.VISIBLE);
                    ConstantData.sFrontLampFragmentStatus[ConstantData.sLampRixingdengStatus] = 1;

                    BaseCommand commandtmp = new CMDLEDHeadLampPosition();
                    commandtmp.turnOn();
                    ServiceManager.getInstance().sendCommandToCar(commandtmp, new CommandListenerAdapter());
                }else if (ConstantData.sFrontLampFragmentStatus[ConstantData.sLampRixingdengStatus] == 1) {
                    ivLampRixingdeng.setImageResource(R.mipmap.ic_lamp_rixingdeng_on);
                    ivLampRixingdeng.setVisibility(View.VISIBLE);
                    ConstantData.sFrontLampFragmentStatus[ConstantData.sLampRixingdengStatus] = 2;

                    BaseCommand commandtmp = new CMDLEDHeadLampPosition();
                    commandtmp.turnOff();
                    ServiceManager.getInstance().sendCommandToCar(commandtmp, new CommandListenerAdapter());

                    BaseCommand commandtmp2 = new CMDLEDHeadLampDRLLight();
                    commandtmp2.turnOn();
                    ServiceManager.getInstance().sendCommandToCar(commandtmp2, new CommandListenerAdapter());
                }else if (ConstantData.sFrontLampFragmentStatus[ConstantData.sLampRixingdengStatus] == 2) {
                    ivLampRixingdeng.setVisibility(View.INVISIBLE);
                    ConstantData.sFrontLampFragmentStatus[ConstantData.sLampRixingdengStatus] = 0;

                    BaseCommand commandtmp = new CMDLEDHeadLampDRLLight();
                    commandtmp.turnOff();
                    ServiceManager.getInstance().sendCommandToCar(commandtmp, new CommandListenerAdapter());
                }
                break;
            case R.id.iv_lamp_turnleft_click:
                clickLamp(ConstantData.sLampTurnLeftStatus, ivLampTurnLeft, new CMDLEDHeadLampTurnLeft());
                break;
            case R.id.iv_lamp_turnright_click:
                clickLamp(ConstantData.sLampTurnRightStatus, ivLampTurnRight, new CMDLEDHeadLampTurnRight());
                break;
            case R.id.iv_lamp_jump:
                ((MainActivity)getActivity()).setSelect(100);
                break;
            case R.id.tv_car_front_lamp:
                if (clickNum==0){
                    mHandler.sendEmptyMessageDelayed(1, 1000);
                }
                mHandler.sendEmptyMessage(0);
                break;
            case R.id.tv_Head_Lamp_diagram:
                ((MainActivity)getActivity()).showDiagram(ConstantData.HL_DIAGRAM);
                break;
        }
    }
    private int clickNum = 0;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    clickNum++;
                    if (clickNum >= 3){
                        ((MainActivity)getActivity()).setSelect(102);
                    }
                    break;
                case 1:
                    clickNum = 0;
                    break;
            }
        }
    };

    private void clickLamp(int index, View view, BaseCommand command){
        if (ConstantData.sFrontLampFragmentStatus[index] == 0) {

            Log.i("FrontHeadLamp","On");
            ConstantData.sFrontLampFragmentStatus[index] = 1;
            command.turnOn();
            ServiceManager.getInstance().sendCommandToCar(command,new CommandListenerAdapter(){
                @Override
                public void onSuccess(BaseResponse response) {
                    super.onSuccess(response);
                    Log.i("FrontHeadLamp","onSuccess");
                }

                @Override
                public void onTimeout() {
                    super.onTimeout();
                    Log.i("FrontHeadLamp","onTimeout");
                }
            });

            view.setVisibility(View.VISIBLE);
            if (index == ConstantData.sLampTurnRightStatus){
                setBlink(view);

                //左右转向灯不要同时闪烁
                /*ConstantData.sFrontLampFragmentStatus[ConstantData.sLampTurnLeftStatus] = 0;
                ivLampTurnLeft.setAnimation(null);
                ivLampTurnLeft.setVisibility(View.INVISIBLE);*/
            }
            if (index == ConstantData.sLampTurnLeftStatus){
                setBlink(view);

                //左右转向灯不要同时闪烁
                /*ConstantData.sFrontLampFragmentStatus[ConstantData.sLampTurnRightStatus] = 0;
                ivLampTurnRight.setAnimation(null);
                ivLampTurnRight.setVisibility(View.INVISIBLE);*/
            }
        } else {
            ConstantData.sFrontLampFragmentStatus[index] = 0;
            Log.i("FrontHeadLamp","Off");
            command.turnOff();
            ServiceManager.getInstance().sendCommandToCar(command,new CommandListenerAdapter(){
                @Override
                public void onSuccess(BaseResponse response) {
                    super.onSuccess(response);
                    Log.i("FrontHeadLamp","onSuccess");
                }

                @Override
                public void onTimeout() {
                    super.onTimeout();
                    Log.i("FrontHeadLamp","onTimeout");
                }
            });
            if (index == ConstantData.sLampTurnRightStatus){
                view.setAnimation(null);
            }
            if (index == ConstantData.sLampTurnLeftStatus){
                view.setAnimation(null);
            }
            view.setVisibility(View.INVISIBLE);
        }
    }

    private void refreshUI(){
        if (ConstantData.sFrontLampFragmentStatus[ConstantData.sLampDadengStatus] == 1) {
            ivLampDadeng.setVisibility(View.VISIBLE);
        } else {
            ivLampDadeng.setVisibility(View.INVISIBLE);
        }
        if (ConstantData.sFrontLampFragmentStatus[sLampJinguangdengStatus] == 1) {
            ivLampJingguangdeng.setVisibility(View.VISIBLE);
        } else {
            ivLampJingguangdeng.setVisibility(View.INVISIBLE);
        }
        if (ConstantData.sFrontLampFragmentStatus[ConstantData.sLampJiaodengRightStatus] == 1) {
            ivLampJiaodengRight.setVisibility(View.VISIBLE);
        } else {
            ivLampJiaodengRight.setVisibility(View.INVISIBLE);
        }
        if (ConstantData.sFrontLampFragmentStatus[ConstantData.sLampJiaodengLeftStatus] == 1) {
            ivLampJiaodengLeft.setVisibility(View.VISIBLE);
        } else {
            ivLampJiaodengLeft.setVisibility(View.INVISIBLE);
        }
        if (ConstantData.sFrontLampFragmentStatus[ConstantData.sLampRixingdengStatus] == 1) {
            ivLampRixingdeng.setVisibility(View.VISIBLE);
        } else {
            ivLampRixingdeng.setVisibility(View.INVISIBLE);
        }
        if (ConstantData.sFrontLampFragmentStatus[ConstantData.sLampTurnLeftStatus] == 1){
            ivLampTurnLeft.setVisibility(View.VISIBLE);
            setBlink(ivLampTurnLeft);
        } else {
            ivLampTurnLeft.setAnimation(null);
            ivLampTurnLeft.setVisibility(View.INVISIBLE);
        }
        if (ConstantData.sFrontLampFragmentStatus[ConstantData.sLampTurnRightStatus] == 1){
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

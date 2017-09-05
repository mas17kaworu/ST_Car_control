package com.longkai.stcarcontrol.st_exp.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager;
import com.longkai.stcarcontrol.st_exp.communication.btComm.BTServer;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDBCMRearLampList.CMDBCMRearLampBrake;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDBCMRearLampList.CMDBCMRearLampPosition;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDBCMRearLampList.CMDBCMRearLampTurnLeft;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDBCMRearLampList.CMDBCMRearLampTurnRight;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPLGMList.CMDPLGM;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter;

import java.security.spec.ECField;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/8/19.
 */

public class CarBackFragment extends Fragment implements View.OnClickListener {

    private View mView;

    private ImageView ivCarbackBreakLamp, ivCarbackPositionLamp, ivCarbackTurnleftLamp, ivCarbackTurnrightLamp;

    private BTServer mBTServer;
    private static ExecutorService sExecutorService;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_car_back, container, false);
        mView.findViewById(R.id.iv_carback_break_click).setOnClickListener(this);
        mView.findViewById(R.id.iv_carback_position_click).setOnClickListener(this);
        mView.findViewById(R.id.iv_carback_turnleft_click).setOnClickListener(this);
        mView.findViewById(R.id.iv_carback_turnright_click).setOnClickListener(this);

        ivCarbackBreakLamp = (ImageView) mView.findViewById(R.id.iv_carback_break_light);
        ivCarbackPositionLamp = (ImageView) mView.findViewById(R.id.iv_carback_position_light);
        ivCarbackTurnleftLamp = (ImageView) mView.findViewById(R.id.iv_carback_turnleft_light);
        ivCarbackTurnrightLamp = (ImageView) mView.findViewById(R.id.iv_carback_turnright_light);

        mBTServer = ((BaseActivity)getActivity()).mBtServer;




        handler.postDelayed(runnable, 500);// 打开定时器，50ms后执行runnable


        /*TimerTask diagnosticTask = new TimerTask() {
            @Override
            public void run() {
                ServiceManager.getInstance().sendCommandToCar(new CMDPLGM(true),new CommandListenerAdapter(){
                    @Override
                    public void onSuccess(BaseResponse response) {
                        super.onSuccess(response);
                        int AntiPinch = ((CMDPLGM.Response)response).getAntiPinch();
                        int MotorStatus = ((CMDPLGM.Response)response).getMotorStatus();
                        Log.i("CarBackFragment", AntiPinch + "  " + MotorStatus);
                    }
                });
            }
        };
        timer = new Timer();
        timer.schedule(diagnosticTask, 500);//开启定时器，*/


        return mView;
    }
    Timer timer;

    Handler handler = new Handler();
    Runnable runnable = new Runnable(){
        @Override
        public void run() {
            ServiceManager.getInstance().sendCommandToCar(new CMDPLGM(true),new CommandListenerAdapter(){
                @Override
                public void onSuccess(BaseResponse response) {
                    super.onSuccess(response);
                    int AntiPinch = ((CMDPLGM.Response)response).getAntiPinch();
                    int MotorStatus = ((CMDPLGM.Response)response).getMotorStatus();
                    Log.i("CarBackFragment", AntiPinch + "  " + MotorStatus);
                }
            });
            handler.removeCallbacks(this); //移除定时任务
            handler.postDelayed(runnable, 500);
        }
    };

    private class DiagnosticRunner implements Runnable{

        @Override
        public void run() {
            ServiceManager.getInstance().sendCommandToCar(new CMDPLGM(true),new CommandListenerAdapter());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_carback_break_click:
                clickLamp(ConstantData.sCarBackBreakLampStatus, ivCarbackBreakLamp,
                        new CMDBCMRearLampBrake());
                break;
            case R.id.iv_carback_position_click:
                clickLamp(ConstantData.sCarBackPositionLampStatus, ivCarbackPositionLamp,
                        new CMDBCMRearLampPosition());
                break;
            case R.id.iv_carback_turnleft_click:
                clickLamp(ConstantData.sCarBackTurnLeftLampStatus, ivCarbackTurnleftLamp,
                        new CMDBCMRearLampTurnLeft());
                break;
            case R.id.iv_carback_turnright_click:
                clickLamp(ConstantData.sCarBackTurnRightLampStatus, ivCarbackTurnrightLamp,
                        new CMDBCMRearLampTurnRight());
                break;
        }
    }


    private void clickLamp(int index, View view, BaseCommand command){
        if (ConstantData.sCarBackFragmentStatus[index] == 0) {
            ConstantData.sCarBackFragmentStatus[index] = 1;
            view.setVisibility(View.VISIBLE);
            command.turnOn();
            ServiceManager.getInstance().sendCommandToCar(command,new CommandListenerAdapter(){
                @Override
                public void onSuccess(BaseResponse response) {
                    super.onSuccess(response);
                    Log.i("CarBackFragment","onSuccess");
                }

                @Override
                public void onTimeout() {
                    super.onTimeout();
                    Log.i("CarBackFragment","onTimeout");
                }
            });

            if (index == ConstantData.sCarBackTurnLeftLampStatus){
                setBlink(view);

                ConstantData.sCarBackFragmentStatus[ConstantData.sCarBackTurnRightLampStatus] = 0;
                ivCarbackTurnrightLamp.setAnimation(null);
                ivCarbackTurnrightLamp.setVisibility(View.INVISIBLE);
            }
            if (index == ConstantData.sCarBackTurnRightLampStatus){
                setBlink(view);

                ConstantData.sCarBackFragmentStatus[ConstantData.sCarBackTurnLeftLampStatus] = 0;
                ivCarbackTurnleftLamp.setAnimation(null);
                ivCarbackTurnleftLamp.setVisibility(View.INVISIBLE);
            }
        } else {
            ConstantData.sCarBackFragmentStatus[index] = 0;
            command.turnOff();
            ServiceManager.getInstance().sendCommandToCar(command,new CommandListenerAdapter(){
                @Override
                public void onSuccess(BaseResponse response) {
                    super.onSuccess(response);
                    Log.i("CarBackFragment","onSuccess");
                }

                @Override
                public void onTimeout() {
                    super.onTimeout();
                    Log.i("CarBackFragment","onTimeout");
                }
            });

            if (index == ConstantData.sCarBackTurnLeftLampStatus){
                view.setAnimation(null);
            }
            if (index == ConstantData.sCarBackTurnRightLampStatus){
                view.setAnimation(null);
            }
            view.setVisibility(View.INVISIBLE);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }
}

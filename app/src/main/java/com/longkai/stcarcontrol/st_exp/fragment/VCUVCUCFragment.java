package com.longkai.stcarcontrol.st_exp.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.longkai.stcarcontrol.st_exp.ConstantData;
import com.longkai.stcarcontrol.st_exp.Enum.VCUStateEnum;
import com.longkai.stcarcontrol.st_exp.Interface.VCUCircleStateChange;
import com.longkai.stcarcontrol.st_exp.R;
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCU7List.CMDVCU7;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUGUIList.CMDVCUHVOff;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUGUIList.CMDVCUHVOn;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Administrator on 2018/5/20.
 */

public class VCUVCUCFragment extends Fragment implements View.OnClickListener, VCUCircleStateChange {
    private View mView;

    private ImageView iv_jdq_zhuzheng, iv_jdq_yuchong, iv_jdq_zhufu;
    private TextView tv_gyjc1, tv_gyjc2;

    GifImageView gifViewCircle, gifViewChart;

    private VCUStateEnum presentState;

    private VCUCircleStateChange mVCUCircleStateChange;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_vcu_vcu, container, false);
        gifViewCircle = (GifImageView) mView.findViewById(R.id.gifv_vcu_circle);
        gifViewChart = (GifImageView) mView.findViewById(R.id.gifv_vcu_chart);
        iv_jdq_zhuzheng = (ImageView) mView.findViewById(R.id.iv_vcu_gyjc_jdq1);
        iv_jdq_yuchong = (ImageView) mView.findViewById(R.id.iv_vcu_gyjc_jdq2);
        iv_jdq_zhufu = (ImageView) mView.findViewById(R.id.iv_vcu_gyjc_jdq3);
        tv_gyjc1 = (TextView) mView.findViewById(R.id.tv_gyjc1);
        tv_gyjc2 = (TextView) mView.findViewById(R.id.tv_gyjc2);
        presentState = VCUStateEnum.IDLE;
        handler.postDelayed(runnable, 500);// 打开定时器，500ms后执行runnable
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mVCUCircleStateChange = this;
        updateJDQ();
//        mVCUCircleStateChange.shangDianState1();
    }

    public VCUCircleStateChange getController(){
        return mVCUCircleStateChange;
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            ServiceManager.getInstance().sendCommandToCar(new CMDVCU7(), new CommandListenerAdapter(){
                @Override
                public void onSuccess(BaseResponse response) {
                    super.onSuccess(response);
                    final BaseResponse r = response;
                    if (getActivity()!=null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if ((((CMDVCU7.Response) r).gaoya_jidianqi_status & 0x01) == 0) {//主正
                                    ConstantData.sVCUJDQ1State = false;
                                    iv_jdq_zhuzheng.setImageResource(R.mipmap.ic_vcu_dianlu_jdq_off);
                                } else {
                                    ConstantData.sVCUJDQ1State = true;
                                    iv_jdq_zhuzheng.setImageResource(R.mipmap.ic_vcu_dianlu_jdq_on);
                                }

                                if ((((CMDVCU7.Response) r).gaoya_jidianqi_status & 0x02) == 0) {//主负
                                    ConstantData.sVCUJDQ3State = false;
                                    iv_jdq_zhufu.setImageResource(R.mipmap.ic_vcu_dianlu_jdq_off);
                                } else {
                                    ConstantData.sVCUJDQ3State = true;
                                    iv_jdq_zhufu.setImageResource(R.mipmap.ic_vcu_dianlu_jdq_on);
                                }

                                if ((((CMDVCU7.Response) r).gaoya_jidianqi_status & 0x04) == 0) {//预充
                                    ConstantData.sVCUJDQ2State = false;
                                    iv_jdq_yuchong.setImageResource(R.mipmap.ic_vcu_dianlu_jdq_off);
                                } else {
                                    ConstantData.sVCUJDQ2State = false;
                                    iv_jdq_yuchong.setImageResource(R.mipmap.ic_vcu_dianlu_jdq_on);
                                }
                                updateStatus();
                            }
                        });
                    }

                }
            });

            handler.removeCallbacks(this); //移除定时任务
            handler.postDelayed(runnable, 500);

        }
    };

    @Override
    public void onClick(View v) {

    }

    @Override
    public void shangDianState1() {
        try{
            Log.i("LK test", "shangDianState1");
            GifDrawable gifDrawableChart = new GifDrawable(getResources(), R.mipmap.gif_vcu_circle_zero);
            gifViewChart.setImageDrawable(gifDrawableChart);
            presentState = VCUStateEnum.SD1;
            ServiceManager.getInstance().sendCommandToCar(new CMDVCUHVOff(false), new CommandListenerAdapter());
            ServiceManager.getInstance().sendCommandToCar(new CMDVCUHVOn(true), new CommandListenerAdapter());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void shangDianState2() {
        try {
            GifDrawable gifDrawable = new GifDrawable(getResources(), R.mipmap.gif_vcu_gyjyjc_yuchong);
            gifViewCircle.setImageDrawable(gifDrawable);

            GifDrawable gifDrawableChart = new GifDrawable(getResources(), R.mipmap.gif_vcu_circle_rise);
            gifViewChart.setImageDrawable(gifDrawableChart);
//            tv_gyjc1.setText("50V");
            tv_gyjc2.setText("0V");
//            updateJDQ();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void shangDianState3() {
        try {
            GifDrawable gifDrawable = new GifDrawable(getResources(), R.mipmap.gif_vcu_gyjyjc_shangdian);
            gifViewCircle.setImageDrawable(gifDrawable);

            GifDrawable gifDrawableChart = new GifDrawable(getResources(), R.mipmap.gif_vcu_circle_high);
            gifViewChart.setImageDrawable(gifDrawableChart);

            tv_gyjc2.setText("50V");

//            updateJDQ();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void xiaDianState1() {
        try {
            GifDrawable gifDrawableChart = new GifDrawable(getResources(), R.mipmap.gif_vcu_circle_high);
            gifViewChart.setImageDrawable(gifDrawableChart);
            presentState = VCUStateEnum.XD1;
            ServiceManager.getInstance().sendCommandToCar(new CMDVCUHVOn(false), new CommandListenerAdapter());
            ServiceManager.getInstance().sendCommandToCar(new CMDVCUHVOff(true), new CommandListenerAdapter());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void xiaDianState2() {
        try {
            GifDrawable gifDrawableChart = new GifDrawable(getResources(), R.mipmap.gif_vcu_circle_decline);
            gifViewChart.setImageDrawable(gifDrawableChart);

//            ConstantData.sVCUJDQ1State = false;
//            ConstantData.sVCUJDQ2State = true;
//            ConstantData.sVCUJDQ3State = true;
//            updateJDQ();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void xiaDianState3() {
        try {
            GifDrawable gifDrawableChart = new GifDrawable(getResources(), R.mipmap.gif_vcu_circle_zero);
            gifViewChart.setImageDrawable(gifDrawableChart);

            tv_gyjc2.setText("0V");
//            ConstantData.sVCUJDQ1State = false;
//            ConstantData.sVCUJDQ2State = false;
//            ConstantData.sVCUJDQ3State = false;
//            updateJDQ();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public void updateCircle

    private void updateJDQ(){
        if (ConstantData.sVCUJDQ1State)
            iv_jdq_zhuzheng.setImageResource(R.mipmap.ic_vcu_dianlu_jdq_on);
        else
            iv_jdq_zhuzheng.setImageResource(R.mipmap.ic_vcu_dianlu_jdq_off);

        if (ConstantData.sVCUJDQ2State)
            iv_jdq_yuchong.setImageResource(R.mipmap.ic_vcu_dianlu_jdq_on);
        else
            iv_jdq_yuchong.setImageResource(R.mipmap.ic_vcu_dianlu_jdq_off);

        if (ConstantData.sVCUJDQ3State)
            iv_jdq_zhufu.setImageResource(R.mipmap.ic_vcu_dianlu_jdq_on);
        else
            iv_jdq_zhufu.setImageResource(R.mipmap.ic_vcu_dianlu_jdq_off);

    }

    private void updateStatus(){
        if ( (presentState.equals(VCUStateEnum.SD1) || presentState.equals(VCUStateEnum.IDLE))
                && ConstantData.sVCUJDQ2State && ConstantData.sVCUJDQ3State){
            presentState = VCUStateEnum.SD2;
            this.shangDianState2();
        }

        if ( (presentState.equals(VCUStateEnum.SD2) || presentState.equals(VCUStateEnum.IDLE))
                && ConstantData.sVCUJDQ1State && ConstantData.sVCUJDQ3State){
            presentState = VCUStateEnum.SD2;
            this.shangDianState3();
        }

        if ( (presentState.equals(VCUStateEnum.XD1) || presentState.equals(VCUStateEnum.IDLE))
                && !ConstantData.sVCUJDQ1State && ConstantData.sVCUJDQ3State){
            presentState = VCUStateEnum.XD2;
            this.xiaDianState2();
        }

        if ( (presentState.equals(VCUStateEnum.XD2) || presentState.equals(VCUStateEnum.IDLE))
                && !ConstantData.sVCUJDQ1State && !ConstantData.sVCUJDQ3State){
            presentState = VCUStateEnum.XD3;
            this.xiaDianState3();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}

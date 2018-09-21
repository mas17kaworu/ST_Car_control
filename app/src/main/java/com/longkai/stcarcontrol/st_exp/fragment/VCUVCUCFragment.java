package com.longkai.stcarcontrol.st_exp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.longkai.stcarcontrol.st_exp.Interface.VCUCircleStateChange;
import com.longkai.stcarcontrol.st_exp.R;
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager;
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

    private ImageView iv_jdq1, iv_jdq2, iv_jdq3;

    GifImageView gifViewCircle, gifViewChart;

    private VCUCircleStateChange mVCUCircleStateChange;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_vcu_vcu, container, false);
        gifViewCircle = (GifImageView) mView.findViewById(R.id.gifv_vcu_circle);
        gifViewChart = (GifImageView) mView.findViewById(R.id.gifv_vcu_chart);
        iv_jdq1 = (ImageView) mView.findViewById(R.id.iv_vcu_gyjc_jdq1);
        iv_jdq2 = (ImageView) mView.findViewById(R.id.iv_vcu_gyjc_jdq2);
        iv_jdq3 = (ImageView) mView.findViewById(R.id.iv_vcu_gyjc_jdq3);


        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mVCUCircleStateChange = this;
        mVCUCircleStateChange.shangDianState1();
    }

    public VCUCircleStateChange getController(){
        return mVCUCircleStateChange;
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void shangDianState1() {
        try{
            GifDrawable gifDrawableChart = new GifDrawable(getResources(), R.mipmap.gif_vcu_circle_zero);
            gifViewChart.setImageDrawable(gifDrawableChart);

            ServiceManager.getInstance().sendCommandToCar(new CMDVCUHVOn(true), new CommandListenerAdapter());
            ServiceManager.getInstance().sendCommandToCar(new CMDVCUHVOff(false), new CommandListenerAdapter());
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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void xiaDianState1() {
        try {
            GifDrawable gifDrawableChart = new GifDrawable(getResources(), R.mipmap.gif_vcu_circle_high);
            gifViewChart.setImageDrawable(gifDrawableChart);

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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void xiaDianState3() {
        try {
            GifDrawable gifDrawableChart = new GifDrawable(getResources(), R.mipmap.gif_vcu_circle_zero);
            gifViewChart.setImageDrawable(gifDrawableChart);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public void updateCircle
}

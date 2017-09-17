package com.longkai.stcarcontrol.st_exp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.longkai.stcarcontrol.st_exp.ConstantData;
import com.longkai.stcarcontrol.st_exp.R;
import com.longkai.stcarcontrol.st_exp.customView.AirconditionDiscView;
import com.longkai.stcarcontrol.st_exp.customView.CoverWindView;

/**
 * Created by Administrator on 2017/8/11.
 */

public class CenterControlFragment extends Fragment implements View.OnClickListener{
    private View mView;
    private AirconditionDiscView avWindPower,avWindAngle;
    private CoverWindView mCoverWindView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_center_control, container, false);
        mCoverWindView = (CoverWindView) mView.findViewById(R.id.cover_wind_view);

        avWindAngle = (AirconditionDiscView) mView.findViewById(R.id.aircondition_angle);
        avWindPower = (AirconditionDiscView) mView.findViewById(R.id.aircondition_power);
        avWindAngle.setProgressChangeListener(mAngleProgressChangeListener);
        avWindPower.setProgressChangeListener(mPowerProgressChangeListener);

        refreshUI();

        return mView;
    }

    AirconditionDiscView.ProgressChangeListener mAngleProgressChangeListener = new AirconditionDiscView.ProgressChangeListener(){
        @Override
        public void onProgressChangeListener(int progress) {
            //0~240  -30~30
            mCoverWindView.setAngle(progress *60 / 240 - 30);
            ConstantData.sCenterControlStatus[ConstantData.sCenterControlWindAngle] = progress;
        }
    };

    AirconditionDiscView.ProgressChangeListener mPowerProgressChangeListener = new AirconditionDiscView.ProgressChangeListener(){
        @Override
        public void onProgressChangeListener(int progress) {
            //0~240  0.5~1
            float scale = progress / 480.0f + 0.5f;
            mCoverWindView.setPower(scale);
            ConstantData.sCenterControlStatus[ConstantData.sCenterControlWindPower] = progress;
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
}

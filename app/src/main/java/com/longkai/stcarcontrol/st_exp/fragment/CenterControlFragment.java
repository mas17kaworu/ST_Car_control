package com.longkai.stcarcontrol.st_exp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.longkai.stcarcontrol.st_exp.R;
import com.longkai.stcarcontrol.st_exp.customView.AirconditionDiscView;

/**
 * Created by Administrator on 2017/8/11.
 */

public class CenterControlFragment extends Fragment implements View.OnClickListener{
    private View mView;
    private AirconditionDiscView avWindPower,avWindAngle;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_center_control, container, false);
        avWindAngle = (AirconditionDiscView) mView.findViewById(R.id.aircondition_angle);
        avWindPower = (AirconditionDiscView) mView.findViewById(R.id.aircondition_power);

        return mView;
    }


    @Override
    public void onClick(View v) {

    }
}

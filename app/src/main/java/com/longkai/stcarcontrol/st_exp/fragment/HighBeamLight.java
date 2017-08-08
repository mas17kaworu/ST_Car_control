package com.longkai.stcarcontrol.st_exp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.longkai.stcarcontrol.st_exp.ConstantData;
import com.longkai.stcarcontrol.st_exp.R;
import com.longkai.stcarcontrol.st_exp.activity.MainActivity;

/**
 * Created by Administrator on 2017/7/10.
 */

public class HighBeamLight extends Fragment implements View.OnClickListener{
    private View mView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_hight_beam_light, container, false);
        mView.findViewById(R.id.rdoBtn_high_beam_urban).setOnClickListener(this);
        mView.findViewById(R.id.rdoBtn_high_beam_highway).setOnClickListener(this);
        mView.findViewById(R.id.rdoBtn_high_beam_country).setOnClickListener(this);
        mView.findViewById(R.id.rdoBtn_high_beam_curve).setOnClickListener(this);
        mView.findViewById(R.id.rdoBtn_high_beam_parking).setOnClickListener(this);
        mView.findViewById(R.id.rdoBtn_high_beam_energy_saving).setOnClickListener(this);
        mView.findViewById(R.id.iv_high_beam_back).setOnClickListener(this);

        return mView;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_high_beam_back:
                ((MainActivity)getActivity()).setSelect(1);
                break;
        }
    }
}

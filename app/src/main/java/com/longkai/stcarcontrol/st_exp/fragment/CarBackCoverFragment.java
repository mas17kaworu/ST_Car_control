package com.longkai.stcarcontrol.st_exp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.longkai.stcarcontrol.st_exp.R;

/**
 * Created by Administrator on 2017/9/9.
 */

public class CarBackCoverFragment extends Fragment implements View.OnClickListener {

    private View mView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_car_back_trunk, container, false);



        return mView;
    }

    @Override
    public void onClick(View v) {

    }
}

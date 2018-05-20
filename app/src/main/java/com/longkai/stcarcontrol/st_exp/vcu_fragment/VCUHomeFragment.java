package com.longkai.stcarcontrol.st_exp.vcu_fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.longkai.stcarcontrol.st_exp.R;

/**
 * Created by Administrator on 2018/5/13.
 */

public class VCUHomeFragment extends Fragment implements View.OnClickListener{
    private View mView;

    private ImageView mAutoDisplay;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_homepage, container, false);
        mAutoDisplay = (ImageView) mView.findViewById(R.id.iv_homepage_auto_display);
        mAutoDisplay.setOnClickListener(this);
        return mView;
    }

    @Override
    public void onClick(View v) {

    }
}

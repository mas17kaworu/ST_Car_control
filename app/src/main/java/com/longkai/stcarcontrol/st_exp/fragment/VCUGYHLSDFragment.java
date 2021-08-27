package com.longkai.stcarcontrol.st_exp.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.longkai.stcarcontrol.st_exp.R;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Administrator on 2018/5/20.
 */

public class VCUGYHLSDFragment extends Fragment implements View.OnClickListener {
    private View mView;
    private GifImageView gif_view_send;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_vcu_gyhlsd, container, false);
        gif_view_send = (GifImageView) mView.findViewById(R.id.gifv_vcu_gysj);
        mView.findViewById(R.id.btn_vcu_gysd).setOnClickListener(this);
        mView.findViewById(R.id.btn_vcu_gyxd).setOnClickListener(this);
        mView.findViewById(R.id.btn_vcu_charging).setOnClickListener(this);
        mView.findViewById(R.id.btn_vcu_niuju_jisuan).setOnClickListener(this);
        try {
            GifDrawable gifDrawable = new GifDrawable(getResources(), R.mipmap.gif_vcu_gyjyjc_shangdian);
            gif_view_send.setImageDrawable(gifDrawable);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_vcu_gysd:
                break;
            case R.id.btn_vcu_gyxd:
                break;
            case R.id.btn_vcu_niuju_jisuan:
                break;
            case R.id.btn_vcu_charging:
                break;
        }
    }
}

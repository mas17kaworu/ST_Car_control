package com.longkai.stcarcontrol.st_exp.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.longkai.stcarcontrol.st_exp.R;

/**
 * Created by Administrator on 2018/5/20.
 */

public class VCUGYHLXDFragment extends Fragment implements View.OnClickListener {
    private View mView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_vcu_vcu, container, false);
        return mView;
    }

    @Override
    public void onClick(View v) {

    }
}

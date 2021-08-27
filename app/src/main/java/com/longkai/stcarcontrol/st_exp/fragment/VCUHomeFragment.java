package com.longkai.stcarcontrol.st_exp.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.longkai.stcarcontrol.st_exp.R;
import com.longkai.stcarcontrol.st_exp.activity.VCUActivity;

/**
 * Created by Administrator on 2018/5/20.
 */

public class VCUHomeFragment extends Fragment implements View.OnClickListener {
    private View mView;

    private ImageView mAutoDisplay;
    private ImageView ivCar;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_vcu_homepage, container, false);
        ivCar = (ImageView) mView.findViewById(R.id.iv_vcu_homepage_car);
        ivCar.setOnClickListener(this);
        return mView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_vcu_homepage_car:
                ((VCUActivity)getActivity()).showDiagram();
                break;
        }
    }
}

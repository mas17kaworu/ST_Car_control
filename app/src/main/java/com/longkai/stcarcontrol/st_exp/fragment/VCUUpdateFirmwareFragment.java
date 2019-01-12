package com.longkai.stcarcontrol.st_exp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.longkai.stcarcontrol.st_exp.R;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Administrator on 2019/1/10.
 */

public class VCUUpdateFirmwareFragment extends Fragment implements View.OnClickListener{

    private View mView;

    private ImageView ivUpdateAIcon, ivUpdateBIcon;
    private ImageView ivUpdateAControl, ivUpdateBControl;

    private AtomicBoolean AinUpdating = new AtomicBoolean(false);
    private AtomicBoolean BinUpdating = new AtomicBoolean(false);


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_vcu_update_firmware, container, false);

        ivUpdateAIcon = (ImageView) mView.findViewById(R.id.iv_vcu_update_a_icon);
        ivUpdateAIcon.setOnClickListener(this);
        ivUpdateBIcon = (ImageView) mView.findViewById(R.id.iv_vcu_update_b_icon);
        ivUpdateBIcon.setOnClickListener(this);

        ivUpdateAControl = (ImageView) mView.findViewById(R.id.iv_vcu_update_a_control_btn);
        ivUpdateAControl.setOnClickListener(this);
        ivUpdateBControl = (ImageView) mView.findViewById(R.id.iv_vcu_update_b_control_btn);
        ivUpdateBControl.setOnClickListener(this);

        return mView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_vcu_update_a_icon:
                if (BinUpdating.get()){
                    return;
                }

                ivUpdateAIcon.setImageResource(R.mipmap.ic_vcu_update_a_selected);
                ivUpdateBIcon.setImageResource(R.mipmap.ic_vcu_update_b_normal);

                ivUpdateAControl.setVisibility(View.VISIBLE);
                ivUpdateBControl.setVisibility(View.INVISIBLE);
                break;
            case R.id.iv_vcu_update_b_icon:
                if (AinUpdating.get()){
                    return;
                }

                ivUpdateBIcon.setImageResource(R.mipmap.ic_vcu_update_b_selected);
                ivUpdateAIcon.setImageResource(R.mipmap.ic_vcu_update_a_normal);
                ivUpdateBControl.setVisibility(View.VISIBLE);
                ivUpdateAControl.setVisibility(View.INVISIBLE);
                break;

            case R.id.iv_vcu_update_a_control_btn:
                break;
            case R.id.iv_vcu_update_b_control_btn:
                break;
        }
    }
}

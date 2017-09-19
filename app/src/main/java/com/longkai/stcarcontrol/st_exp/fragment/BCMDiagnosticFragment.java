package com.longkai.stcarcontrol.st_exp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.longkai.stcarcontrol.st_exp.R;
import com.longkai.stcarcontrol.st_exp.activity.MainActivity;

/**
 * Created by Administrator on 2017/9/12.
 */

public class BCMDiagnosticFragment extends Fragment implements View.OnClickListener{
    private View mView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_diagnostic_bcm, container, false);

        mView.findViewById(R.id.iv_diagnostic_back).setOnClickListener(this);

        return mView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_diagnostic_back:
                ((MainActivity)getActivity()).setSelect(5);
                break;

        }
    }
}

package com.longkai.stcarcontrol.st_exp.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.longkai.stcarcontrol.st_exp.R;
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDAutoRunSwitch;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter;

/**
 * Created by Administrator on 2017/7/10.
 */

public class HomeFragment extends Fragment implements View.OnClickListener{
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

    private int status=0;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_homepage_auto_display:
                if (status==0){
                    status = 1;
                    mAutoDisplay.setImageResource(R.mipmap.ic_homepage_auto_display_c);
                    ServiceManager.getInstance().sendCommandToCar(new CMDAutoRunSwitch(true), new CommandListenerAdapter());
                }else {
                    status = 0;
                    mAutoDisplay.setImageResource(R.mipmap.ic_homepage_auto_display_u);
                    ServiceManager.getInstance().sendCommandToCar(new CMDAutoRunSwitch(false), new CommandListenerAdapter());
                }
                break;
        }
    }
}

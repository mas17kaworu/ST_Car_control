package com.longkai.stcarcontrol.st_exp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.longkai.stcarcontrol.st_exp.R;
import com.longkai.stcarcontrol.st_exp.customView.DashboardView;

/**
 * Created by Administrator on 2018/5/20.
 */

public class VCUMCUFragment extends Fragment implements View.OnClickListener {
    private View mView;
    private DashboardView engineSpeedDashboard;

    Thread testThread;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_vcu_mcu, container, false);
        engineSpeedDashboard = (DashboardView)mView.findViewById(R.id.dashboard_mcu_speed);
        testThread = new Thread(){
            @Override
            public void run() {
                super.run();
                while(true){
                    engineSpeedDashboard.setValue((float) (Math.random()*100.0f));
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        testThread.start();
        return mView;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        testThread = null;
    }
}

package com.longkai.stcarcontrol.st_exp.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.longkai.stcarcontrol.st_exp.R;
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUMCU1List.CMDVCUMCU1;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter;
import com.longkai.stcarcontrol.st_exp.customView.dashboard.DashboardView;
import com.longkai.stcarcontrol.st_exp.customView.dashboard.MCUVoltageDashboard;

/**
 * Created by Administrator on 2018/5/20.
 */

public class VCUMCUFragment extends Fragment implements View.OnClickListener {
    private View mView;
    private DashboardView engineSpeedDashboard;
    private MCUVoltageDashboard mcuVoltageDashboard;

    private TextView tvNiuju, tvCurrent;

    Thread testThread;

    private int motorRealTimeSpeed, motorCurrent, motorTemp, mcuTemp, mcuInputVoltage;
    private float motorTorch;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_vcu_mcu, container, false);
        engineSpeedDashboard = (DashboardView)mView.findViewById(R.id.dashboard_mcu_speed);
        mcuVoltageDashboard = (MCUVoltageDashboard) mView.findViewById(R.id.dashboard_mcu_voltage);
        tvCurrent = (TextView) mView.findViewById(R.id.tv_mcu_dianliu_number);
        tvNiuju = (TextView) mView.findViewById(R.id.tv_mcu_niuju_number);

        handler.postDelayed(runnable, 500);// 打开定时器，500ms后执行runnable
        /*testThread = new Thread(){
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
        };*/
        //testThread.start();

        return mView;
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable(){
        @Override
        public void run() {
            ServiceManager.getInstance().sendCommandToCar(new CMDVCUMCU1(),new CommandListenerAdapter(){
                @Override
                public void onSuccess(BaseResponse response) {
                    super.onSuccess(response);
                    motorRealTimeSpeed = ((CMDVCUMCU1.Response)response).Motor_Realtime_Speed;
                    motorTorch = ((CMDVCUMCU1.Response)response).Torch_of_Motor;
                    motorCurrent = ((CMDVCUMCU1.Response)response).Motor_Current;
                    motorTemp = ((CMDVCUMCU1.Response)response).Temp_of_Motor;
                    mcuTemp = ((CMDVCUMCU1.Response)response).Temp_of_MCU;
                    mcuInputVoltage = ((CMDVCUMCU1.Response)response).Input_Voltage_of_MCU;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            engineSpeedDashboard.setValue(motorRealTimeSpeed);
                            mcuVoltageDashboard.setValue(mcuInputVoltage);
                            tvNiuju.setText(Float.toString(motorTorch));
                            tvCurrent.setText(Integer.toString(motorCurrent));
                        }
                    });

                }
            });
            handler.removeCallbacks(this); //移除定时任务
            handler.postDelayed(runnable, 1000);
        }
    };

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        testThread = null;
        handler.removeCallbacks(runnable);
    }
}

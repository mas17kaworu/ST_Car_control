package com.longkai.stcarcontrol.st_exp.fragment;

import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.longkai.stcarcontrol.st_exp.R;
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUCarModeList.CMDVCUCarMode;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUMCU1List.CMDVCUMCU1;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter;
import com.longkai.stcarcontrol.st_exp.customView.Thermometer;
import com.longkai.stcarcontrol.st_exp.customView.dashboard.DashboardView;
import com.longkai.stcarcontrol.st_exp.customView.dashboard.MCUVoltageDashboard;

/**
 * Created by Administrator on 2018/5/20.
 */

public class VCUMCUFragment extends Fragment implements View.OnClickListener {
    private View mView;
    private DashboardView engineSpeedDashboard;
    private MCUVoltageDashboard mcuVoltageDashboard;
    private Thermometer mcuTmpView;
    private Thermometer engineTmpView;
    private Switch swDemo;

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
        swDemo = (Switch) mView.findViewById(R.id.sw_vcu_mcu_demo_actual);
        swDemo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CMDVCUCarMode cmdvcuCarMode = new CMDVCUCarMode();
                if (isChecked){
                    cmdvcuCarMode.change2Static();
                    ServiceManager.getInstance().sendCommandToCar(cmdvcuCarMode, new CommandListenerAdapter());
                } else {
                    cmdvcuCarMode.change2Driving();
                    ServiceManager.getInstance().sendCommandToCar(cmdvcuCarMode, new CommandListenerAdapter());
                }
            }
        });

        mcuTmpView = (Thermometer) mView.findViewById(R.id.thermometer_mcu_mcu);
        engineTmpView = (Thermometer) mView.findViewById(R.id.thermometer_mcu_engine);
        handler.postDelayed(runnable, 500);// 打开定时器，500ms后执行runnable

        //start test
//        MockMessageServiceImpl.getService().StartService(VCUMCUFragment.class.toString());
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
//        mcuTmpView.setValue(0);
//        engineTmpView.setValue(60);
        return mView;
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable(){
        @Override
        public void run() {
            ServiceManager.getInstance().sendCommandToCar(new CMDVCUMCU1(),new CommandListenerAdapter<CMDVCUMCU1.Response>(){
                @Override
                public void onSuccess(CMDVCUMCU1.Response response) {
                    super.onSuccess(response);
                    motorRealTimeSpeed = response.Motor_Realtime_Speed;
                    motorTorch = response.Torch_of_Motor;
                    motorCurrent = response.Motor_Current;
                    motorTemp = response.Temp_of_Motor;
                    mcuTemp = response.Temp_of_MCU;
                    mcuInputVoltage = response.Input_Voltage_of_MCU;
                    if (getActivity() != null) {
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
                    mcuTmpView.setValue(response.Temp_of_MCU);
                    engineTmpView.setValue(response.Temp_of_Motor);
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

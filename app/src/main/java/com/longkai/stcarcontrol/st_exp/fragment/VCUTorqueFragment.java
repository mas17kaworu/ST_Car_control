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
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCU3List.CMDVCU3;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCU4List.CMDVCU4;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCU6List.CMDVCU6;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUMCU1List.CMDVCUMCU1;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter;
import com.longkai.stcarcontrol.st_exp.customView.TorqueVerticalbar;
import com.longkai.stcarcontrol.st_exp.customView.dashboard.TorqueDashboard;

/**
 * Created by longka on 7/31/2018.
 */

public class VCUTorqueFragment extends Fragment {
    private View mView;
    private TorqueVerticalbar pedalBar, brakeBar;
    private TorqueDashboard torchRequire;

    private TextView tvTestText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_vcu_torque, container, false);
        pedalBar = (TorqueVerticalbar) mView.findViewById(R.id.vrb_torque_acc);
        pedalBar.setMaxValue(100.f);
        pedalBar.setMinValue(0);
        brakeBar = (TorqueVerticalbar) mView.findViewById(R.id.vrb_torque_break);
        brakeBar.setMaxValue(100.f);
        brakeBar.setMinValue(0);
        torchRequire = (TorqueDashboard) mView.findViewById(R.id.dashboard_vcu_torch_expected);

        tvTestText = (TextView) mView.findViewById(R.id.tv_vcu_torque_test_text);
        handler.postDelayed(runnable, 500);// 打开定时器，500ms后执行runnable
        return mView;
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            final StringBuilder builder = new StringBuilder();

            ServiceManager.getInstance().sendCommandToCar(new CMDVCU3(), new CommandListenerAdapter(){
                @Override
                public void onSuccess(BaseResponse response) {
                    super.onSuccess(response);
                    brakeBar.setValue((float)((CMDVCU3.Response)response).Brake_Status);
                    pedalBar.setValue((float)((CMDVCU3.Response)response).Pedal_Status);

                    builder.append("Brake_Status:" + ((CMDVCU3.Response)response).Brake_Status + " ");
                    builder.append("Pedal_Status:" + ((CMDVCU3.Response)response).Pedal_Status + " ");
                }
            });
            ServiceManager.getInstance().sendCommandToCar(new CMDVCU4(), new CommandListenerAdapter(){
                @Override
                public void onSuccess(BaseResponse response) {
                    super.onSuccess(response);
                    torchRequire.setPercent((((CMDVCU4.Response)response).Motor_Expected_Torch + 2000f) *100 / 7000f);

                    builder.append("Motor_Expected_Torch:" + ((CMDVCU4.Response)response).Motor_Expected_Torch);
                }
            });

            tvTestText.setText(builder.toString());
            handler.removeCallbacks(this); //移除定时任务
            handler.postDelayed(runnable, 1000);

        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}

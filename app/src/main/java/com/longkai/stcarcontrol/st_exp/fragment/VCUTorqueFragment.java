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
import com.longkai.stcarcontrol.st_exp.Utils.Logger;
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCU7List.CMDVCU7;
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

    private TextView tvTestText,tvTestText2;

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
        tvTestText2 = (TextView) mView.findViewById(R.id.tv_vcu_torque_test_text2);
        handler.postDelayed(runnable, 500);// 打开定时器，500ms后执行runnable
        return mView;
    }

    Handler handler = new Handler();
    final StringBuilder builder = new StringBuilder();

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            final StringBuilder builder2 = new StringBuilder();
            builder2.append("test ");

            tvTestText.setText(builder.toString());


//            brakeBar.setValue(42663.0f);
            ServiceManager.getInstance().sendCommandToCar(new CMDVCU7(), new CommandListenerAdapter(){
                @Override
                public void onSuccess(BaseResponse response) {
                    super.onSuccess(response);
                    builder.delete(0, builder.length());
                    builder.append("Test: ");
                    builder.append("Brake_Status:" + ((CMDVCU7.Response)response).break_status + " ");
                    builder.append("Pedal_Status:" + ((CMDVCU7.Response)response).pedal_status + " ");
                    builder.append("torch :" + ((CMDVCU7.Response)response).torch_expired + " ");

                    brakeBar.setValue((float)((CMDVCU7.Response)response).break_status);
                    pedalBar.setValue((float)((CMDVCU7.Response)response).pedal_status);

                    torchRequire.setPercent( ((CMDVCU7.Response) response).torch_expired * 100 / 200);
                }
            });

            builder2.append(Logger.getLogger().Logger1ToString());
            builder2.append(Logger.getLogger().Logger2ToString());
            builder2.append(Logger.getLogger().Logger3ToString());

            tvTestText2.setText(builder2.toString());


            /*ServiceManager.getInstance().sendCommandToCar(new CMDVCU4(), new CommandListenerAdapter(){
                @Override
                public void onSuccess(BaseResponse response) {
                    super.onSuccess(response);
                    torchRequire.setPercent((((CMDVCU4.Response)response).Motor_Expected_Torch + 2000f) *100 / 7000f);
                }
            });*/


            handler.removeCallbacks(this); //移除定时任务
            handler.postDelayed(runnable, 200);

        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}

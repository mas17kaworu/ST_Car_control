package com.longkai.stcarcontrol.st_exp.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.longkai.stcarcontrol.st_exp.R;
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCU6List.CMDVCU6;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUBMS1List.CMDVCUBMS1;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter;
import com.longkai.stcarcontrol.st_exp.customView.dashboard.DashboardView;
import com.longkai.stcarcontrol.st_exp.customView.dashboard.VoltageDashboard;

/**
 * Created by Administrator on 2018/5/20.
 */

public class VCUBMSFragment extends Fragment implements View.OnClickListener {
    private View mView;

    private VoltageDashboard dashboardMainPlusBefore, dashboardMainPlusAfter, dashboardMainMinusAfter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_vcu_bms, container, false);

        dashboardMainPlusBefore = (VoltageDashboard) mView.findViewById(R.id.dashboard_bms_main_jdq_front);
        dashboardMainPlusAfter=(VoltageDashboard) mView.findViewById(R.id.dashboard_bms_main_jdq_behind);
        dashboardMainMinusAfter=(VoltageDashboard) mView.findViewById(R.id.dashboard_bms_minus_jdq_behind);

//        dashboardMainPlusBefore.setValue(20f);
        handler.postDelayed(runnable, 500);// 打开定时器，500ms后执行runnable
        return mView;
    }

    @Override
    public void onClick(View v) {

    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            ServiceManager.getInstance().sendCommandToCar(new CMDVCUBMS1(), new CommandListenerAdapter(){
                @Override
                public void onSuccess(BaseResponse response) {
                    super.onSuccess(response);
                    final BaseResponse r = response;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            dashboardMainPlusBefore.setValue(((CMDVCUBMS1.Response) r).U_HighVoltage_1);
                            dashboardMainPlusAfter.setValue(((CMDVCUBMS1.Response) r).U_HighVoltage_2);
                            dashboardMainMinusAfter.setValue(((CMDVCUBMS1.Response) r).U_HighVoltage_3);
                            /*if (((CMDVCUBMS1.Response) r).Charger_Status == 0) {
                                ivChargeState.setImageResource(R.mipmap.ic_vcu_charge_unplug);
                            } else {
                                ivChargeState.setImageResource(R.mipmap.ic_vcu_charge_plugin);
                            }

                            if (((CMDVCU6.Response) r).Locker_Status == 0) {
                                ivCarLockState.setImageResource(R.mipmap.ic_vcu_charge_carunlock);
                            } else {
                                ivCarLockState.setImageResource(R.mipmap.ic_vcu_charge_carlocked);
                            }*/
                        }
                    });

                }
            });

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

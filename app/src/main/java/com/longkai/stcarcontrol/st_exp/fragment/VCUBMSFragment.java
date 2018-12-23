package com.longkai.stcarcontrol.st_exp.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.longkai.stcarcontrol.st_exp.R;
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCU6List.CMDVCU6;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUBMS1List.CMDVCUBMS1;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUBMS3List.CMDVCUBMS3;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUBMS4List.CMDVCUBMS4;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter;
import com.longkai.stcarcontrol.st_exp.customView.VerticalRollingBar;
import com.longkai.stcarcontrol.st_exp.customView.dashboard.DashboardView;
import com.longkai.stcarcontrol.st_exp.customView.dashboard.VoltageDashboard;

/**
 * Created by Administrator on 2018/5/20.
 */

public class VCUBMSFragment extends Fragment implements View.OnClickListener {
    private View mView;

    private VoltageDashboard dashboardMainPlusBefore, dashboardMainPlusAfter, dashboardMainMinusAfter;
    private VoltageDashboard dashboardPackageVoltage, dashboardPackageCurrent;
    private VerticalRollingBar Module_Temperature_1, Module_Temperature_2, Module_Temperature_3
            ,Module_Temperature_4, Module_Temperature_5, Module_Temperature_6, Module_Temperature_7;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_vcu_bms, container, false);

        dashboardMainPlusBefore = (VoltageDashboard) mView.findViewById(R.id.dashboard_bms_main_jdq_front);
        dashboardMainPlusAfter=(VoltageDashboard) mView.findViewById(R.id.dashboard_bms_main_jdq_behind);
        dashboardMainMinusAfter=(VoltageDashboard) mView.findViewById(R.id.dashboard_bms_minus_jdq_behind);

        dashboardPackageVoltage = (VoltageDashboard) mView.findViewById(R.id.dashboard_bms_battery_voltage);
        dashboardPackageCurrent = (VoltageDashboard) mView.findViewById(R.id.dashboard_bms_battery_current);
        dashboardPackageCurrent.setMaxValue(60.f);

        Module_Temperature_1 = (VerticalRollingBar) mView.findViewById(R.id.vrb_vcu_bms_temperature_1);
        Module_Temperature_2 = (VerticalRollingBar) mView.findViewById(R.id.vrb_vcu_bms_temperature_2);
        Module_Temperature_3 = (VerticalRollingBar) mView.findViewById(R.id.vrb_vcu_bms_temperature_3);
        Module_Temperature_4 = (VerticalRollingBar) mView.findViewById(R.id.vrb_vcu_bms_temperature_4);
        Module_Temperature_5 = (VerticalRollingBar) mView.findViewById(R.id.vrb_vcu_bms_temperature_5);
        Module_Temperature_6 = (VerticalRollingBar) mView.findViewById(R.id.vrb_vcu_bms_temperature_6);
        Module_Temperature_7 = (VerticalRollingBar) mView.findViewById(R.id.vrb_vcu_bms_temperature_7);

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
            ServiceManager.getInstance().sendCommandToCar(new CMDVCUBMS1(), new CommandListenerAdapter<CMDVCUBMS1.Response>(){
                @Override
                public void onSuccess(CMDVCUBMS1.Response response) {
                    super.onSuccess(response);
                    final CMDVCUBMS1.Response r = response;
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                dashboardMainPlusBefore.setValue( r.U_HighVoltage_1);
                                dashboardMainPlusAfter.setValue( r.U_HighVoltage_2);
                                dashboardMainMinusAfter.setValue( r.U_HighVoltage_3);
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

                }
            });

            ServiceManager.getInstance().sendCommandToCar(new CMDVCUBMS3(), new CommandListenerAdapter(){
                @Override
                public void onSuccess(BaseResponse response) {
                    super.onSuccess(response);
                    final CMDVCUBMS3.Response r = (CMDVCUBMS3.Response)response;
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((TextView)mView.findViewById(R.id.tv_bms_cell_voltage_1)).setText(r.Cell_1_Voltage + "V");
                                ((TextView)mView.findViewById(R.id.tv_bms_cell_voltage_2)).setText(r.Cell_2_Voltage + "V");
                                ((TextView)mView.findViewById(R.id.tv_bms_cell_voltage_3)).setText(r.Cell_3_Voltage + "V");
                                ((TextView)mView.findViewById(R.id.tv_bms_cell_voltage_4)).setText(r.Cell_4_Voltage + "V");
                                ((TextView)mView.findViewById(R.id.tv_bms_cell_voltage_5)).setText(r.Cell_5_Voltage + "V");
                                ((TextView)mView.findViewById(R.id.tv_bms_cell_voltage_6)).setText(r.Cell_6_Voltage + "V");
                                ((TextView)mView.findViewById(R.id.tv_bms_cell_voltage_7)).setText(r.Cell_7_Voltage + "V");
                                ((TextView)mView.findViewById(R.id.tv_bms_cell_voltage_8)).setText(r.Cell_8_Voltage + "V");
                                ((TextView)mView.findViewById(R.id.tv_bms_cell_voltage_9)).setText(r.Cell_9_Voltage + "V");
                                ((TextView)mView.findViewById(R.id.tv_bms_cell_voltage_10)).setText(r.Cell_10_Voltage + "V");
                                ((TextView)mView.findViewById(R.id.tv_bms_cell_voltage_11)).setText(r.Cell_11_Voltage + "V");
                                ((TextView)mView.findViewById(R.id.tv_bms_cell_voltage_12)).setText(r.Cell_12_Voltage + "V");
                                ((TextView)mView.findViewById(R.id.tv_bms_cell_voltage_13)).setText(r.Cell_13_Voltage + "V");
                                ((TextView)mView.findViewById(R.id.tv_bms_cell_voltage_14)).setText(r.Cell_14_Voltage + "V");

                                updateCellBalance(r.CellBalance_Status_1, R.id.iv_bms_cell_balance_1);
                                updateCellBalance(r.CellBalance_Status_2, R.id.iv_bms_cell_balance_2);
                                updateCellBalance(r.CellBalance_Status_3, R.id.iv_bms_cell_balance_3);
                                updateCellBalance(r.CellBalance_Status_4, R.id.iv_bms_cell_balance_4);
                                updateCellBalance(r.CellBalance_Status_5, R.id.iv_bms_cell_balance_5);
                                updateCellBalance(r.CellBalance_Status_6, R.id.iv_bms_cell_balance_6);
                                updateCellBalance(r.CellBalance_Status_7, R.id.iv_bms_cell_balance_7);
                                updateCellBalance(r.CellBalance_Status_8, R.id.iv_bms_cell_balance_8);
                                updateCellBalance(r.CellBalance_Status_9, R.id.iv_bms_cell_balance_9);
                                updateCellBalance(r.CellBalance_Status_10, R.id.iv_bms_cell_balance_10);
                                updateCellBalance(r.CellBalance_Status_11, R.id.iv_bms_cell_balance_11);
                                updateCellBalance(r.CellBalance_Status_12, R.id.iv_bms_cell_balance_12);
                                updateCellBalance(r.CellBalance_Status_13, R.id.iv_bms_cell_balance_13);
                                updateCellBalance(r.CellBalance_Status_14, R.id.iv_bms_cell_balance_14);

                            }
                        });
                    }
                }
            });

            ServiceManager.getInstance().sendCommandToCar(new CMDVCUBMS4(), new CommandListenerAdapter<CMDVCUBMS4.Response>(){
                @Override
                public void onSuccess(final CMDVCUBMS4.Response response) {
                    super.onSuccess(response);

                    Module_Temperature_1.setValue(response.Module_Temperature_1);
                    Module_Temperature_2.setValue(response.Module_Temperature_2);
                    Module_Temperature_3.setValue(response.Module_Temperature_3);
                    Module_Temperature_4.setValue(response.Module_Temperature_4);
                    Module_Temperature_5.setValue(response.Module_Temperature_5);
                    Module_Temperature_6.setValue(response.Module_Temperature_6);
                    Module_Temperature_7.setValue(response.Module_Temperature_7);

                    dashboardPackageVoltage.setValue(((CMDVCUBMS4.Response) response).Pack_Voltage);
                    dashboardPackageCurrent.setValue(((CMDVCUBMS4.Response) response).Pack_Current);
                }
            });



            handler.removeCallbacks(this); //移除定时任务
            handler.postDelayed(runnable, 1000);

        }
    };

    private void updateCellBalance(int balance, int ResID){
        if (balance == 0){
            ((ImageView)mView.findViewById(ResID)).setImageResource(R.mipmap.ic_bms_balance_red);
        } else {
            ((ImageView)mView.findViewById(ResID)).setImageResource(R.mipmap.ic_bms_balance_green);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}

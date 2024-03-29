package com.longkai.stcarcontrol.st_exp.fragment;

import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.longkai.stcarcontrol.st_exp.R;
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUBMS1List.CMDVCUBMS1;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUBMS3List.CMDVCUBMS3;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUBMS4List.CMDVCUBMS4;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUBMS5List.CMDVCUBMS5;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter;
import com.longkai.stcarcontrol.st_exp.customView.VerticalRollingBar;
import com.longkai.stcarcontrol.st_exp.customView.dashboard.VoltageDashboard;
import com.longkai.stcarcontrol.st_exp.mockMessage.MockMessageServiceImpl;

import java.text.DecimalFormat;

/**
 * Created by Administrator on 2018/5/20.
 */

public class VCUBMSFragment extends Fragment implements View.OnClickListener {
    private View mView;

    private VoltageDashboard dashboardMainPlusBefore, dashboardMainPlusAfter, dashboardMainMinusAfter;
    private VoltageDashboard dashboardPackageVoltage, dashboardPackageCurrent;
    private VerticalRollingBar Module_Temperature_1, Module_Temperature_2, Module_Temperature_3
            ,Module_Temperature_4, Module_Temperature_5, Module_Temperature_6, Module_Temperature_7;

    private ImageView ivPowerRemain;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_vcu_bms, container, false);

        ivPowerRemain = (ImageView) mView.findViewById (R.id.iv_vcu_bms_power_remain);

        dashboardMainPlusBefore = (VoltageDashboard) mView.findViewById(R.id.dashboard_bms_main_jdq_front);
        dashboardMainPlusBefore.setMaxValue(80.f);
        dashboardMainPlusAfter=(VoltageDashboard) mView.findViewById(R.id.dashboard_bms_main_jdq_behind);
        dashboardMainPlusAfter.setMaxValue(80.f);
        dashboardMainMinusAfter=(VoltageDashboard) mView.findViewById(R.id.dashboard_bms_minus_jdq_behind);
        dashboardMainMinusAfter.setMaxValue(80.f);

        dashboardPackageVoltage = (VoltageDashboard) mView.findViewById(R.id.dashboard_bms_battery_voltage);
        dashboardPackageVoltage.setMaxValue(80.f);
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

        //test
        MockMessageServiceImpl.getService().StartService(VCUBMSFragment.class.toString());

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
                                DecimalFormat fnum = new DecimalFormat("##0.000");
                                ((TextView)mView.findViewById(R.id.tv_bms_cell_voltage_1)).setText(fnum.format(r.Cell_1_Voltage / 1000.f) + "V");
                                ((TextView)mView.findViewById(R.id.tv_bms_cell_voltage_2)).setText(fnum.format(r.Cell_2_Voltage / 1000.f) + "V");
                                ((TextView)mView.findViewById(R.id.tv_bms_cell_voltage_3)).setText(fnum.format(r.Cell_3_Voltage / 1000.f) + "V");
                                ((TextView)mView.findViewById(R.id.tv_bms_cell_voltage_4)).setText(fnum.format(r.Cell_4_Voltage / 1000.f) + "V");
                                ((TextView)mView.findViewById(R.id.tv_bms_cell_voltage_5)).setText(fnum.format(r.Cell_5_Voltage / 1000.f) + "V");
                                ((TextView)mView.findViewById(R.id.tv_bms_cell_voltage_6)).setText(fnum.format(r.Cell_6_Voltage / 1000.f) + "V");
                                ((TextView)mView.findViewById(R.id.tv_bms_cell_voltage_7)).setText(fnum.format(r.Cell_7_Voltage / 1000.f) + "V");
                                ((TextView)mView.findViewById(R.id.tv_bms_cell_voltage_8)).setText(fnum.format(r.Cell_8_Voltage / 1000.f) + "V");
                                ((TextView)mView.findViewById(R.id.tv_bms_cell_voltage_9)).setText(fnum.format(r.Cell_9_Voltage / 1000.f) + "V");
                                ((TextView)mView.findViewById(R.id.tv_bms_cell_voltage_10)).setText(fnum.format(r.Cell_10_Voltage / 1000.f) + "V");
                                ((TextView)mView.findViewById(R.id.tv_bms_cell_voltage_11)).setText(fnum.format(r.Cell_11_Voltage / 1000.f) + "V");
                                ((TextView)mView.findViewById(R.id.tv_bms_cell_voltage_12)).setText(fnum.format(r.Cell_12_Voltage / 1000.f) + "V");
                                ((TextView)mView.findViewById(R.id.tv_bms_cell_voltage_13)).setText(fnum.format(r.Cell_13_Voltage / 1000.f) + "V");
                                ((TextView)mView.findViewById(R.id.tv_bms_cell_voltage_14)).setText(fnum.format(r.Cell_14_Voltage / 1000.f) + "V");

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

                    dashboardPackageVoltage.setValue(response.Pack_Voltage);
                    dashboardPackageCurrent.setValue(response.Pack_Current);
                }
            });

            ServiceManager.getInstance().sendCommandToCar(new CMDVCUBMS5(), new CommandListenerAdapter<CMDVCUBMS5.Response>(){
                @Override
                public void onSuccess(CMDVCUBMS5.Response response) {
                    super.onSuccess(response);
                    final CMDVCUBMS5.Response r = response;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((TextView) mView.findViewById(R.id.tv_vcu_bms_batteryremain_percent))
                                    .setText((int)(r.SOC) + "%");
                            if (r.SOC > 80) {
                                ivPowerRemain.setImageResource(R.mipmap.ic_bms_power_remain_100);
                            } else if (r.SOC > 60){
                                ivPowerRemain.setImageResource(R.mipmap.ic_bms_power_remain_80);
                            } else if (r.SOC > 40) {
                                ivPowerRemain.setImageResource(R.mipmap.ic_bms_power_remain_60);
                            } else if (r.SOC > 20) {
                                ivPowerRemain.setImageResource(R.mipmap.ic_bms_power_remain_40);
                            } else if (r.SOC > 10) {
                                ivPowerRemain.setImageResource(R.mipmap.ic_bms_power_remain_20);
                            } else {
                                ivPowerRemain.setImageResource(R.mipmap.ic_bms_power_remain_0);
                            }

                        }
                    });

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

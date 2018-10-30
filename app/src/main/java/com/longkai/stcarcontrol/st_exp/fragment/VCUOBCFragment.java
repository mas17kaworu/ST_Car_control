package com.longkai.stcarcontrol.st_exp.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.longkai.stcarcontrol.st_exp.R;
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCU7List.CMDVCU7;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCU7List.CMDVCU7DCDC;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCU7List.CMDVCU7OBCOff;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCU7List.CMDVCU7OBCOn;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter;

/**
 * Created by Administrator on 2018/10/29.
 */

public class VCUOBCFragment extends Fragment implements View.OnClickListener{
    private View mView;
    private ImageView ivDcdcJDQ, ivOBCPJDQ, ivOBCMJDQ;
    private ImageView btnOBC, btnDCDC;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_vcu_obc, container, false);
        ivDcdcJDQ = (ImageView) mView.findViewById(R.id.iv_vcu_obc_jdq_dcdc);
        ivOBCMJDQ = (ImageView) mView.findViewById(R.id.iv_vcu_obc_jdq_obc_m);
        ivOBCPJDQ = (ImageView) mView.findViewById(R.id.iv_vcu_obc_jdq_obc_p);

        btnDCDC = (ImageView) mView.findViewById(R.id.iv_vcu_obc_dcdc_switch);
        btnDCDC.setOnClickListener(this);
        btnOBC = (ImageView) mView.findViewById(R.id.iv_vcu_obc_obc_switch);
        btnOBC.setOnClickListener(this);

        handler.postDelayed(runnable, 500);// 打开定时器，500ms后执行runnable
        return mView;
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            ServiceManager.getInstance().sendCommandToCar(new CMDVCU7(), new CommandListenerAdapter(){
                @Override
                public void onSuccess(BaseResponse response) {
                    super.onSuccess(response);
                    final BaseResponse r = response;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if ( (((CMDVCU7.Response) r).gaoya_jidianqi_status & 0x08) == 0) {
                                ivOBCPJDQ.setImageResource(R.mipmap.ic_vcu_dianlu_jdq_off);
                            } else {
                                ivOBCPJDQ.setImageResource(R.mipmap.ic_vcu_dianlu_jdq_on);
                            }

                            if ( (((CMDVCU7.Response) r).gaoya_jidianqi_status & 0x10) == 0) {
                                ivOBCMJDQ.setImageResource(R.mipmap.ic_vcu_dianlu_jdq_off);
                            } else {
                                ivOBCMJDQ.setImageResource(R.mipmap.ic_vcu_dianlu_jdq_on);
                            }

                            if ( (((CMDVCU7.Response) r).gaoya_jidianqi_status & 0x20) == 0) {
                                ivDcdcJDQ.setImageResource(R.mipmap.ic_vcu_dianlu_jdq_off);
                            } else {
                                ivDcdcJDQ.setImageResource(R.mipmap.ic_vcu_dianlu_jdq_on);
                            }
                        }
                    });

                }
            });

            handler.removeCallbacks(this); //移除定时任务
            handler.postDelayed(runnable, 500);

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_vcu_obc_dcdc_switch:
                CMDVCU7DCDC cmdvcu7DCDC = new CMDVCU7DCDC();
                if (cmdvcu7DCDC.getValue() == 0) {
                    cmdvcu7DCDC.on();
                    btnDCDC.setImageResource(R.mipmap.ic_vcu_obc_on);
                } else {
                    cmdvcu7DCDC.off();
                    btnDCDC.setImageResource(R.mipmap.ic_vcu_obc_off);
                }
                break;
            case R.id.iv_vcu_obc_obc_switch:
                CMDVCU7OBCOn cmdvcu7OBCOn = new CMDVCU7OBCOn();
                CMDVCU7OBCOff cmdvcu7OBCOff = new CMDVCU7OBCOff();
                if (cmdvcu7OBCOn.getValue() == 0) {
                    cmdvcu7OBCOff.off();
                    cmdvcu7OBCOn.on();
                    btnOBC.setImageResource(R.mipmap.ic_vcu_obc_on);
                } else {
                    cmdvcu7OBCOn.off();
                    cmdvcu7OBCOff.on();
                    btnOBC.setImageResource(R.mipmap.ic_vcu_obc_off);
                }
                break;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}

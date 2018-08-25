package com.longkai.stcarcontrol.st_exp.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.longkai.stcarcontrol.st_exp.Enum.BMSMonitorEnum;
import com.longkai.stcarcontrol.st_exp.Interface.StateChange;
import com.longkai.stcarcontrol.st_exp.R;

import java.text.DecimalFormat;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2018/8/19.
 */

public class VCUBMSMonitorFragment extends Fragment implements View.OnClickListener, StateChange<BMSMonitorEnum> {

    private View mView;
    private ImageView ivBack;
    private TextView tvV1, tvV2, tvV3, tvRp, tvRn;
    private volatile Boolean ready = false;
    private BMSMonitorEnum presentState;

    private float randomV1;
    private float randomV2;
    private float randomV3;
    private int randomRp;
    private int randomRn;
    private DecimalFormat decimalFormat;
    private final ScheduledExecutorService threadPool = Executors.newSingleThreadScheduledExecutor();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_vcu_bms_monitor, container, false);
        ivBack = (ImageView) mView.findViewById(R.id.iv_bms_monitor_back);
        tvV1 = (TextView) mView.findViewById(R.id.tv_bms_monitor_voltage1);
        tvV2 = (TextView) mView.findViewById(R.id.tv_bms_monitor_voltage2);
        tvV3 = (TextView) mView.findViewById(R.id.tv_bms_monitor_voltage3);
        tvRn = (TextView) mView.findViewById(R.id.tv_bms_monitor_rn);
        tvRp = (TextView) mView.findViewById(R.id.tv_bms_monitor_rp);
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ready = true;
        threadPool.scheduleAtFixedRate(randomGenerater, 0, 500, TimeUnit.MILLISECONDS);
        decimalFormat=new DecimalFormat("0.00");
        changeTo(presentState); //must behind ready =true


    }

    @Override
    public void onPause() {
        super.onPause();
        threadPool.shutdown();
        ready = false;
    }

    @Override
    public void onClick(View v) {

    }

    public StateChange getController(){
        return this;
    }

    private Handler handler = new Handler();
    @Override
    public void changeTo(BMSMonitorEnum state) {
        Log.i("VCUBMSMonitorFragment", "ready status = " + ready);
        presentState = state;
        if (ready) {
            switch (state) {
                case Insulation:
                    ivBack.setImageResource(R.mipmap.ic_bms_monitor_insullation_1);
                    tvV2.setVisibility(View.INVISIBLE);
                    tvV3.setVisibility(View.INVISIBLE);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ivBack.setImageResource(R.mipmap.ic_bms_monitor_insullation_2);
                        }
                    }, 1000);

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ivBack.setImageResource(R.mipmap.ic_bms_monitor_insullation_3);
                        }
                    }, 2000);

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ivBack.setImageResource(R.mipmap.ic_bms_monitor_insullation_4);
                        }
                    }, 3000);


                    break;
                case Connection:
                    tvV2.setVisibility(View.VISIBLE);
                    tvV3.setVisibility(View.VISIBLE);
                    ivBack.setImageResource(R.mipmap.ic_bms_monitor_connection_1);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ivBack.setImageResource(R.mipmap.ic_bms_monitor_connection_2);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    changeTo(BMSMonitorEnum.Connection);
                                }
                            }, 1000);
                        }
                    }, 1000);

                    break;
                default:
                    break;
            }
        }
    }

    private void UpdateTV(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvV1.setText(decimalFormat.format(48.f+randomV1) + "V");
                tvV2.setText(decimalFormat.format(randomV1) + "V");
                tvV3.setText(decimalFormat.format(5 + randomV1) + "V");
                tvRn.setText((50000 + randomRn) + "Ω");
                tvRp.setText((50000 + randomRp) + "Ω");
            }
        });
    }

    Thread randomGenerater = new Thread(new Runnable() {
        @Override
        public void run() {
            randomV1 = (float)(Math.random()*2 - 1);
            randomV2 = (float)(Math.random()*2 - 1);
            randomV3 = (float)(Math.random()*2 - 1);
            randomRn = (int)(Math.random()*20 - 10);
            randomRp = (int)(Math.random()*20 - 10);
            if (ready) {
                UpdateTV();
            }
        }
    });
}

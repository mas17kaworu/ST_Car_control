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
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter;

/**
 * Created by Administrator on 2018/5/20.
 */

public class VCUChargeFragment extends Fragment implements View.OnClickListener {
    private View mView;
    private ImageView ivChargeState, ivCarLockState;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_vcu_charge, container, false);
        ivChargeState = (ImageView) mView.findViewById(R.id.iv_vcu_charge_charge_status);
        ivCarLockState = (ImageView) mView.findViewById(R.id.iv_vcu_charge_lock_state);
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
                            if ( (((CMDVCU7.Response) r).charging_status & 0x01)== 0) {
                                ivChargeState.setImageResource(R.mipmap.ic_vcu_charge_unplug);
                            } else {
                                ivChargeState.setImageResource(R.mipmap.ic_vcu_charge_plugin);
                            }

                            if ( (((CMDVCU7.Response) r).charging_status &0x10) == 0) {
                                ivCarLockState.setImageResource(R.mipmap.ic_vcu_charge_carunlock);
                            } else {
                                ivCarLockState.setImageResource(R.mipmap.ic_vcu_charge_carlocked);
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

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}

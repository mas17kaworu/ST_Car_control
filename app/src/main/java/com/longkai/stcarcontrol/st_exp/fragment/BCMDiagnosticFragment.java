package com.longkai.stcarcontrol.st_exp.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.longkai.stcarcontrol.st_exp.R;
import com.longkai.stcarcontrol.st_exp.Utils.ByteUtils;
import com.longkai.stcarcontrol.st_exp.activity.MainActivity;
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDBCMRearLampList.CMDBCMRearLamp;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPLGMList.CMDPLGM;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter;

/**
 * Created by Administrator on 2017/9/12.
 */

public class BCMDiagnosticFragment extends Fragment implements View.OnClickListener{
    private final String Tag = "BCMDiagnosticFragment";
    private View mView;

    public int[] openLoadArray = new int[17]; //   7       6           5           4           3           2            1          0
    //"U17_CH0"	"U13_CH0"	"U12_CH0"	"U10_CH0"	"U9_CH0"	"U8_CH0"	"U7_CH0"	"U5_CH0"
    //"U17_CH1"	"U13_CH1"	"U12_CH1"	"U10_CH1"	"U9_CH1"	"U8_CH1"	"U7_CH1"	"U5_CH0"
    //"U11_CH0"
    public int[] overLoadArray = new int[17];
    public int[] tempretureArray = new int[9];

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_diagnostic_bcm, container, false);

        mView.findViewById(R.id.iv_diagnostic_back).setOnClickListener(this);

        handler.postDelayed(runnable, 500);// 打开定时器，50ms后执行runnable

        return mView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_diagnostic_back:
                ((MainActivity)getActivity()).setSelect(5);
                break;

        }
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable(){
        @Override
        public void run() {
            ServiceManager.getInstance().sendCommandToCar(new CMDBCMRearLamp(true),new CommandListenerAdapter(){
                @Override
                public void onSuccess(BaseResponse response) {
                    super.onSuccess(response);
                    tempretureArray = ((CMDBCMRearLamp.Response)response).tempreture;
                    overLoadArray = ((CMDBCMRearLamp.Response)response).overLoad;
                    openLoadArray = ((CMDBCMRearLamp.Response)response).openLoad;
//                    Toast.makeText(getActivity(), )
                }
            });
            handler.removeCallbacks(this); //移除定时任务
            handler.postDelayed(runnable, 1100);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(Tag, "onDestroy");
        handler.removeCallbacks(runnable);
    }
}

package com.longkai.stcarcontrol.st_exp.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.longkai.stcarcontrol.st_exp.R;
import com.longkai.stcarcontrol.st_exp.Utils.ByteUtils;
import com.longkai.stcarcontrol.st_exp.activity.MainActivity;
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDBCMRearLampList.CMDBCMRearLamp;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPLGMList.CMDPLGM;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter;
import com.longkai.stcarcontrol.st_exp.customView.CoverHeatView;

/**
 * Created by Administrator on 2017/9/12.
 */

public class BCMDiagnosticFragment extends Fragment implements View.OnClickListener{
    private final String Tag = "BCMDiagnosticFragment";
    private View mView;

    public int[] openLoadArray = new int[17];
    //   7       6           5           4           3           2            1          0
    //"U17_CH0"	"U13_CH0"	"U12_CH0"	"U10_CH0"	"U9_CH0"	"U8_CH0"	"U7_CH0"	"U5_CH0"
    //"U17_CH1"	"U13_CH1"	"U12_CH1"	"U10_CH1"	"U9_CH1"	"U8_CH1"	"U7_CH1"	"U5_CH0"
    //"U11_CH0"
    public int[] overLoadArray = new int[17];

//    U[5,7,8,9,10,12,13,17,11]
    public int[] tempretureArray = new int[9];

    private float[] realTempreturArray = new float[9];

    private CoverHeatView mCoverHeatView;
    private TextView temptext;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_diagnostic_bcm, container, false);
        mView.findViewById(R.id.iv_diagnostic_back).setOnClickListener(this);

        mCoverHeatView = (CoverHeatView)mView.findViewById(R.id.temperature_indicator);
        temptext = (TextView) mView.findViewById(R.id.tv_diagnostic_temp_show);

        handler.postDelayed(runnable, 500);// 打开定时器，500ms后执行runnable

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
                    caculateRealTemp();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshText();
                            mCoverHeatView.setTempAndRefresh(realTempreturArray);
                        }
                    });

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

    private void caculateRealTemp(){
        Log.d("Diagnostic", "tempreture[0] = " + tempretureArray[0]);
        realTempreturArray[0] = ((2.7051f - (float)(5 * tempretureArray[0])/1024)*1000.0f/5.5f+33.3f);//u5
        realTempreturArray[1] = ((2.0312f - (float)(5 * tempretureArray[1])/1024)*1000.0f/5.5f+33.3f);//u7
        realTempreturArray[2] = ((2.0654f - (float)(5 * tempretureArray[2])/1024)*1000.0f/5.5f+30.6f);//u8
        realTempreturArray[3] = ((2.6709f - (float)(5 * tempretureArray[3])/1024)*1000.0f/5.5f+30.8f);//9
        realTempreturArray[4] = ((2.7149f - (float)(5 * tempretureArray[4])/1024)*1000.0f/5.5f+32.2f);//10
        realTempreturArray[5] = ((2.0508f - (float)(5 * tempretureArray[5])/1024)*1000.0f/5.5f+32.0f);//12
        realTempreturArray[6] = ((2.0508f - (float)(5 * tempretureArray[6])/1024)*1000.0f/5.5f+30.6f);//13
        realTempreturArray[7] = ((2.6709f - (float)(5 * tempretureArray[7])/1024)*1000.0f/5.5f+30.8f);//17
        realTempreturArray[8] = ((2.7051f - (float)(5 * tempretureArray[8])/1024)*1000.0f/5.5f+33.3f);//11

    }

    private void refreshText(){
        StringBuilder sb = new StringBuilder(100);
        for (float i:realTempreturArray
             ) {
            sb.append(String.valueOf(i));
            sb.append(" ");
        }
        temptext.setText(sb.toString());
        temptext.invalidate();
    }
}

package com.longkai.stcarcontrol.st_exp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.longkai.stcarcontrol.st_exp.R;
import com.longkai.stcarcontrol.st_exp.activity.MainActivity;
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList.CMDLEDHeadLampHBLEDR1;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList.CMDLEDHeadLampHBLEDR2;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList.CMDLEDHeadLampHBLEDR3;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList.CMDLEDHeadLampHBLEDR4;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList.CMDLEDHeadLampHBLEDR5;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList.CMDLEDHeadLampHBLEDR6;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList.CMDLEDHeadLampHBLEDR7;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList.CMDLEDHeadLampHBLEDR8;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList.CMDLEDHeadLampLowBeam1;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList.CMDLEDHeadLampLowBeam2;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter;

/**
 * Created by Administrator on 2017/9/28.
 */

public class FrontHeadLampTest extends Fragment implements View.OnClickListener {

    private View mView;
    private int[] mLampStateRecord = new int[20];

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_front_lamp_test, container, false);
        mView.findViewById(R.id.btn_high_beam_1).setOnClickListener(this);
        mView.findViewById(R.id.btn_high_beam_2).setOnClickListener(this);
        mView.findViewById(R.id.btn_high_beam_3).setOnClickListener(this);
        mView.findViewById(R.id.btn_high_beam_4).setOnClickListener(this);
        mView.findViewById(R.id.btn_high_beam_5).setOnClickListener(this);
        mView.findViewById(R.id.btn_high_beam_6).setOnClickListener(this);
        mView.findViewById(R.id.btn_high_beam_7).setOnClickListener(this);
        mView.findViewById(R.id.btn_high_beam_8).setOnClickListener(this);
        mView.findViewById(R.id.btn_low_beam_1).setOnClickListener(this);
        mView.findViewById(R.id.btn_low_beam_2).setOnClickListener(this);
        mView.findViewById(R.id.iv_front_lamp_test_back).setOnClickListener(this);

        return mView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_high_beam_1:
                clickBtn(new CMDLEDHeadLampHBLEDR1(), 1);
                break;
            case R.id.btn_high_beam_2:
                clickBtn(new CMDLEDHeadLampHBLEDR2(), 2);
                break;
            case R.id.btn_high_beam_3:
                clickBtn(new CMDLEDHeadLampHBLEDR3(), 3);
                break;
            case R.id.btn_high_beam_4:
                clickBtn(new CMDLEDHeadLampHBLEDR4(), 4);
                break;
            case R.id.btn_high_beam_5:
                clickBtn(new CMDLEDHeadLampHBLEDR5(), 5);
                break;
            case R.id.btn_high_beam_6:
                clickBtn(new CMDLEDHeadLampHBLEDR6(), 6);
                break;
            case R.id.btn_high_beam_7:
                clickBtn(new CMDLEDHeadLampHBLEDR7(), 7);
                break;
            case R.id.btn_high_beam_8:
                clickBtn(new CMDLEDHeadLampHBLEDR8(), 8);
                break;
            case R.id.btn_low_beam_1:
                clickBtn(new CMDLEDHeadLampLowBeam1(), 9);
                break;
            case R.id.btn_low_beam_2:
                clickBtn(new CMDLEDHeadLampLowBeam2(), 10);
                break;
            case R.id.iv_front_lamp_test_back:
                ((MainActivity)getActivity()).setSelect(1);
                break;
        }
    }

    private void clickBtn(BaseCommand command, int index){
        if (mLampStateRecord[index] == 0){
            command.turnOn();
            mLampStateRecord[index] = 1;
        } else {
            command.turnOff();
            mLampStateRecord[index] = 0;
        }
        ServiceManager.getInstance().sendCommandToCar(command, new CommandListenerAdapter());
    }
}

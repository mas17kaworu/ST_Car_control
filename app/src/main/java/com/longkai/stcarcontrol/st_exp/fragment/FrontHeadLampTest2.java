package com.longkai.stcarcontrol.st_exp.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.longkai.stcarcontrol.st_exp.R;
import com.longkai.stcarcontrol.st_exp.activity.MainActivity;
import com.longkai.stcarcontrol.st_exp.communication.Command;
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList.CMDLEDHeadLampAutoCon;
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
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList.CMDLEDHeadLampRightCorner;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter;

/**
 * Created by Administrator on 2017/9/28.
 */

public class FrontHeadLampTest2 extends Fragment implements View.OnClickListener {

    private View mView;
    private ImageView mAutoPlay;
    private int[] mLampStateRecord = new int[20];
    private int status=0;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_front_lamp_test2, container, false);
        mView.findViewById(R.id.iv_testlamp_front_yuan1_click).setOnClickListener(this);
        mView.findViewById(R.id.iv_testlamp_front_yuan2_click).setOnClickListener(this);
        mView.findViewById(R.id.iv_testlamp_front_yuan3_click).setOnClickListener(this);
        mView.findViewById(R.id.iv_testlamp_front_yuan4_click).setOnClickListener(this);
        mView.findViewById(R.id.iv_testlamp_front_yuan5_click).setOnClickListener(this);
        mView.findViewById(R.id.iv_testlamp_front_yuan6_click).setOnClickListener(this);
        mView.findViewById(R.id.iv_testlamp_front_yuan7_click).setOnClickListener(this);
        mView.findViewById(R.id.iv_testlamp_front_yuan8_click).setOnClickListener(this);
        mView.findViewById(R.id.iv_testlamp_front_jin1_click).setOnClickListener(this);
        mView.findViewById(R.id.iv_testlamp_front_jin2_click).setOnClickListener(this);
        mView.findViewById(R.id.iv_testlamp_front_corner_click).setOnClickListener(this);
        mView.findViewById(R.id.iv_front_lamp_test_back).setOnClickListener(this);
        mAutoPlay =(ImageView) mView.findViewById(R.id.iv_testlamp_auto_display);
        mAutoPlay.setOnClickListener(this);
        return mView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_testlamp_front_yuan1_click:
                clickBtn(new CMDLEDHeadLampHBLEDR1(), 1, R.id.iv_testlamp_front_yuan1);
                break;
            case R.id.iv_testlamp_front_yuan2_click:
                clickBtn(new CMDLEDHeadLampHBLEDR2(), 2,R.id.iv_testlamp_front_yuan2);
                break;
            case R.id.iv_testlamp_front_yuan3_click:
                clickBtn(new CMDLEDHeadLampHBLEDR3(), 3, R.id.iv_testlamp_front_yuan3);
                break;
            case R.id.iv_testlamp_front_yuan4_click:
                clickBtn(new CMDLEDHeadLampHBLEDR4(), 4, R.id.iv_testlamp_front_yuan4);
                break;
            case R.id.iv_testlamp_front_yuan5_click:
                clickBtn(new CMDLEDHeadLampHBLEDR5(), 5, R.id.iv_testlamp_front_yuan5);
                break;
            case R.id.iv_testlamp_front_yuan6_click:
                clickBtn(new CMDLEDHeadLampHBLEDR6(), 6, R.id.iv_testlamp_front_yuan6);
                break;
            case R.id.iv_testlamp_front_yuan7_click:
                clickBtn(new CMDLEDHeadLampHBLEDR7(), 7, R.id.iv_testlamp_front_yuan7);
                break;
            case R.id.iv_testlamp_front_yuan8_click:
                clickBtn(new CMDLEDHeadLampHBLEDR8(), 8, R.id.iv_testlamp_front_yuan8);
                break;
            case R.id.iv_testlamp_front_jin1_click:
                clickBtn(new CMDLEDHeadLampLowBeam1(), 9, R.id.iv_testlamp_front_jin1);
                break;
            case R.id.iv_testlamp_front_jin2_click:
                clickBtn(new CMDLEDHeadLampLowBeam2(), 10, R.id.iv_testlamp_front_jin2);
                break;
            case R.id.iv_testlamp_front_corner_click:
                clickBtn(new CMDLEDHeadLampRightCorner(), 11, R.id.iv_testlamp_front_corner);
                break;
            case R.id.iv_front_lamp_test_back:
                ((MainActivity)getActivity()).setSelect(1);
                break;
            case R.id.iv_testlamp_auto_display:
                Command cmd = new CMDLEDHeadLampAutoCon();
                if (status==0){
                    status = 1;
                    mAutoPlay.setImageResource(R.mipmap.ic_homepage_auto_display_c);
                    ((CMDLEDHeadLampAutoCon)cmd).turnOn();
                    ServiceManager.getInstance().sendCommandToCar(cmd, new CommandListenerAdapter());
                }else {
                    status = 0;
                    mAutoPlay.setImageResource(R.mipmap.ic_homepage_auto_display_u);
                    ((CMDLEDHeadLampAutoCon)cmd).turnOff();
                    ServiceManager.getInstance().sendCommandToCar(cmd, new CommandListenerAdapter());
                }
                break;
        }
    }

    private void clickBtn(BaseCommand command, int index, int resID){
        if (mLampStateRecord[index] == 0){
            command.turnOn();
            mLampStateRecord[index] = 1;
            mView.findViewById(resID).setVisibility(View.VISIBLE);
        } else {
            command.turnOff();
            mLampStateRecord[index] = 0;
            mView.findViewById(resID).setVisibility(View.INVISIBLE);
        }
        ServiceManager.getInstance().sendCommandToCar(command, new CommandListenerAdapter());
    }
}

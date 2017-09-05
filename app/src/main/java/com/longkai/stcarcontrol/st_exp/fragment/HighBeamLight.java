package com.longkai.stcarcontrol.st_exp.fragment;

import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.longkai.stcarcontrol.st_exp.ConstantData;
import com.longkai.stcarcontrol.st_exp.R;
import com.longkai.stcarcontrol.st_exp.activity.MainActivity;
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList.CMDLEDHeadLampCountry;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList.CMDLEDHeadLampCurve;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList.CMDLEDHeadLampEnergySave;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList.CMDLEDHeadLampHighway;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList.CMDLEDHeadLampParking;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList.CMDLEDHeadLampUrban;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Administrator on 2017/7/10.
 */

public class HighBeamLight extends Fragment implements View.OnClickListener{
    private View mView;

    RadioButton rdoUrban, rdoHighway, rdoCountry, rdoCurve, rdoParking, rdoEnergySaving;
    private GifImageView gif_view_high_beam;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_hight_beam_light, container, false);

        gif_view_high_beam = (GifImageView) mView.findViewById(R.id.gifv_high_beam);
        rdoUrban = (RadioButton) mView.findViewById(R.id.rdoBtn_high_beam_urban);
        rdoUrban.setOnClickListener(this);
        rdoHighway = (RadioButton) mView.findViewById(R.id.rdoBtn_high_beam_highway);
        rdoHighway.setOnClickListener(this);
        rdoCountry = (RadioButton) mView.findViewById(R.id.rdoBtn_high_beam_country);
        rdoCountry.setOnClickListener(this);
        rdoCurve = (RadioButton) mView.findViewById(R.id.rdoBtn_high_beam_curve);
        rdoCurve.setOnClickListener(this);
        rdoParking = (RadioButton) mView.findViewById(R.id.rdoBtn_high_beam_parking);
        rdoParking.setOnClickListener(this);
        rdoEnergySaving = (RadioButton) mView.findViewById(R.id.rdoBtn_high_beam_energy_saving);
        rdoEnergySaving.setOnClickListener(this);
        mView.findViewById(R.id.iv_high_beam_back).setOnClickListener(this);


        return mView;
    }

    private int highBeamStatus = 0;
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rdoBtn_high_beam_urban:
                //new command 的时候已经自动清零
                clickTask(rdoUrban,1,R.mipmap.gif_high_beam_urban, new CMDLEDHeadLampUrban());
                break;
            case R.id.rdoBtn_high_beam_highway:
                clickTask(rdoHighway,2,R.mipmap.gif_high_beam_highway, new CMDLEDHeadLampHighway());
                break;
            case R.id.rdoBtn_high_beam_country:
                clickTask(rdoCountry,3,R.mipmap.gif_high_beam_country, new CMDLEDHeadLampCountry());
                break;
            case R.id.rdoBtn_high_beam_curve:
                clickTask(rdoCurve,4,R.mipmap.gif_high_beam_curve, new CMDLEDHeadLampCurve());
                break;
            case R.id.rdoBtn_high_beam_parking:
                clickTask(rdoParking,5,R.mipmap.gif_high_beam_park, new CMDLEDHeadLampParking());
                break;
            case R.id.rdoBtn_high_beam_energy_saving:
                clickTask(rdoEnergySaving,6,R.mipmap.gif_high_beam_energy_saving,new CMDLEDHeadLampEnergySave());
                break;
            case R.id.iv_high_beam_back:
                ((MainActivity)getActivity()).setSelect(1);
                break;
        }
    }

    private void clickTask(RadioButton rb, int num, int gifResId, BaseCommand command){
        if (highBeamStatus == num) {
            rb.setChecked(false);
            highBeamStatus = 0;
            releaseGifView();
            //send command
            command.turnOff();
            ServiceManager.getInstance().sendCommandToCar(command,new CommandListenerAdapter(){
                @Override
                public void onSuccess(BaseResponse response) {
                    super.onSuccess(response);
                    Log.i("HighBeamLight","onSuccess");
                }

                @Override
                public void onTimeout() {
                    super.onTimeout();
                    Log.i("HighBeamLight","onTimeout");
                }
            });
        } else {
            rb.setChecked(true);
            highBeamStatus = num;
            loadGif(gifResId);

            command.turnOn();
            ServiceManager.getInstance().sendCommandToCar(command,new CommandListenerAdapter(){
                @Override
                public void onSuccess(BaseResponse response) {
                    super.onSuccess(response);
                    Log.i("HighBeamLight","onSuccess");
                }

                @Override
                public void onTimeout() {
                    super.onTimeout();
                    Log.i("HighBeamLight","onTimeout");
                }
            });
        }
    }

    private void loadGif(int resID){
        try {
            // 如果加载的是gif动图，第一步需要先将gif动图资源转化为GifDrawable
            // 将gif图资源转化为GifDrawable
            GifDrawable gifDrawable = new GifDrawable(getResources(), resID);

            // gif1加载一个动态图gif
            gif_view_high_beam.setImageDrawable(gifDrawable);

            // 如果是普通的图片资源，就像Android的ImageView set图片资源一样简单设置进去即可。
            // gif2加载一个普通的图片（如png，bmp，jpeg等等）
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void releaseGifView(){
        try {
            gif_view_high_beam.setImageDrawable(null);
            gif_view_high_beam.setImageResource(R.mipmap.ic_highbeam_car);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

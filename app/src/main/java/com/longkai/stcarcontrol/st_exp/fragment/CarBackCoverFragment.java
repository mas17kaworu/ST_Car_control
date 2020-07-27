package com.longkai.stcarcontrol.st_exp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.longkai.stcarcontrol.st_exp.ConstantData;
import com.longkai.stcarcontrol.st_exp.R;
import com.longkai.stcarcontrol.st_exp.activity.MainActivity;
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPLGMList.CMDPLGMTrunkDownOff;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPLGMList.CMDPLGMTrunkDownOn;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPLGMList.CMDPLGMTrunkUpOff;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPLGMList.CMDPLGMTrunkUpOn;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter;
import com.longkai.stcarcontrol.st_exp.customView.MenuViewItem;

import java.util.Timer;
import java.util.TimerTask;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 *
 *
 * Created by Administrator on 2017/9/9.
 */

public class CarBackCoverFragment extends Fragment implements View.OnClickListener {

    private View mView;

    private GifImageView gif_view_back;
    private ImageView ivTrunkOpen, ivTrunkClose;

    private Timer timer = new Timer();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_car_back_trunk, container, false);
        gif_view_back = (GifImageView) mView.findViewById(R.id.gifv_car_trunk);
        ivTrunkOpen = (ImageView) mView.findViewById(R.id.iv_carback_trunk_open);

        ivTrunkOpen.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        ivTrunkOpen.setImageResource(R.mipmap.ic_car_back_btn_trunk_open_green);
                        ServiceManager.getInstance().sendCommandToCar(new CMDPLGMTrunkUpOn(),
                                new CommandListenerAdapter());
                        break;
                    case MotionEvent.ACTION_UP:
                        ivTrunkOpen.setImageResource(R.mipmap.ic_car_back_btn_trunk_open_gray);
                        ServiceManager.getInstance().sendCommandToCar(new CMDPLGMTrunkUpOff(),
                                new CommandListenerAdapter());
                        ConstantData.sTrunkStatus[ConstantData.sTrunkStatu] = 1;
                        loadGif(R.mipmap.gif_car_back_trunk_open);
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                              if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                  @Override
                                  public void run() {
                                    ivTrunkOpen.setImageResource(
                                        R.mipmap.ic_car_back_btn_trunk_open_gray);
                                    setGif_Image(R.mipmap.ic_car_back_trunk_open);
                                  }
                                });
                              }
                            }
                        },2000);
                        break;
                }
                return true;
            }
        });

//        ivTrunkOpen.setOnClickListener(this);

        ivTrunkClose = (ImageView) mView.findViewById(R.id.iv_carback_trunk_close);
        ivTrunkClose.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        ivTrunkClose.setImageResource(R.mipmap.ic_car_back_btn_trunk_close_green);
                        ServiceManager.getInstance().sendCommandToCar(new CMDPLGMTrunkDownOn(), new CommandListenerAdapter());
                        break;
                    case MotionEvent.ACTION_UP:
                        ivTrunkClose.setImageResource(R.mipmap.ic_car_back_btn_trunk_close_gray);
                        ServiceManager.getInstance().sendCommandToCar(new CMDPLGMTrunkDownOff(), new CommandListenerAdapter());
                        loadGif(R.mipmap.gif_car_back_trunk_close);
                        ConstantData.sTrunkStatus[ConstantData.sTrunkStatu] = 0;
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                              if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                  @Override
                                  public void run() {
                                    ivTrunkClose.setImageResource(
                                        R.mipmap.ic_car_back_btn_trunk_close_gray);
                                    setGif_Image(R.mipmap.ic_car_back_trunk_close);
                                  }
                                });
                              }
                            }
                        },2000);
                        break;
                }
                return true;
            }
        });

//        ivTrunkClose.setOnClickListener(this);

        mView.findViewById(R.id.tv_trunk_diagram).setOnClickListener(this);
        refreshUI();
        return mView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_carback_trunk_open:
                ivTrunkOpen.setImageResource(R.mipmap.ic_car_back_btn_trunk_open_green);
                ServiceManager.getInstance().sendCommandToCar(new CMDPLGMTrunkUpOn(), new CommandListenerAdapter());
                loadGif(R.mipmap.gif_car_back_trunk_open);
                ConstantData.sTrunkStatus[ConstantData.sTrunkStatu] = 1;

                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        ServiceManager.getInstance().sendCommandToCar(new CMDPLGMTrunkUpOff(), new CommandListenerAdapter());
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ivTrunkOpen.setImageResource(R.mipmap.ic_car_back_btn_trunk_open_gray);
                                setGif_Image(R.mipmap.ic_car_back_trunk_open);
                            }
                        });
                    }
                },2000);
                break;
            case R.id.iv_carback_trunk_close:
                ivTrunkClose.setImageResource(R.mipmap.ic_car_back_btn_trunk_close_green);
                ServiceManager.getInstance().sendCommandToCar(new CMDPLGMTrunkDownOn(), new CommandListenerAdapter());
                loadGif(R.mipmap.gif_car_back_trunk_close);
                ConstantData.sTrunkStatus[ConstantData.sTrunkStatu] = 0;
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        ServiceManager.getInstance().sendCommandToCar(new CMDPLGMTrunkDownOff(), new CommandListenerAdapter());

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ivTrunkClose.setImageResource(R.mipmap.ic_car_back_btn_trunk_close_gray);
                                setGif_Image(R.mipmap.ic_car_back_trunk_close);
                            }
                        });
                    }
                },2000);
                break;
            case R.id.tv_trunk_diagram:
                ((MainActivity)getActivity()).showDiagram(ConstantData.PLCM_DIAGRAM);
                break;
        }
    }

    private void loadGif(int resID){
        try {
            // 如果加载的是gif动图，第一步需要先将gif动图资源转化为GifDrawable
            // 将gif图资源转化为GifDrawable
            GifDrawable gifDrawable = new GifDrawable(getResources(), resID);

            // gif1加载一个动态图gif
            gif_view_back.setImageDrawable(gifDrawable);

            // 如果是普通的图片资源，就像Android的ImageView set图片资源一样简单设置进去即可。
            // gif2加载一个普通的图片（如png，bmp，jpeg等等）
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setGif_Image(int resID){
        try {
            gif_view_back.setImageDrawable(null);
            gif_view_back.setImageResource(resID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshUI(){
        if (ConstantData.sTrunkStatus[ConstantData.sTrunkStatu] == 0){
            setGif_Image(R.mipmap.ic_car_back_trunk_close);
        } else if (ConstantData.sTrunkStatus[ConstantData.sTrunkStatu] == 1){
            setGif_Image(R.mipmap.ic_car_back_trunk_open);
        }
    }
}

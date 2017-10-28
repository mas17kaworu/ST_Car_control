package com.longkai.stcarcontrol.st_exp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.longkai.stcarcontrol.st_exp.ConstantData;
import com.longkai.stcarcontrol.st_exp.R;
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList.CMDPowerSeatBackrestForwardOn;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList.CMDPowerSeatHeatCodeSet;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList.CMDPowerSeatSearFormerDownOff;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList.CMDPowerSeatSearFormerDownOn;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList.CMDPowerSeatSeatBackwardOff;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList.CMDPowerSeatSeatBackwardOn;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList.CMDPowerSeatSeatFormerUpOff;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList.CMDPowerSeatSeatFormerUpOn;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList.CMDPowerSeatSeatFowardOff;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList.CMDPowerSeatSeatFowardOn;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList.CMDPowerSeatSeatPitchDownOff;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList.CMDPowerSeatSeatPitchDownOn;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList.CMDPowerSeatSeatPitchUpOff;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList.CMDPowerSeatSeatPitchUpOn;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList.CMDPowerSeatVentilationLevelSet;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList.CMDPowerSeatVentilationOff;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList.CMDPowerSeatVentilationOn;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList.CMDPowerSeatWaistBackwardOff;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList.CMDPowerSeatWaistBackwardOn;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList.CMDPowerSeatWaistDownOff;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList.CMDPowerSeatWaistDownOn;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList.CMDPowerSeatWaistOrwardOff;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList.CMDPowerSeatWaistOrwardOn;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList.CMDPowerSeatWaistUpOff;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList.CMDPowerSeatWaistUpOn;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter;
import com.longkai.stcarcontrol.st_exp.customView.MenuViewItem;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 *
 *
 * Created by Administrator on 2017/7/10.
 */

public class SeatFragment extends Fragment implements View.OnClickListener,View.OnTouchListener{
    private View mView;
    private ImageView iv_seat_heat;
    private ImageView iv_seat_heat_stage_1,iv_seat_heat_stage_2,iv_seat_heat_stage_3;

    private View v_seat_wind_group,v_seat_set_group;
    private RadioButton rbtn_seat_wind_stage1,rbtn_seat_wind_stage2,rbtn_seat_wind_stage3;
    private RadioButton rbtn_seat_set_stage1,rbtn_seat_set_stage2,rbtn_seat_set_stage3;
    private ImageView iv_seat_wind;
    private ImageView iv_seat_set;

    private GifImageView gif_view_seat;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_seat, container, false);
        mView.findViewById( R.id.seat_back_support_up).setOnClickListener(this);
        mView.findViewById( R.id.seat_back_support_down).setOnClickListener( this);
        ((MenuViewItem)mView.findViewById(R.id.seat_back_support_up)).setOnNewTouchEventListener(new MenuViewItem.OnNewTouchEventListener() {
            @Override
            public void onDown() {
                onBtnDown(R.mipmap.gif_seat_back_support_up, new CMDPowerSeatWaistUpOn());
            }

            @Override
            public void onUp() {
                onBtnUp(new CMDPowerSeatWaistUpOff());
            }
        });
        ((MenuViewItem)mView.findViewById( R.id.seat_back_support_down)).setOnNewTouchEventListener(new MenuViewItem.OnNewTouchEventListener() {
            @Override
            public void onDown() {
                onBtnDown(R.mipmap.gif_seat_back_support_down, new CMDPowerSeatWaistDownOn());
            }

            @Override
            public void onUp() {
                onBtnUp(new CMDPowerSeatWaistDownOff());
            }
        });

        mView.findViewById( R.id.seat_back_support_left).setOnClickListener( this);
        mView.findViewById( R.id.seat_back_support_right).setOnClickListener( this);
        ((MenuViewItem)mView.findViewById(R.id.seat_back_support_left)).setOnNewTouchEventListener(new MenuViewItem.OnNewTouchEventListener() {
            @Override
            public void onDown() {
                onBtnDown(R.mipmap.gif_seat_back_support_forward, new CMDPowerSeatWaistOrwardOn());
            }

            @Override
            public void onUp() {
                onBtnUp(new CMDPowerSeatWaistOrwardOff());
            }
        });
        ((MenuViewItem)mView.findViewById( R.id.seat_back_support_right)).setOnNewTouchEventListener(new MenuViewItem.OnNewTouchEventListener() {
            @Override
            public void onDown() {
                onBtnDown(R.mipmap.gif_seat_back_support_backward, new CMDPowerSeatWaistBackwardOn());
            }

            @Override
            public void onUp() {
                onBtnUp(new CMDPowerSeatWaistBackwardOff());
            }
        });




        mView.findViewById( R.id.seat_bottom_backward).setOnClickListener(this);
        mView.findViewById( R.id.seat_bottom_forward).setOnClickListener(this);
        ((MenuViewItem)mView.findViewById(R.id.seat_bottom_backward)).setOnNewTouchEventListener(new MenuViewItem.OnNewTouchEventListener() {
            @Override
            public void onDown() {
                onBtnDown(R.mipmap.gif_seat_bottom_backward, new CMDPowerSeatSeatBackwardOn());
            }

            @Override
            public void onUp() {
                onBtnUp(new CMDPowerSeatSeatBackwardOff());
            }
        });
        ((MenuViewItem)mView.findViewById( R.id.seat_bottom_forward)).setOnNewTouchEventListener(new MenuViewItem.OnNewTouchEventListener() {
            @Override
            public void onDown() {
                onBtnDown(R.mipmap.gif_seat_bottom_forward, new CMDPowerSeatSeatFowardOn());
            }

            @Override
            public void onUp() {
                onBtnUp(new CMDPowerSeatSeatFowardOff());
            }
        });


        mView.findViewById( R.id.seat_back_backward).setOnClickListener(this);
        mView.findViewById( R.id.seat_back_forward).setOnClickListener(this);
        ((MenuViewItem)mView.findViewById( R.id.seat_back_backward)).setOnNewTouchEventListener(new MenuViewItem.OnNewTouchEventListener() {
            @Override
            public void onDown() {
                onBtnDown(R.mipmap.gif_seat_back_backward, new CMDPowerSeatBackrestForwardOn());
            }

            @Override
            public void onUp() {
                releaseLeftGifView();
            }
        });
        ((MenuViewItem)mView.findViewById( R.id.seat_back_forward)).setOnNewTouchEventListener(new MenuViewItem.OnNewTouchEventListener() {
            @Override
            public void onDown() {
                onBtnDown(R.mipmap.gif_seat_back_forward, new CMDPowerSeatSeatPitchUpOn());
            }

            @Override
            public void onUp() {
                releaseLeftGifView();
            }
        });


        mView.findViewById( R.id.seat_bottom_clockwise).setOnClickListener(this);
        mView.findViewById( R.id.seat_bottom_anticlockwise).setOnClickListener(this);
        ((MenuViewItem)mView.findViewById( R.id.seat_bottom_clockwise)).setOnNewTouchEventListener(new MenuViewItem.OnNewTouchEventListener() {
            @Override
            public void onDown() {
                onBtnDown(R.mipmap.gif_seat_bottom_pitch_up, new CMDPowerSeatSeatPitchUpOn());
            }

            @Override
            public void onUp() {
                onBtnUp(new CMDPowerSeatSeatPitchUpOff());
            }
        });
        ((MenuViewItem)mView.findViewById( R.id.seat_bottom_anticlockwise)).setOnNewTouchEventListener(new MenuViewItem.OnNewTouchEventListener() {
            @Override
            public void onDown() {
                onBtnDown(R.mipmap.gif_seat_bottom_pitch_down,new CMDPowerSeatSeatPitchDownOn());
            }

            @Override
            public void onUp() {
                onBtnUp(new CMDPowerSeatSeatPitchDownOff());
            }
        });


        mView.findViewById( R.id.seat_bottom_up).setOnClickListener(this);
        mView.findViewById( R.id.seat_bottom_down).setOnClickListener(this);
        ((MenuViewItem)mView.findViewById( R.id.seat_bottom_up)).setOnNewTouchEventListener(new MenuViewItem.OnNewTouchEventListener() {
            @Override
            public void onDown() {
                onBtnDown(R.mipmap.gif_seat_bottom_up, new CMDPowerSeatSeatFormerUpOn());
            }

            @Override
            public void onUp() {
                onBtnUp(new CMDPowerSeatSeatFormerUpOff());
            }
        });
        ((MenuViewItem)mView.findViewById( R.id.seat_bottom_down)).setOnNewTouchEventListener(new MenuViewItem.OnNewTouchEventListener() {
            @Override
            public void onDown() {
                onBtnDown(R.mipmap.gif_seat_bottom_down,new CMDPowerSeatSearFormerDownOn());
            }

            @Override
            public void onUp() {
                onBtnUp(new CMDPowerSeatSearFormerDownOff());
            }
        });

        iv_seat_heat = (ImageView) mView.findViewById(R.id.iv_seat_heat);
        iv_seat_heat.setOnClickListener(this);
        iv_seat_wind = (ImageView) mView.findViewById(R.id.iv_seat_wind);
        iv_seat_wind.setOnClickListener(this);
        iv_seat_set = (ImageView) mView.findViewById(R.id.iv_seat_set);
        iv_seat_set.setOnClickListener(this);

        gif_view_seat = (GifImageView) mView.findViewById(R.id.gifv_seat);

        iv_seat_heat_stage_1 = (ImageView) mView.findViewById(R.id.iv_seat_heat_stage_1);
        iv_seat_heat_stage_2 = (ImageView) mView.findViewById(R.id.iv_seat_heat_stage_2);
        iv_seat_heat_stage_3 = (ImageView) mView.findViewById(R.id.iv_seat_heat_stage_3);

        v_seat_wind_group = mView.findViewById(R.id.set_group_wind);
        rbtn_seat_wind_stage1 = (RadioButton) v_seat_wind_group.findViewById(R.id.radiobtn_wind_stage1);
        rbtn_seat_wind_stage1.setOnClickListener(this);
        rbtn_seat_wind_stage2 = (RadioButton) v_seat_wind_group.findViewById(R.id.radiobtn_wind_stage2);
        rbtn_seat_wind_stage2.setOnClickListener(this);
        rbtn_seat_wind_stage3 = (RadioButton) v_seat_wind_group.findViewById(R.id.radiobtn_wind_stage3);
        rbtn_seat_wind_stage3.setOnClickListener(this);

        v_seat_set_group = mView.findViewById(R.id.set_group_set);
        rbtn_seat_set_stage1 =  (RadioButton) v_seat_set_group.findViewById(R.id.radiobtn_set_stage1);
        rbtn_seat_set_stage1.setOnClickListener(this);
        rbtn_seat_set_stage2 =  (RadioButton) v_seat_set_group.findViewById(R.id.radiobtn_set_stage2);
        rbtn_seat_set_stage2.setOnClickListener(this);
        rbtn_seat_set_stage3 =  (RadioButton) v_seat_set_group.findViewById(R.id.radiobtn_set_stage3);
        rbtn_seat_set_stage3.setOnClickListener(this);

        initUI();

        return mView;
    }

    private void onBtnDown(int resId, BaseCommand command){
        loadGifonLeft(resId);
        ServiceManager.getInstance().sendCommandToCar(command, new CommandListenerAdapter());
    }

    private void onBtnUp(BaseCommand command){
        releaseLeftGifView();
        ServiceManager.getInstance().sendCommandToCar(command, new CommandListenerAdapter());

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            /*case R.id.seat_back_support_up:

                break;
            case R.id.seat_back_support_down:

                break;
            case R.id.seat_back_support_left:

                break;
            case R.id.seat_back_support_right:

                break;
            case R.id.seat_back_backward:

                break;
            case R.id.seat_back_forward:

                break;
            case R.id.seat_bottom_clockwise:

                break;
            case R.id.seat_bottom_anticlockwise:

                break;
            case R.id.seat_bottom_up:

                break;
            case R.id.seat_bottom_down:

                break;*/
            case R.id.iv_seat_heat:
                if (ConstantData.mSeatHeatStatus >= 3){
                    ConstantData.mSeatHeatStatus = 0;
                    iv_seat_heat.setImageResource(R.mipmap.ic_seat_heat_gray);
                    iv_seat_heat_stage_1.setImageResource(R.mipmap.ic_seat_stage_gray);
                    iv_seat_heat_stage_2.setImageResource(R.mipmap.ic_seat_stage_gray);
                    iv_seat_heat_stage_3.setImageResource(R.mipmap.ic_seat_stage_gray);
                    ServiceManager.getInstance().sendCommandToCar(
                            new CMDPowerSeatHeatCodeSet(0), new CommandListenerAdapter(){

                            });
                } else {
                    ConstantData.mSeatHeatStatus++;
                    iv_seat_heat.setImageResource(R.mipmap.ic_seat_heat_white);
                    switch (ConstantData.mSeatHeatStatus){
                        case 1:
                            iv_seat_heat_stage_1.setImageResource(R.mipmap.ic_seat_stage_green);
                            ServiceManager.getInstance().sendCommandToCar(
                                    new CMDPowerSeatHeatCodeSet(1), new CommandListenerAdapter(){

                                    });
                            break;
                        case 2:
                            iv_seat_heat_stage_2.setImageResource(R.mipmap.ic_seat_stage_green);
                            ServiceManager.getInstance().sendCommandToCar(
                                    new CMDPowerSeatHeatCodeSet(2), new CommandListenerAdapter(){

                                    });
                            break;
                        case 3:
                            iv_seat_heat_stage_3.setImageResource(R.mipmap.ic_seat_stage_green);
                            ServiceManager.getInstance().sendCommandToCar(
                                    new CMDPowerSeatHeatCodeSet(3), new CommandListenerAdapter(){

                                    });
                            break;
                    }
                }

                break;
            case R.id.iv_seat_wind:
                if (ConstantData.mSeatWindStatus == 0){
                    iv_seat_wind.setImageResource(R.mipmap.ic_seat_wind_white);
                    v_seat_wind_group.setVisibility(View.VISIBLE);
                    ConstantData.mSeatWindStatus = 2;
                    ServiceManager.getInstance().sendCommandToCar(
                            new CMDPowerSeatVentilationOn(), new CommandListenerAdapter(){

                            });

                } else {
                    v_seat_wind_group.setVisibility(View.INVISIBLE);
                    iv_seat_wind.setImageResource(R.mipmap.ic_seat_wind_gray);
                    ConstantData.mSeatWindStatus = 0;
                    ServiceManager.getInstance().sendCommandToCar(
                            new CMDPowerSeatVentilationOff(), new CommandListenerAdapter(){

                            });
                }

                break;
            case R.id.iv_seat_set:
                if (ConstantData.mSeatSetStatus == 0){
                    iv_seat_set.setImageResource(R.mipmap.ic_seat_set_white);
//                    v_seat_set_group.setVisibility(View.VISIBLE);
                    ConstantData.mSeatSetStatus = 2;
                    //// TODO: 2017/9/4 send command

                } else {
                    iv_seat_set.setImageResource(R.mipmap.ic_seat_set_gray);
//                    v_seat_set_group.setVisibility(View.INVISIBLE);
                    ConstantData.mSeatSetStatus = 0;
                }
                break;

            case R.id.radiobtn_wind_stage1:
                ServiceManager.getInstance().sendCommandToCar(
                        new CMDPowerSeatVentilationLevelSet(1), new CommandListenerAdapter(){

                        });
                break;
            case R.id.radiobtn_wind_stage2:
                ServiceManager.getInstance().sendCommandToCar(
                        new CMDPowerSeatVentilationLevelSet(2), new CommandListenerAdapter(){

                        });
                break;
            case R.id.radiobtn_wind_stage3:
                ServiceManager.getInstance().sendCommandToCar(
                        new CMDPowerSeatVentilationLevelSet(3), new CommandListenerAdapter(){

                        });
                break;
            case R.id.radiobtn_set_stage1:
                ServiceManager.getInstance().sendCommandToCar(
                        new CMDPowerSeatHeatCodeSet(1), new CommandListenerAdapter(){

                        });
                break;
            case R.id.radiobtn_set_stage2:
                ServiceManager.getInstance().sendCommandToCar(
                        new CMDPowerSeatHeatCodeSet(2), new CommandListenerAdapter(){

                        });
                break;
            case R.id.radiobtn_set_stage3:
                ServiceManager.getInstance().sendCommandToCar(
                        new CMDPowerSeatHeatCodeSet(3), new CommandListenerAdapter(){

                        });
                break;

        }
    }

    private void loadGifonLeft(int resID){
        try {
            // 如果加载的是gif动图，第一步需要先将gif动图资源转化为GifDrawable
            // 将gif图资源转化为GifDrawable
            GifDrawable gifDrawable = new GifDrawable(getResources(), resID);

            // gif1加载一个动态图gif
            gif_view_seat.setImageDrawable(gifDrawable);

            // 如果是普通的图片资源，就像Android的ImageView set图片资源一样简单设置进去即可。
            // gif2加载一个普通的图片（如png，bmp，jpeg等等）
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void releaseLeftGifView(){
        try {
            gif_view_seat.setImageDrawable(null);
            gif_view_seat.setImageResource(R.mipmap.ic_seat_tmp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()){
            case R.id.seat_back_support_up:
                break;
            case R.id.seat_back_support_down:
                break;
            case R.id.seat_back_support_left:
                break;
            case R.id.seat_back_support_right:
                break;
            case R.id.seat_bottom_backward:
                break;
            case R.id.seat_bottom_forward:
                break;
            case R.id.seat_back_backward:
                break;
            case R.id.seat_back_forward:
                break;
            case R.id.seat_bottom_clockwise:
                break;
            case R.id.seat_bottom_anticlockwise:
                break;
            case R.id.seat_bottom_up:
                break;
            case R.id.seat_bottom_down:
                break;
        }
        return false;
    }


    private void initUI(){
        //heat
        if (ConstantData.mSeatHeatStatus == 0){
            iv_seat_heat.setImageResource(R.mipmap.ic_seat_heat_gray);
            iv_seat_heat_stage_1.setImageResource(R.mipmap.ic_seat_stage_gray);
            iv_seat_heat_stage_2.setImageResource(R.mipmap.ic_seat_stage_gray);
            iv_seat_heat_stage_3.setImageResource(R.mipmap.ic_seat_stage_gray);

        } else {
            iv_seat_heat.setImageResource(R.mipmap.ic_seat_heat_white);
            switch (ConstantData.mSeatHeatStatus){
                case 3:
                    iv_seat_heat_stage_3.setImageResource(R.mipmap.ic_seat_stage_green);
                case 2:
                    iv_seat_heat_stage_2.setImageResource(R.mipmap.ic_seat_stage_green);
                case 1:
                    iv_seat_heat_stage_1.setImageResource(R.mipmap.ic_seat_stage_green);
                    break;
            }
        }

        //wind
        if (ConstantData.mSeatWindStatus != 0){
            iv_seat_wind.setImageResource(R.mipmap.ic_seat_wind_white);
            v_seat_wind_group.setVisibility(View.VISIBLE);
            switch (ConstantData.mSeatWindStatus){
                case 1:
                    rbtn_seat_wind_stage1.toggle();
                    break;
                case 2:
                    rbtn_seat_wind_stage2.toggle();
                    break;
                case 3:
                    rbtn_seat_wind_stage3.toggle();
                    break;
            }

        } else {
            v_seat_wind_group.setVisibility(View.INVISIBLE);
            iv_seat_wind.setImageResource(R.mipmap.ic_seat_wind_gray);
        }

        //set
        if (ConstantData.mSeatSetStatus != 0){
            iv_seat_set.setImageResource(R.mipmap.ic_seat_set_white);
            v_seat_set_group.setVisibility(View.VISIBLE);
            switch (ConstantData.mSeatSetStatus){
                case 1:
                    rbtn_seat_set_stage1.toggle();
                    break;
                case 2:
                    rbtn_seat_set_stage2.toggle();
                    break;
                case 3:
                    rbtn_seat_set_stage3.toggle();
                    break;
            }

        } else {
            v_seat_wind_group.setVisibility(View.INVISIBLE);
            iv_seat_wind.setImageResource(R.mipmap.ic_seat_wind_gray);
        }
    }
}

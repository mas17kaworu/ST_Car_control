package com.longkai.stcarcontrol.st_exp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.longkai.stcarcontrol.st_exp.ConstantData;
import com.longkai.stcarcontrol.st_exp.R;
import com.longkai.stcarcontrol.st_exp.activity.MainActivity;
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList.CMDDoorDoorFoot_L_LightOff;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList.CMDDoorDoorFoot_L_LightOn;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList.CMDDoorDoorFoot_R_LightOff;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList.CMDDoorDoorFoot_R_LightOn;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList.CMDDoorECVVoltageCodeSet;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList.CMDDoorLockLOff;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList.CMDDoorLockLOn;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList.CMDDoorLockROff;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList.CMDDoorLockROn;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList.CMDDoorMirrorBlind_L_LightOff;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList.CMDDoorMirrorBlind_L_LightOn;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList.CMDDoorMirrorBlind_R_LightOff;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList.CMDDoorMirrorBlind_R_LightOn;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList.CMDDoorMirrorFoldOff;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList.CMDDoorMirrorFoldOn;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList.CMDDoorMirrorGround_L_LightOff;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList.CMDDoorMirrorGround_L_LightOn;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList.CMDDoorMirrorGround_R_LightOff;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList.CMDDoorMirrorGround_R_LightOn;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList.CMDDoorMirrorUnfoldOff;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList.CMDDoorMirrorUnfoldOn;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList.CMDDoorMirrorXLeftOff;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList.CMDDoorMirrorXLeftOn;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList.CMDDoorMirrorXRightOff;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList.CMDDoorMirrorXRightOn;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList.CMDDoorMirrorYDownOff;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList.CMDDoorMirrorYDownOn;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList.CMDDoorMirrorYupOff;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList.CMDDoorMirrorYupOn;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList.CMDDoorUnlockLOff;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList.CMDDoorUnlockLOn;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList.CMDDoorUnlockROff;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList.CMDDoorUnlockROn;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList.CMDDoorWindowLDownOff;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList.CMDDoorWindowLDownOn;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList.CMDDoorWindowLUpOff;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList.CMDDoorWindowLUpOn;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter;
import com.longkai.stcarcontrol.st_exp.customView.MenuViewItem;

import java.util.Timer;
import java.util.TimerTask;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Administrator on 2017/8/6.
 */

public class DoorFragment extends Fragment implements View.OnClickListener{

    private View mView;

    private GifImageView gif_view_door;

    private ProgressBar pb_door_mirror;

    private ImageView ivDoorLock, ivDoorUnlock, ivMirrorHeat, ivMirrorLight, ivMirrorFold, ivMirrorUnfold, ivMirrorSelect;

    private ImageView iv_fade_zone_lamp, iv_ground_lamp, iv_foot_lamp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_door, container, false);

        mView.findViewById( R.id.iv_door_window_up).setOnClickListener( this);
        mView.findViewById( R.id.iv_door_window_down).setOnClickListener( this);
        ((MenuViewItem)mView.findViewById( R.id.iv_door_window_up)).setOnNewTouchEventListener(new MenuViewItem.OnNewTouchEventListener() {
            @Override
            public void onDown() {
                onBtnDown(R.mipmap.gif_door_window_up, new CMDDoorWindowLUpOn());
            }

            @Override
            public void onUp() {
                onBtnUp(new CMDDoorWindowLUpOff());
            }
        });
        ((MenuViewItem)mView.findViewById( R.id.iv_door_window_down)).setOnNewTouchEventListener(new MenuViewItem.OnNewTouchEventListener() {
            @Override
            public void onDown() {
                onBtnDown(R.mipmap.gif_door_window_down, new CMDDoorWindowLDownOn());
            }

            @Override
            public void onUp() {
                onBtnUp(new CMDDoorWindowLDownOff());
            }
        });

        mView.findViewById( R.id.door_mirror_up).setOnClickListener( this);
        mView.findViewById( R.id.door_mirror_down).setOnClickListener( this);
        ((MenuViewItem)mView.findViewById( R.id.door_mirror_up)).setOnNewTouchEventListener(new MenuViewItem.OnNewTouchEventListener() {
            @Override
            public void onDown() {
                onBtnDown(R.mipmap.gif_door_mirror_up, new CMDDoorMirrorYupOn());
            }

            @Override
            public void onUp() {
                onBtnUp(new CMDDoorMirrorYupOff());
            }
        });
        ((MenuViewItem)mView.findViewById( R.id.door_mirror_down)).setOnNewTouchEventListener(new MenuViewItem.OnNewTouchEventListener() {
            @Override
            public void onDown() {
                onBtnDown(R.mipmap.gif_door_mirror_down, new CMDDoorMirrorYDownOn());
            }

            @Override
            public void onUp() {
                onBtnUp(new CMDDoorMirrorYDownOff());
            }
        });

        mView.findViewById( R.id.door_mirror_left).setOnClickListener( this);
        mView.findViewById( R.id.door_mirror_right).setOnClickListener( this);
        ((MenuViewItem)mView.findViewById( R.id.door_mirror_left)).setOnNewTouchEventListener(new MenuViewItem.OnNewTouchEventListener() {
            @Override
            public void onDown() {
                onBtnDown(R.mipmap.gif_door_mirror_forward, new CMDDoorMirrorXLeftOn());
            }

            @Override
            public void onUp() {
                onBtnUp(new CMDDoorMirrorXLeftOff());
            }
        });
        ((MenuViewItem)mView.findViewById( R.id.door_mirror_right)).setOnNewTouchEventListener(new MenuViewItem.OnNewTouchEventListener() {
            @Override
            public void onDown() {
                onBtnDown(R.mipmap.gif_door_mirror_backward, new CMDDoorMirrorXRightOn());
            }

            @Override
            public void onUp() {
                onBtnUp(new CMDDoorMirrorXRightOff());
            }
        });



        gif_view_door = (GifImageView) mView.findViewById(R.id.gifv_door);
        pb_door_mirror = (ProgressBar) mView.findViewById(R.id.pb_door_mirror_anti_glare);
        mView.findViewById(R.id.iv_door_mirror_plus).setOnClickListener(this);
        mView.findViewById(R.id.iv_door_mirror_minus).setOnClickListener(this);

        ivDoorLock = (ImageView) mView.findViewById(R.id.iv_door_lock);
        ivDoorLock.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        ivDoorLock.setImageResource(R.mipmap.ic_door_lock_green);
                        if (ConstantData.sDoorFragmentStatus[ConstantData.sDoorMirrorSelect] == 0) {//左边
                            ServiceManager.getInstance().sendCommandToCar(new CMDDoorLockLOn(),
                                    new CommandListenerAdapter());
                        } else {
                            ServiceManager.getInstance().sendCommandToCar(new CMDDoorLockROn(),
                                    new CommandListenerAdapter());
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        ivDoorLock.setImageResource(R.mipmap.ic_door_lock_gray);
                        if (ConstantData.sDoorFragmentStatus[ConstantData.sDoorMirrorSelect] == 0) {//左边
                            ServiceManager.getInstance().sendCommandToCar(new CMDDoorLockLOff(),
                                    new CommandListenerAdapter());
                        } else {
                            ServiceManager.getInstance().sendCommandToCar(new CMDDoorLockROff(),
                                    new CommandListenerAdapter());
                        }
                        break;
                }
                return true;
            }
        });
        ivDoorUnlock = (ImageView) mView.findViewById(R.id.iv_door_unlock);
        ivDoorUnlock.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        ivDoorUnlock.setImageResource(R.mipmap.ic_door_unlock_green);
                        if (ConstantData.sDoorFragmentStatus[ConstantData.sDoorMirrorSelect] == 0) {//左边
                            ServiceManager.getInstance().sendCommandToCar(new CMDDoorUnlockLOn(),
                                    new CommandListenerAdapter());
                        } else {
                            ServiceManager.getInstance().sendCommandToCar(new CMDDoorUnlockROn(),
                                    new CommandListenerAdapter());
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        ivDoorUnlock.setImageResource(R.mipmap.ic_door_unlock_gray);
                        if (ConstantData.sDoorFragmentStatus[ConstantData.sDoorMirrorSelect] == 0) {//左边
                            ServiceManager.getInstance().sendCommandToCar(new CMDDoorUnlockLOff(),
                                    new CommandListenerAdapter());
                        } else {
                            ServiceManager.getInstance().sendCommandToCar(new CMDDoorUnlockROff(),
                                    new CommandListenerAdapter());
                        }
                        break;
                }
                return true;
            }
        });
//        ivDoorLock.setOnClickListener(this);
        ivMirrorHeat = (ImageView) mView.findViewById(R.id.iv_door_mirror_heat);
        ivMirrorHeat.setOnClickListener(this);
        ivMirrorLight = (ImageView) mView.findViewById(R.id.iv_door_mirror_light);
        ivMirrorLight.setOnClickListener(this);

        ivMirrorSelect = (ImageView) mView.findViewById(R.id.iv_door_mirror_select);
        ivMirrorSelect.setOnClickListener(this);
        ivMirrorFold = (ImageView) mView.findViewById(R.id.iv_door_mirror_fold);
        ivMirrorFold.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        ServiceManager.getInstance().sendCommandToCar(
                                new CMDDoorMirrorFoldOn(), new CommandListenerAdapter());
                        loadGif(R.mipmap.gif_door_mirror_fold);
                        scheduleMirrorGifDelayTask();
                        break;
                    case MotionEvent.ACTION_UP:
                        ServiceManager.getInstance().sendCommandToCar(
                                new CMDDoorMirrorFoldOff(), new CommandListenerAdapter()
                        );
                        break;
                }
                return true;
            }
        });
//        ivMirrorFold.setOnClickListener(this);
        ivMirrorUnfold = (ImageView) mView.findViewById(R.id.iv_door_mirror_unfold);
        ivMirrorUnfold.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        ServiceManager.getInstance().sendCommandToCar(
                                new CMDDoorMirrorUnfoldOn(), new CommandListenerAdapter());
                        loadGif(R.mipmap.gif_door_mirror_unfold);
                        scheduleMirrorGifDelayTask();
                        break;
                    case MotionEvent.ACTION_UP:
                        ServiceManager.getInstance().sendCommandToCar(
                                new CMDDoorMirrorUnfoldOff(), new CommandListenerAdapter()
                        );
                        break;
                }
                return true;
            }
        });
//        ivMirrorUnfold.setOnClickListener(this);

        iv_fade_zone_lamp = (ImageView) mView.findViewById(R.id.iv_door_fade_zone_lamp);
        iv_fade_zone_lamp.setOnClickListener(this);
        iv_ground_lamp = (ImageView) mView.findViewById(R.id.iv_door_ground_lamp);
        iv_ground_lamp.setOnClickListener(this);
        iv_foot_lamp = (ImageView) mView.findViewById(R.id.iv_door_foot_lamp);
        iv_foot_lamp.setOnClickListener(this);

        mView.findViewById(R.id.tv_door_diagram).setOnClickListener(this);

        refreshUI();
        return mView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            /*case R.id.iv_door_window_down:
                break;
            case R.id.iv_door_window_up:
                break;*/
            case R.id.iv_door_mirror_plus:
                clickProgress(true);
                break;
            case R.id.iv_door_mirror_minus:
                clickProgress(false);
                break;
            case R.id.iv_door_lock:

                break;
            case R.id.iv_door_mirror_heat:
                onBtnClick(ConstantData.sDoorMirrorHeat, ivMirrorHeat, "CMDDoorMirrorHeat");
                break;
            case R.id.iv_door_mirror_light:
                if (ConstantData.sDoorFragmentStatus[ConstantData.sDoorMirrorSelect] == 0) {//左边
                    onBtnClick(ConstantData.sDoorMirrorLeftLight, ivMirrorLight, "CMDDoorMirrorTLLight");
                } else {
                    onBtnClick(ConstantData.sDoorMirrorRightLight, ivMirrorLight, "CMDDoorMirrorTRLight");
                }
                break;
            case R.id.iv_door_mirror_select:
                //一部分命令通过mirrorSelect区分左右
                onBtnClick(ConstantData.sDoorMirrorSelect, ivMirrorSelect, "CMDDoorMirrorSelect");
                refreshUI();
                break;
            case R.id.iv_door_mirror_fold:
                ServiceManager.getInstance().sendCommandToCar(new CMDDoorMirrorFoldOn(),new CommandListenerAdapter());
                ServiceManager.getInstance().sendCommandToCar(new CMDDoorMirrorUnfoldOff(),new CommandListenerAdapter());
                loadGif(R.mipmap.gif_door_mirror_fold);
                scheduleMirrorGifDelayTask();
                break;
            case R.id.iv_door_mirror_unfold:
                ServiceManager.getInstance().sendCommandToCar(new CMDDoorMirrorFoldOff(),new CommandListenerAdapter());
                ServiceManager.getInstance().sendCommandToCar(new CMDDoorMirrorUnfoldOn(),new CommandListenerAdapter());
                loadGif(R.mipmap.gif_door_mirror_unfold);
                scheduleMirrorGifDelayTask();
                break;

            case R.id.iv_door_fade_zone_lamp:
                if (ConstantData.sDoorFragmentStatus[ConstantData.sDoorMirrorSelect] == 0) {//左
                    if (ConstantData.sDoorFragmentStatus[ConstantData.sDoorFadeZoneLeftLamp] == 0){
                        iv_fade_zone_lamp.setImageResource(R.mipmap.ic_door_fade_zone_lamp_green);
                        ServiceManager.getInstance().sendCommandToCar(new CMDDoorMirrorBlind_L_LightOn(), new CommandListenerAdapter());
                        ConstantData.sDoorFragmentStatus[ConstantData.sDoorFadeZoneLeftLamp] = 1;
                    } else {
                        iv_fade_zone_lamp.setImageResource(R.mipmap.ic_door_fade_zone_lamp_gray);
                        ServiceManager.getInstance().sendCommandToCar(new CMDDoorMirrorBlind_L_LightOff(), new CommandListenerAdapter());
                        ConstantData.sDoorFragmentStatus[ConstantData.sDoorFadeZoneLeftLamp] = 0;
                    }
                } else {//右
                    if (ConstantData.sDoorFragmentStatus[ConstantData.sDoorFadeZoneRightLamp] == 0) {
                        iv_fade_zone_lamp.setImageResource(R.mipmap.ic_door_fade_zone_lamp_green);
                        ServiceManager.getInstance().sendCommandToCar(new CMDDoorMirrorBlind_R_LightOn(), new CommandListenerAdapter());
                        ConstantData.sDoorFragmentStatus[ConstantData.sDoorFadeZoneRightLamp] = 1;
                    } else {
                        iv_fade_zone_lamp.setImageResource(R.mipmap.ic_door_fade_zone_lamp_gray);
                        ServiceManager.getInstance().sendCommandToCar(new CMDDoorMirrorBlind_R_LightOff(), new CommandListenerAdapter());
                        ConstantData.sDoorFragmentStatus[ConstantData.sDoorFadeZoneRightLamp] = 0;
                    }
                }
                break;
            case R.id.iv_door_ground_lamp:
                if (ConstantData.sDoorFragmentStatus[ConstantData.sDoorMirrorSelect] == 0) {//左
                    if (ConstantData.sDoorFragmentStatus[ConstantData.sDoorGroundLeftLamp] == 0) {
                        iv_ground_lamp.setImageResource(R.mipmap.ic_door_ground_lamp_green);
                        ServiceManager.getInstance().sendCommandToCar(new CMDDoorMirrorGround_L_LightOn(), new CommandListenerAdapter());
                        ConstantData.sDoorFragmentStatus[ConstantData.sDoorGroundLeftLamp] = 1;
                    } else if (ConstantData.sDoorFragmentStatus[ConstantData.sDoorGroundLeftLamp] == 1) {
                        iv_ground_lamp.setImageResource(R.mipmap.ic_door_ground_lamp_gray);
                        ServiceManager.getInstance().sendCommandToCar(new CMDDoorMirrorGround_L_LightOff(), new CommandListenerAdapter());
                        ConstantData.sDoorFragmentStatus[ConstantData.sDoorGroundLeftLamp] = 0;
                    }
                } else {
                    if (ConstantData.sDoorFragmentStatus[ConstantData.sDoorGroundRightLamp] == 0) {
                        iv_ground_lamp.setImageResource(R.mipmap.ic_door_ground_lamp_green);
                        ServiceManager.getInstance().sendCommandToCar(new CMDDoorMirrorGround_R_LightOn(), new CommandListenerAdapter());
                        ConstantData.sDoorFragmentStatus[ConstantData.sDoorGroundRightLamp] = 1;
                    } else if (ConstantData.sDoorFragmentStatus[ConstantData.sDoorGroundRightLamp] == 1) {
                        iv_ground_lamp.setImageResource(R.mipmap.ic_door_ground_lamp_gray);
                        ServiceManager.getInstance().sendCommandToCar(new CMDDoorMirrorGround_R_LightOff(), new CommandListenerAdapter());
                        ConstantData.sDoorFragmentStatus[ConstantData.sDoorGroundRightLamp] = 0;
                    }
                }
                break;
            case R.id.iv_door_foot_lamp:
                if (ConstantData.sDoorFragmentStatus[ConstantData.sDoorMirrorSelect] == 0) {//左
                    if (ConstantData.sDoorFragmentStatus[ConstantData.sDoorFootLeftLamp] == 0) {
                        iv_foot_lamp.setImageResource(R.mipmap.ic_door_foot_lamp_green);
                        ServiceManager.getInstance().sendCommandToCar(new CMDDoorDoorFoot_L_LightOn(), new CommandListenerAdapter());
                        ConstantData.sDoorFragmentStatus[ConstantData.sDoorFootLeftLamp] = 1;
                    } else if (ConstantData.sDoorFragmentStatus[ConstantData.sDoorFootLeftLamp] == 1) {
                        iv_foot_lamp.setImageResource(R.mipmap.ic_door_foot_lamp_gray);
                        ServiceManager.getInstance().sendCommandToCar(new CMDDoorDoorFoot_L_LightOff(), new CommandListenerAdapter());
                        ConstantData.sDoorFragmentStatus[ConstantData.sDoorFootLeftLamp] = 0;
                    }
                } else {
                    if (ConstantData.sDoorFragmentStatus[ConstantData.sDoorFootRightLamp] == 0) {
                        iv_foot_lamp.setImageResource(R.mipmap.ic_door_foot_lamp_green);
                        ServiceManager.getInstance().sendCommandToCar(new CMDDoorDoorFoot_R_LightOn(), new CommandListenerAdapter());
                        ConstantData.sDoorFragmentStatus[ConstantData.sDoorFootRightLamp] = 1;
                    } else if (ConstantData.sDoorFragmentStatus[ConstantData.sDoorFootRightLamp] == 1) {
                        iv_foot_lamp.setImageResource(R.mipmap.ic_door_foot_lamp_gray);
                        ServiceManager.getInstance().sendCommandToCar(new CMDDoorDoorFoot_R_LightOff(), new CommandListenerAdapter());
                        ConstantData.sDoorFragmentStatus[ConstantData.sDoorFootRightLamp] = 0;
                    }
                }
                break;
            case R.id.tv_door_diagram:
                ((MainActivity)getActivity()).showDiagram(ConstantData.DOOR_DIAGRAM);
                break;
        }
    }

    Timer timer = new Timer();

    private void scheduleMirrorGifDelayTask(){
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
//                ServiceManager.getInstance().sendCommandToCar(new CMDDoorMirrorUnfoldOff(),new CommandListenerAdapter());
//                ServiceManager.getInstance().sendCommandToCar(new CMDDoorMirrorFoldOff(),new CommandListenerAdapter());
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            releaseGifView();
                        }
                    });
                }

            }
        }, 2500);
    }


    private void onBtnDown(int resId, BaseCommand command){
        loadGif(resId);
        ServiceManager.getInstance().sendCommandToCar(command, new CommandListenerAdapter());
    }

    private void onBtnUp(BaseCommand command){
        releaseGifView();
        ServiceManager.getInstance().sendCommandToCar(command, new CommandListenerAdapter());
    }


    private void onBtnClick(int index, View view, String baseCommand){
        if (ConstantData.sDoorFragmentStatus[index] == 0){
            view.setSelected(true);
            ConstantData.sDoorFragmentStatus[index] = 1;

            try {//反射
                Class<?> class1 = Class.forName("com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList." +
                        baseCommand + "On");
                Object object = class1.newInstance();
                ServiceManager.getInstance().sendCommandToCar((BaseCommand)object, new CommandListenerAdapter());
            } catch ( ClassNotFoundException e){
                e.printStackTrace();
            } catch ( java.lang.InstantiationException e){
                e.printStackTrace();
            } catch ( IllegalAccessException e){
                e.printStackTrace();
            }

        } else {
            view.setSelected(false);
            ConstantData.sDoorFragmentStatus[index] = 0;

            try {
                Class<?> class1 = Class.forName("com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList." +
                        baseCommand + "Off");
                Object object = class1.newInstance();
                ServiceManager.getInstance().sendCommandToCar((BaseCommand)object, new CommandListenerAdapter());
            } catch ( ClassNotFoundException e){
                e.printStackTrace();
            } catch ( java.lang.InstantiationException e){
                e.printStackTrace();
            } catch ( IllegalAccessException e){
                e.printStackTrace();
            }
        }
    }

    private void clickProgress(boolean isPlus){
        int progress = ConstantData.sDoorFragmentStatus[ConstantData.sDoorAntiGlare];
        if (isPlus && progress < 100){
            progress+=10;
        }
        if (!isPlus && progress > 0){
            progress-=10;
        }
        pb_door_mirror.setProgress(progress);
        ConstantData.sDoorFragmentStatus[ConstantData.sDoorAntiGlare] = progress;

        //progress 0~100  转换到 0~63
        progress*=0.63;

        ServiceManager.getInstance().sendCommandToCar(new CMDDoorECVVoltageCodeSet(progress),
                new CommandListenerAdapter());
    }

    private void refreshUI(){
        pb_door_mirror.setProgress(ConstantData.sDoorFragmentStatus[ConstantData.sDoorAntiGlare]);

        if (ConstantData.sDoorFragmentStatus[ConstantData.sDoorMirrorHeat] == 0){
            ivMirrorHeat.setSelected(false);
        } else {
            ivMirrorHeat.setSelected(true);
        }
        if (ConstantData.sDoorFragmentStatus[ConstantData.sDoorMirrorSelect] == 0){
            ivMirrorSelect.setSelected(false);//
        } else {
            ivMirrorSelect.setSelected(true);
        }

        if (ConstantData.sDoorFragmentStatus[ConstantData.sDoorMirrorSelect] == 0) {
            if (ConstantData.sDoorFragmentStatus[ConstantData.sDoorMirrorLeftLight] == 0) {
                ivMirrorLight.setSelected(false);
            } else {
                ivMirrorLight.setSelected(true);
            }
            /*if (ConstantData.sDoorFragmentStatus[ConstantData.sDoorLeftLock] == 0) {
                ivDoorLock.setSelected(false);
            } else {
                ivDoorLock.setSelected(true);
            }*/
            if (ConstantData.sDoorFragmentStatus[ConstantData.sDoorFadeZoneLeftLamp] == 0) {
                iv_fade_zone_lamp.setImageResource(R.mipmap.ic_door_fade_zone_lamp_gray);
            } else {
                iv_fade_zone_lamp.setImageResource(R.mipmap.ic_door_fade_zone_lamp_green);
            }
            if (ConstantData.sDoorFragmentStatus[ConstantData.sDoorGroundLeftLamp] == 0 ){
                iv_ground_lamp.setImageResource(R.mipmap.ic_door_ground_lamp_gray);
            } else {
                iv_ground_lamp.setImageResource(R.mipmap.ic_door_ground_lamp_green);
            }
            if (ConstantData.sDoorFragmentStatus[ConstantData.sDoorFootLeftLamp] ==0){
                iv_foot_lamp.setImageResource(R.mipmap.ic_door_foot_lamp_gray);
            } else {
                iv_foot_lamp.setImageResource(R.mipmap.ic_door_foot_lamp_green);
            }

        } else if (ConstantData.sDoorFragmentStatus[ConstantData.sDoorMirrorSelect] == 1){
            if (ConstantData.sDoorFragmentStatus[ConstantData.sDoorMirrorRightLight] == 0) {
                ivMirrorLight.setSelected(false);
            } else {
                ivMirrorLight.setSelected(true);
            }
            /*if (ConstantData.sDoorFragmentStatus[ConstantData.sDoorRightLock] == 0) {
                ivDoorLock.setSelected(false);
            } else {
                ivDoorLock.setSelected(true);
            }*/
            if (ConstantData.sDoorFragmentStatus[ConstantData.sDoorFadeZoneRightLamp] == 0) {
                iv_fade_zone_lamp.setImageResource(R.mipmap.ic_door_fade_zone_lamp_gray);
            } else {
                iv_fade_zone_lamp.setImageResource(R.mipmap.ic_door_fade_zone_lamp_green);
            }
            if (ConstantData.sDoorFragmentStatus[ConstantData.sDoorGroundRightLamp] == 0 ){
                iv_ground_lamp.setImageResource(R.mipmap.ic_door_ground_lamp_gray);
            } else {
                iv_ground_lamp.setImageResource(R.mipmap.ic_door_ground_lamp_green);
            }
            if (ConstantData.sDoorFragmentStatus[ConstantData.sDoorFootRightLamp] ==0){
                iv_foot_lamp.setImageResource(R.mipmap.ic_door_foot_lamp_gray);
            } else {
                iv_foot_lamp.setImageResource(R.mipmap.ic_door_foot_lamp_green);
            }

        }

    }

    private void loadGif(int resID){
        try {
            // 如果加载的是gif动图，第一步需要先将gif动图资源转化为GifDrawable
            // 将gif图资源转化为GifDrawable
            GifDrawable gifDrawable = new GifDrawable(getResources(), resID);

            // gif1加载一个动态图gif
            gif_view_door.setImageDrawable(gifDrawable);

            // 如果是普通的图片资源，就像Android的ImageView set图片资源一样简单设置进去即可。
            // gif2加载一个普通的图片（如png，bmp，jpeg等等）
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void releaseGifView(){
        try {
            gif_view_door.setImageDrawable(null);
            gif_view_door.setImageResource(R.mipmap.ic_door);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

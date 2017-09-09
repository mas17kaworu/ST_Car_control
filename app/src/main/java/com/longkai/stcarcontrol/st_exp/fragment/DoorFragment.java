package com.longkai.stcarcontrol.st_exp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.longkai.stcarcontrol.st_exp.ConstantData;
import com.longkai.stcarcontrol.st_exp.R;
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList.CMDDoorECVVoltageCodeSet;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList.CMDDoorMirrorXLeftOff;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList.CMDDoorMirrorXLeftOn;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList.CMDDoorMirrorXRightOff;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList.CMDDoorMirrorXRightOn;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList.CMDDoorMirrorYDownOff;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList.CMDDoorMirrorYDownOn;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList.CMDDoorMirrorYupOff;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList.CMDDoorMirrorYupOn;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList.CMDDoorWindowLDownOff;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList.CMDDoorWindowLDownOn;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList.CMDDoorWindowLUpOff;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList.CMDDoorWindowLUpOn;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter;
import com.longkai.stcarcontrol.st_exp.customView.MenuViewItem;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Administrator on 2017/8/6.
 */

public class DoorFragment extends Fragment implements View.OnClickListener{

    private View mView;

    private GifImageView gif_view_door;

    private ProgressBar pb_door_mirror;

    private ImageView ivDoorLock, ivMirrorHeat, ivMirrorLight, ivMirrorFold, ivMirrorUnfold, ivMirrorSelect;

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
        ivDoorLock.setOnClickListener(this);
        ivMirrorHeat = (ImageView) mView.findViewById(R.id.iv_door_mirror_heat);
        ivMirrorHeat.setOnClickListener(this);
        ivMirrorLight = (ImageView) mView.findViewById(R.id.iv_door_mirror_light);
        ivMirrorLight.setOnClickListener(this);

        ivMirrorSelect = (ImageView) mView.findViewById(R.id.iv_door_mirror_select);
        ivMirrorSelect.setOnClickListener(this);
        ivMirrorFold = (ImageView) mView.findViewById(R.id.iv_door_mirror_fold);
        ivMirrorFold.setOnClickListener(this);
        ivMirrorUnfold = (ImageView) mView.findViewById(R.id.iv_door_mirror_unfold);
        ivMirrorUnfold.setOnClickListener(this);

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
                onBtnClick(ConstantData.sDoorLock, ivDoorLock, "CMDDoorLockL");
                break;
            case R.id.iv_door_mirror_heat:
                onBtnClick(ConstantData.sDoorMirrorHeat, ivMirrorHeat, "CMDDoorMirrorHeat");
                break;
            case R.id.iv_door_mirror_light:
                onBtnClick(ConstantData.sDoorMirrorLight, ivMirrorLight, "CMDDoorMirrorTLLight");
                break;
            case R.id.iv_door_mirror_select:
                onBtnClick(ConstantData.sDoorMirrorSelect, ivMirrorSelect, "CMDDoorMirrorSelect");
                break;
            case R.id.iv_door_mirror_fold:

                break;
            case R.id.iv_door_mirror_unfold:

                break;

        }
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
        if (ConstantData.sDoorFragmentStatus[ConstantData.sDoorLock] == 0){
            ivDoorLock.setSelected(false);
        } else {
            ivDoorLock.setSelected(true);
        }
        if (ConstantData.sDoorFragmentStatus[ConstantData.sDoorMirrorHeat] == 0){
            ivMirrorHeat.setSelected(false);
        } else {
            ivMirrorHeat.setSelected(true);
        }
        if (ConstantData.sDoorFragmentStatus[ConstantData.sDoorMirrorLight] == 0){
            ivMirrorLight.setSelected(false);
        } else {
            ivMirrorLight.setSelected(true);
        }
        if (ConstantData.sDoorFragmentStatus[ConstantData.sDoorMirrorSelect] == 0){
            ivMirrorSelect.setSelected(false);
        } else {
            ivMirrorSelect.setSelected(true);
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

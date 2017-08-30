package com.longkai.stcarcontrol.st_exp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.longkai.stcarcontrol.st_exp.ConstantData;
import com.longkai.stcarcontrol.st_exp.R;
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_door, container, false);

        mView.findViewById( R.id.iv_door_window_up).setOnClickListener( this);
        mView.findViewById( R.id.iv_door_window_down).setOnClickListener( this);
        ((MenuViewItem)mView.findViewById( R.id.iv_door_window_up)).setOnNewTouchEventListener(new MenuViewItem.OnNewTouchEventListener() {
            @Override
            public void onDown() {
                loadGif(R.mipmap.gif_door_window_up);
            }

            @Override
            public void onUp() {
                releaseGifView();
            }
        });
        ((MenuViewItem)mView.findViewById( R.id.iv_door_window_down)).setOnNewTouchEventListener(new MenuViewItem.OnNewTouchEventListener() {
            @Override
            public void onDown() {
                loadGif(R.mipmap.gif_door_window_down);
            }

            @Override
            public void onUp() {
                releaseGifView();
            }
        });

        mView.findViewById( R.id.door_mirror_up).setOnClickListener( this);
        mView.findViewById( R.id.door_mirror_down).setOnClickListener( this);
        ((MenuViewItem)mView.findViewById( R.id.door_mirror_up)).setOnNewTouchEventListener(new MenuViewItem.OnNewTouchEventListener() {
            @Override
            public void onDown() {
                loadGif(R.mipmap.gif_door_mirror_up);
            }

            @Override
            public void onUp() {
                releaseGifView();
            }
        });
        ((MenuViewItem)mView.findViewById( R.id.door_mirror_down)).setOnNewTouchEventListener(new MenuViewItem.OnNewTouchEventListener() {
            @Override
            public void onDown() {
                loadGif(R.mipmap.gif_door_mirror_down);
            }

            @Override
            public void onUp() {
                releaseGifView();
            }
        });

        mView.findViewById( R.id.door_mirror_left).setOnClickListener( this);
        mView.findViewById( R.id.door_mirror_right).setOnClickListener( this);
        ((MenuViewItem)mView.findViewById( R.id.door_mirror_left)).setOnNewTouchEventListener(new MenuViewItem.OnNewTouchEventListener() {
            @Override
            public void onDown() {
                loadGif(R.mipmap.gif_door_mirror_forward);
            }

            @Override
            public void onUp() {
                releaseGifView();
            }
        });
        ((MenuViewItem)mView.findViewById( R.id.door_mirror_right)).setOnNewTouchEventListener(new MenuViewItem.OnNewTouchEventListener() {
            @Override
            public void onDown() {
                loadGif(R.mipmap.gif_door_mirror_backward);
            }

            @Override
            public void onUp() {
                releaseGifView();
            }
        });



        gif_view_door = (GifImageView) mView.findViewById(R.id.gifv_door);
        pb_door_mirror = (ProgressBar) mView.findViewById(R.id.pb_door_mirror_anti_glare);
        mView.findViewById(R.id.iv_door_mirror_plus).setOnClickListener(this);
        mView.findViewById(R.id.iv_door_mirror_minus).setOnClickListener(this);


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

        }

    }

    private void clickProgress(boolean isPlus){
        int progress = ConstantData.sDoorFragmentStatus[ConstantData.sDoorAntiGlare];
        if (isPlus && progress <= 100){
            progress+=10;
        }
        if (!isPlus && progress >= 0){
            progress-=10;
        }
        pb_door_mirror.setProgress(progress);
        ConstantData.sDoorFragmentStatus[ConstantData.sDoorAntiGlare] = progress;
    }

    private void refreshUI(){
        pb_door_mirror.setProgress(ConstantData.sDoorFragmentStatus[ConstantData.sDoorAntiGlare]);

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

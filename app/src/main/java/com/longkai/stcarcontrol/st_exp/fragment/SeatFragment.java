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

    private GifImageView gif_view_seat_left;
    private GifImageView gif_view_seat_right;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_seat, container, false);
        mView.findViewById( R.id.seat_back_support_up).setOnClickListener(this);
        mView.findViewById( R.id.seat_back_support_down).setOnClickListener( this);
        mView.findViewById( R.id.seat_back_support_left).setOnClickListener( this);
        mView.findViewById( R.id.seat_back_support_right).setOnClickListener( this);

        mView.findViewById( R.id.seat_bottom_backward).setOnClickListener(this);
        mView.findViewById( R.id.seat_bottom_forward).setOnClickListener(this);
        ((MenuViewItem)mView.findViewById(R.id.seat_bottom_backward)).setOnNewTouchEventListener(new MenuViewItem.OnNewTouchEventListener() {
            @Override
            public void onDown() {
                loadGifonLeft(R.mipmap.gif_seat_bottom_backward);
            }

            @Override
            public void onUp() {
                releaseLeftGifView();
            }
        });
        ((MenuViewItem)mView.findViewById( R.id.seat_bottom_forward)).setOnNewTouchEventListener(new MenuViewItem.OnNewTouchEventListener() {
            @Override
            public void onDown() {
                loadGifonLeft(R.mipmap.git_seat_bottom_forward);
            }

            @Override
            public void onUp() {
                releaseLeftGifView();
            }
        });


        mView.findViewById( R.id.seat_back_backward).setOnClickListener(this);
        mView.findViewById( R.id.seat_back_forward).setOnClickListener(this);
        mView.findViewById( R.id.seat_bottom_clockwise).setOnClickListener(this);
        mView.findViewById( R.id.seat_bottom_anticlockwise).setOnClickListener(this);
        mView.findViewById( R.id.seat_bottom_up).setOnClickListener(this);
        mView.findViewById( R.id.seat_bottom_down).setOnClickListener(this);
        iv_seat_heat = (ImageView) mView.findViewById(R.id.iv_seat_heat);
        iv_seat_heat.setOnClickListener(this);
        iv_seat_wind = (ImageView) mView.findViewById(R.id.iv_seat_wind);
        iv_seat_wind.setOnClickListener(this);
        iv_seat_set = (ImageView) mView.findViewById(R.id.iv_seat_set);
        iv_seat_set.setOnClickListener(this);

        gif_view_seat_left = (GifImageView) mView.findViewById(R.id.gifv_seat_left);
        gif_view_seat_right = (GifImageView) mView.findViewById(R.id.gifv_seat_right);

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




    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.seat_back_support_up:

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

                break;
            case R.id.iv_seat_heat:
                if (ConstantData.mSeatHeatStatus >= 3){
                    ConstantData.mSeatHeatStatus = 0;
                    iv_seat_heat.setImageResource(R.mipmap.ic_seat_heat_gray);
                    iv_seat_heat_stage_1.setImageResource(R.mipmap.ic_seat_stage_gray);
                    iv_seat_heat_stage_2.setImageResource(R.mipmap.ic_seat_stage_gray);
                    iv_seat_heat_stage_3.setImageResource(R.mipmap.ic_seat_stage_gray);

                } else {
                    ConstantData.mSeatHeatStatus++;
                    iv_seat_heat.setImageResource(R.mipmap.ic_seat_heat_white);
                    switch (ConstantData.mSeatHeatStatus){
                        case 1:
                            iv_seat_heat_stage_1.setImageResource(R.mipmap.ic_seat_stage_green);
                            break;
                        case 2:
                            iv_seat_heat_stage_2.setImageResource(R.mipmap.ic_seat_stage_green);
                            break;
                        case 3:
                            iv_seat_heat_stage_3.setImageResource(R.mipmap.ic_seat_stage_green);
                            break;
                    }
                }

                break;
            case R.id.iv_seat_wind:
                if (ConstantData.mSeatWindStatus == 0){
                    iv_seat_wind.setImageResource(R.mipmap.ic_seat_wind_white);
                    v_seat_wind_group.setVisibility(View.VISIBLE);
                    ConstantData.mSeatWindStatus = 2;

                } else {
                    v_seat_wind_group.setVisibility(View.INVISIBLE);
                    iv_seat_wind.setImageResource(R.mipmap.ic_seat_wind_gray);
                    ConstantData.mSeatWindStatus = 0;
                }

                break;
            case R.id.iv_seat_set:
                if (ConstantData.mSeatSetStatus == 0){
                    iv_seat_set.setImageResource(R.mipmap.ic_seat_set_white);
                    v_seat_set_group.setVisibility(View.VISIBLE);
                    ConstantData.mSeatSetStatus = 2;

                } else {
                    iv_seat_set.setImageResource(R.mipmap.ic_seat_set_gray);
                    v_seat_set_group.setVisibility(View.INVISIBLE);
                    ConstantData.mSeatSetStatus = 0;
                }
                break;

            case R.id.radiobtn_wind_stage1:

                break;
            case R.id.radiobtn_wind_stage2:

                break;
            case R.id.radiobtn_wind_stage3:

                break;

        }
    }

    private void loadGifonLeft(int resID){
        try {
            // 如果加载的是gif动图，第一步需要先将gif动图资源转化为GifDrawable
            // 将gif图资源转化为GifDrawable
            GifDrawable gifDrawable = new GifDrawable(getResources(), resID);

            // gif1加载一个动态图gif
            gif_view_seat_left.setImageDrawable(gifDrawable);

            // 如果是普通的图片资源，就像Android的ImageView set图片资源一样简单设置进去即可。
            // gif2加载一个普通的图片（如png，bmp，jpeg等等）
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void releaseLeftGifView(){
        try {
            gif_view_seat_left.setImageDrawable(null);
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

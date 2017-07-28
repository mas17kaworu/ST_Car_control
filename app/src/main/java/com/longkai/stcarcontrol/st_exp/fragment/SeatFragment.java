package com.longkai.stcarcontrol.st_exp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.longkai.stcarcontrol.st_exp.R;

/**
 *
 *
 * Created by Administrator on 2017/7/10.
 */

public class SeatFragment extends Fragment implements View.OnClickListener{
    private View mView;
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
        mView.findViewById( R.id.seat_back_backward).setOnClickListener(this);
        mView.findViewById( R.id.seat_back_forward).setOnClickListener(this);
        mView.findViewById( R.id.seat_bottom_clockwise).setOnClickListener(this);
        mView.findViewById( R.id.seat_bottom_anticlockwise).setOnClickListener(this);
        mView.findViewById( R.id.seat_bottom_up).setOnClickListener(this);
        mView.findViewById( R.id.seat_bottom_down).setOnClickListener(this);
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
    }
}

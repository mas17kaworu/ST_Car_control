package com.longkai.stcarcontrol.st_exp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.longkai.stcarcontrol.st_exp.R;

public class CarBackOLEDFragment extends Fragment implements View.OnClickListener {

  private View mView;

  private ImageView ivReversing, ivBrake, ivPosition, ivTurnLeft, ivTurnRight, ivAuto1, ivAuto2, ivAuto3;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    mView = inflater.inflate(R.layout.fragment_car_back_oled, container, false);
    return mView;
  }

  @Override public void onClick(View view) {

  }
}

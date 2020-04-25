package com.longkai.stcarcontrol.st_exp.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.longkai.stcarcontrol.st_exp.R;
import java.text.SimpleDateFormat;
import java.util.concurrent.atomic.AtomicBoolean;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Administrator on 2018/10/29.
 */

public class VCUOBCDemoFragment extends Fragment implements View.OnClickListener {
  private View mView;

  private ImageView ivSwitch;
  private TextView tvTimeCounting;
  private GifImageView gifVCharging;
  private AtomicBoolean charging = new AtomicBoolean(false);

  private static long ZERO = -28800000L;
  private long time = ZERO;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    mView = inflater.inflate(R.layout.fragment_vcu_obc_demo, container, false);

    ivSwitch = (ImageView) mView.findViewById(R.id.iv_obc_demo_switch);
    ivSwitch.setOnClickListener(this);

    tvTimeCounting = (TextView) mView.findViewById(R.id.tv_obc_demo_time);

    gifVCharging = (GifImageView) mView.findViewById(R.id.gifv_vcu_obc_demo_charging);

    //todo register obc demo return

    //handler.postDelayed(runnable, 1000);// 打开定时器，500ms后执行runnable
    return mView;
  }
  SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");

  Handler handler = new Handler();
  Runnable runnable = new Runnable() {
    @Override
    public void run() {
      if (charging.get()) {
        time += 1000;
        String date = df.format(time);
        tvTimeCounting.setText(date);
        handler.postDelayed(runnable, 1000);
      }
    }
  };

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.iv_obc_demo_switch:
        if (charging.get()){ //关闭
          charging.set(false);
          ivSwitch.setImageResource(R.mipmap.ic_obc_demo_switch_on);
          gifVCharging.setVisibility(View.INVISIBLE);
          //todo send cmd
        } else { //开启
          time =  ZERO;
          String date = df.format(time);
          tvTimeCounting.setText(date);
          charging.set(true);
          handler.removeCallbacks(runnable);
          handler.postDelayed(runnable, 1000);
          ivSwitch.setImageResource(R.mipmap.ic_obc_demo_switch_off);
          showChargingGif();
          //todo send cmd
        }
        break;
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    handler.removeCallbacks(runnable);
  }

  private void showChargingGif(){
    try {
      GifDrawable gifDrawable = new GifDrawable(getResources(), R.mipmap.gif_obc_demo_charging);
      gifVCharging.setVisibility(View.VISIBLE);
      gifVCharging.setImageDrawable(gifDrawable);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

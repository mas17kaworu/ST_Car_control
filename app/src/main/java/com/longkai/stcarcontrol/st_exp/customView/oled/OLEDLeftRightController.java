package com.longkai.stcarcontrol.st_exp.customView.oled;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import com.longkai.stcarcontrol.st_exp.R;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class OLEDLeftRightController {
  List<WeakReference<ImageView>> LeftLightList = new ArrayList<>();
  List<WeakReference<ImageView>> RightLightList = new ArrayList<>();

  private AtomicBoolean leftState = new AtomicBoolean(false);
  private AtomicBoolean rightState= new AtomicBoolean(false);

  public OLEDLeftRightController(Fragment fragment){
    LeftLightList = Arrays.asList(
      new WeakReference<ImageView>((ImageView) (fragment.getView().findViewById(R.id.iv_back_oled_left_10))),
      new WeakReference<ImageView>((ImageView) (fragment.getView().findViewById(R.id.iv_back_oled_left_9))),
      new WeakReference<ImageView>((ImageView) (fragment.getView().findViewById(R.id.iv_back_oled_left_8))),
      new WeakReference<ImageView>((ImageView) (fragment.getView().findViewById(R.id.iv_back_oled_left_7))),
      new WeakReference<ImageView>((ImageView) (fragment.getView().findViewById(R.id.iv_back_oled_left_6))),
      new WeakReference<ImageView>((ImageView) (fragment.getView().findViewById(R.id.iv_back_oled_left_5))),
      new WeakReference<ImageView>((ImageView) (fragment.getView().findViewById(R.id.iv_back_oled_left_4))),
      new WeakReference<ImageView>((ImageView) (fragment.getView().findViewById(R.id.iv_back_oled_left_3))),
      new WeakReference<ImageView>((ImageView) (fragment.getView().findViewById(R.id.iv_back_oled_left_2))),
      new WeakReference<ImageView>((ImageView) (fragment.getView().findViewById(R.id.iv_back_oled_left_1)))
    );

    RightLightList = Arrays.asList(
        new WeakReference<ImageView>((ImageView) (fragment.getView().findViewById(R.id.iv_back_oled_right_10))),
        new WeakReference<ImageView>((ImageView) (fragment.getView().findViewById(R.id.iv_back_oled_right_9))),
        new WeakReference<ImageView>((ImageView) (fragment.getView().findViewById(R.id.iv_back_oled_right_8))),
        new WeakReference<ImageView>((ImageView) (fragment.getView().findViewById(R.id.iv_back_oled_right_7))),
        new WeakReference<ImageView>((ImageView) (fragment.getView().findViewById(R.id.iv_back_oled_right_6))),
        new WeakReference<ImageView>((ImageView) (fragment.getView().findViewById(R.id.iv_back_oled_right_5))),
        new WeakReference<ImageView>((ImageView) (fragment.getView().findViewById(R.id.iv_back_oled_right_4))),
        new WeakReference<ImageView>((ImageView) (fragment.getView().findViewById(R.id.iv_back_oled_right_3))),
        new WeakReference<ImageView>((ImageView) (fragment.getView().findViewById(R.id.iv_back_oled_right_2))),
        new WeakReference<ImageView>((ImageView) (fragment.getView().findViewById(R.id.iv_back_oled_right_1)))
    );
    leftRunner = new PaoMaDeng(LeftLightList);
    rightRunner = new PaoMaDeng(RightLightList);
  }



  public void turnOnLeft(){
    if (rightState.get()) { //sync left and right
      handler.removeCallbacks(rightRunner);
      turnOffAllLight(RightLightList);
      handler.post(rightRunner);
    }
    handler.removeCallbacks(leftRunner);
    handler.post(leftRunner);
    leftState.set(true);
  }

  public void turnOffLeft(){
    leftState.set(false);
    handler.removeCallbacks(leftRunner);
    turnOffAllLight(LeftLightList);
  }

  public void turnOnRight(){
    if (leftState.get()){//sync left and right
      handler.removeCallbacks(leftRunner);
      turnOffAllLight(LeftLightList);
      handler.post(leftRunner);
    }
    handler.removeCallbacks(rightRunner);
    handler.post(rightRunner);
    rightState.set(true);
  }

  public void turnOffRight(){
    rightState.set(false);
    handler.removeCallbacks(rightRunner);
    turnOffAllLight(RightLightList);
  }


  private Handler handler = new Handler();

  private PaoMaDeng leftRunner, rightRunner;

  private void playAnimation(List<WeakReference<ImageView>> lampList){
    handler.post(new PaoMaDeng(lampList));
  }

  private class PaoMaDeng implements Runnable {
    List<WeakReference<ImageView>> lampList;

    PaoMaDeng (List<WeakReference<ImageView>> list){
      this.lampList = list;
    }

    @Override public void run() {
      for (WeakReference<ImageView> lampRF: lampList) {
        if (lampRF.get() != null){

          if (lampRF.get().getVisibility() == View.INVISIBLE) {
            lampRF.get().setVisibility(View.VISIBLE);
            handler.postDelayed(this, 60);
            return;
          }

        } else {
          return;
        }
      }
      turnOffAllLight(lampList);
      handler.postDelayed(this, 150);
    }
  }

  private void turnOffAllLight(List<WeakReference<ImageView>> lampList){
    for (WeakReference<ImageView> lampRF: lampList
    ) {
      if (lampRF.get() != null) {
        lampRF.get().setVisibility(View.INVISIBLE);
      }
    }
  }
}

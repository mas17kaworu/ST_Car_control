package com.longkai.stcarcontrol.st_exp.customView.oled2;

import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.longkai.stcarcontrol.st_exp.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class OLED2LeftRightController {
  List<WeakReference<ImageView>> LeftLightList = new ArrayList<>();
  List<WeakReference<ImageView>> RightLightList = new ArrayList<>();

  private AtomicBoolean leftState = new AtomicBoolean(false);
  private AtomicBoolean rightState= new AtomicBoolean(false);

  public OLED2LeftRightController(Fragment fragment){
    LeftLightList = Arrays.asList(
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
    flashRunner = new FlashRunner();
  }

  public void updateByState(OLEDLeftRightState state){
    handler.removeCallbacksAndMessages(null);//remove all
    switchAllLight(false);
    switch (state) {
      case ALL_ON:
        switchAllLight(true);
        break;
      case ALL_OFF:
        break;
      case RUN_LEFT:
        turnOnLeft();
        break;
      case RUN_RIGHT:
        turnOnRight();
        break;
      case DOUBLE_FLASH:
        doubleFlash();
        break;
      default:
        break;
    }
  }

  private void switchAllLight(boolean turnOn) {
    for (WeakReference<ImageView> lampRF: LeftLightList
    ) {
      if (lampRF.get() != null) {
        if (turnOn) {
          lampRF.get().setVisibility(View.VISIBLE);
        } else {
          lampRF.get().setVisibility(View.INVISIBLE);
        }
      }
    }

    for (WeakReference<ImageView> lampRF: RightLightList
    ) {
      if (lampRF.get() != null) {
        if (turnOn) {
          lampRF.get().setVisibility(View.VISIBLE);
        } else {
          lampRF.get().setVisibility(View.INVISIBLE);
        }
      }
    }
  }



  public void turnOnLeft(){
    /*if (rightState.get()) { //sync left and right
      handler.removeCallbacks(rightRunner);
      turnOffOneSideLight(RightLightList);
      handler.post(rightRunner);
    }*/
    handler.removeCallbacks(leftRunner);
    handler.post(leftRunner);
    leftState.set(true);
  }

  public void turnOffLeft(){
    leftState.set(false);
    handler.removeCallbacks(leftRunner);
    turnOffOneSideLight(LeftLightList);
  }

  private void turnOnRight(){
    /*if (leftState.get()){//sync left and right
      handler.removeCallbacks(leftRunner);
      turnOffOneSideLight(LeftLightList);
      handler.post(leftRunner);
    }*/
    handler.removeCallbacks(rightRunner);
    handler.post(rightRunner);
    rightState.set(true);
  }

  private void turnOffRight(){
    rightState.set(false);
    handler.removeCallbacks(rightRunner);
    turnOffOneSideLight(RightLightList);
  }

  private void doubleFlash(){
    handler.post(flashRunner);
  }


  private Handler handler = new Handler();

  private PaoMaDeng leftRunner, rightRunner;

  private FlashRunner flashRunner;

  private void playAnimation(List<WeakReference<ImageView>> lampList){
    handler.post(new PaoMaDeng(lampList));
  }

  private class FlashRunner implements Runnable {
    private int number=0;
    @Override public void run() {
      if (number%2 == 0) {
        switchAllLight(true);
      } else {
        switchAllLight(false);
      }
      number++;
      handler.postDelayed(this, 500);
    }
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
      turnOffOneSideLight(lampList);
      handler.postDelayed(this, 150);
    }
  }

  private void turnOffOneSideLight(List<WeakReference<ImageView>> lampList){
    for (WeakReference<ImageView> lampRF: lampList
    ) {
      if (lampRF.get() != null) {
        lampRF.get().setVisibility(View.INVISIBLE);
      }
    }
  }
}

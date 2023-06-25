package com.longkai.stcarcontrol.st_exp.customView.oled2;

import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.longkai.stcarcontrol.st_exp.R;

import java.lang.ref.WeakReference;

public class OLED2Controller {
  WeakReference<Fragment>  fragmentRF;

  ImageView reversingIV, breakIV, positionIV; //memory leak

  OLED2LeftRightController oled2LeftRightController;

  public OLED2Controller(
      Fragment fragment,
      ImageView reversingIV,
      ImageView breakIV,
      ImageView positionIV
      ) {
    this.fragmentRF = new WeakReference<>(fragment);
    this.reversingIV = reversingIV;
    this.breakIV = breakIV;
    this.positionIV = positionIV;
    oled2LeftRightController = new OLED2LeftRightController(fragment);
  }

  private final Handler handler = new Handler();
  private final CircleRunner runner = new CircleRunner();

  public void updateState(OLEDState state){
    if (fragmentRF.get() != null){
      OLEDLeftRightState leftRightState = OLEDLeftRightState.ALL_OFF;

      if (state.reverseState) {
        reversingIV.setVisibility(View.VISIBLE);
//        leftRightState = OLEDLeftRightState.ALL_ON;
      } else {
        reversingIV.setVisibility(View.INVISIBLE);
        //leftRightState = OLEDLeftRightState.ALL_OFF;
      }

      if (state.brakeState) {
        breakIV.setVisibility(View.VISIBLE);
        positionIV.setImageResource(R.mipmap.ic_back_oled2_position_state1);
        positionIV.setVisibility(View.VISIBLE);
      }
      else {
        if (!state.positionState) { // position 也是关的
          breakIV.setVisibility(View.INVISIBLE);
          positionIV.setVisibility(View.INVISIBLE);
        }
      }

      if (state.positionState) {
        positionIV.setVisibility(View.VISIBLE);
        breakIV.setVisibility(View.VISIBLE);
        handler.postDelayed(runner, 500);
      } else {
        handler.removeCallbacks(runner);
        if (!state.brakeState) { // break 也是关的
          breakIV.setVisibility(View.INVISIBLE);
          positionIV.setVisibility(View.INVISIBLE);
        }
      }


      if (!state.reverseState) {
        if (state.turnRightState) {
          leftRightState = OLEDLeftRightState.RUN_RIGHT;
        }

        if (state.turnLeftState) {
          leftRightState = OLEDLeftRightState.RUN_LEFT;
        }

        if (state.turnRightState && state.turnLeftState) {
          leftRightState = OLEDLeftRightState.DOUBLE_FLASH;
        }
      }
      oled2LeftRightController.updateByState(leftRightState);
    }
  }

  public static class OLEDState {
    boolean reverseState;
    boolean brakeState;
    boolean positionState;
    boolean turnLeftState;
    boolean turnRightState;

    public OLEDState(boolean reverseState,
        boolean brakeState,
        boolean positionState,
        boolean turnLeftState,
        boolean turnRightState){
      this.brakeState = brakeState;
      this.positionState = positionState;
      this.reverseState = reverseState;
      this.turnLeftState = turnLeftState;
      this.turnRightState = turnRightState;
    }
  }

  private class CircleRunner implements Runnable {
    private int number = 1;
    @Override public void run() {
      if (number == 1) {
        positionIV.setImageResource(R.mipmap.ic_back_oled2_position_state1);
      } else if (number == 2) {
        positionIV.setImageResource(R.mipmap.ic_back_oled2_position_state2);
      } else if (number == 3) {
        positionIV.setImageResource(R.mipmap.ic_back_oled2_position_state3);
        number = 0;
      }
      number++;
      handler.postDelayed(this, 500);
    }
  }
}

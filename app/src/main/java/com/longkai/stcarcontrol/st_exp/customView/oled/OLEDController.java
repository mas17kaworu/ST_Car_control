package com.longkai.stcarcontrol.st_exp.customView.oled;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import java.lang.ref.WeakReference;

public class OLEDController {
  WeakReference<Fragment>  fragmentRF;

  ImageView reversingIV, breakIV, positionIV; //memory leak

  OLEDLeftRightController oledLeftRightController;

  public OLEDController(
      Fragment fragment,
      ImageView reversingIV,
      ImageView breakIV,
      ImageView positionIV
      ) {
    this.fragmentRF = new WeakReference<>(fragment);
    this.reversingIV = reversingIV;
    this.breakIV = breakIV;
    this.positionIV = positionIV;
    oledLeftRightController = new OLEDLeftRightController(fragment);
  }

  public void updateState(OLEDState state){
    if (fragmentRF.get() != null){
      OLEDLeftRightState leftRightState = OLEDLeftRightState.ALL_OFF;

      if (state.reverseState) {
        reversingIV.setVisibility(View.VISIBLE);
        leftRightState = OLEDLeftRightState.ALL_ON;
      } else {
        reversingIV.setVisibility(View.INVISIBLE);
        //leftRightState = OLEDLeftRightState.ALL_OFF;
      }

      if (state.breakState)
        breakIV.setVisibility(View.VISIBLE);
      else
        breakIV.setVisibility(View.INVISIBLE);

      if (state.positionState)
        positionIV.setVisibility(View.VISIBLE);
      else
        positionIV.setVisibility(View.INVISIBLE);


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
      oledLeftRightController.updateByState(leftRightState);
    }
  }

  public static class OLEDState {
    boolean reverseState;
    boolean breakState;
    boolean positionState;
    boolean turnLeftState;
    boolean turnRightState;

    public OLEDState(boolean reverseState,
        boolean breakState,
        boolean positionState,
        boolean turnLeftState,
        boolean turnRightState){
      this.breakState = breakState;
      this.positionState = positionState;
      this.reverseState = reverseState;
      this.turnLeftState = turnLeftState;
      this.turnRightState = turnRightState;
    }
  }
}

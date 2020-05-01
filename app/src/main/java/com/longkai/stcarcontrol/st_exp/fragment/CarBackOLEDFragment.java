package com.longkai.stcarcontrol.st_exp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.longkai.stcarcontrol.st_exp.ConstantData;
import com.longkai.stcarcontrol.st_exp.R;
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDOLEDBackList.CMDOLEDAuto1;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDOLEDBackList.CMDOLEDAuto2;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDOLEDBackList.CMDOLEDAuto3;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDOLEDBackList.CMDOLEDBase;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDOLEDBackList.CMDOLEDBrake;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDOLEDBackList.CMDOLEDPosition;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDOLEDBackList.CMDOLEDReversing;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDOLEDBackList.CMDOLEDTurnLeft;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDOLEDBackList.CMDOLEDTurnRight;
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter;
import com.longkai.stcarcontrol.st_exp.customView.oled.OLEDController;

import static com.longkai.stcarcontrol.st_exp.ConstantData.sBackOLEDAuto1;
import static com.longkai.stcarcontrol.st_exp.ConstantData.sBackOLEDAuto2;
import static com.longkai.stcarcontrol.st_exp.ConstantData.sBackOLEDAuto3;
import static com.longkai.stcarcontrol.st_exp.ConstantData.sBackOLEDBreak;
import static com.longkai.stcarcontrol.st_exp.ConstantData.sBackOLEDPosition;
import static com.longkai.stcarcontrol.st_exp.ConstantData.sBackOLEDReverse;
import static com.longkai.stcarcontrol.st_exp.ConstantData.sBackOLEDTurnLeft;
import static com.longkai.stcarcontrol.st_exp.ConstantData.sBackOLEDTurnRight;
import static com.longkai.stcarcontrol.st_exp.ConstantData.sDoorFadeZoneLeftLamp;

public class CarBackOLEDFragment extends Fragment implements View.OnClickListener {

  private View mView;

  private ImageView ivReversing, ivBrake, ivPosition, ivTurnLeft, ivTurnRight, ivAuto1, ivAuto2, ivAuto3;

  private OLEDController oledController;


  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    mView = inflater.inflate(R.layout.fragment_car_back_oled, container, false);
    ivReversing = (ImageView) mView.findViewById(R.id.btn_back_oled_reversing);
    ivReversing.setOnClickListener(this);
    ivBrake = (ImageView) mView.findViewById(R.id.btn_back_oled_break);
    ivBrake.setOnClickListener(this);
    ivPosition = (ImageView) mView.findViewById(R.id.btn_back_oled_position);
    ivPosition.setOnClickListener(this);
    ivTurnLeft = (ImageView) mView.findViewById(R.id.btn_back_oled_left);
    ivTurnLeft.setOnClickListener(this);
    ivTurnRight = (ImageView) mView.findViewById(R.id.btn_back_oled_right);
    ivTurnRight.setOnClickListener(this);
    ivAuto1 = (ImageView) mView.findViewById(R.id.btn_back_oled_a1);
    ivAuto1.setOnClickListener(this);
    ivAuto2 = (ImageView) mView.findViewById(R.id.btn_back_oled_a2);
    ivAuto2.setOnClickListener(this);
    ivAuto3 = (ImageView) mView.findViewById(R.id.btn_back_oled_a3);
    ivAuto3.setOnClickListener(this);

    oledController = new OLEDController(
        this,
        (ImageView)mView.findViewById(R.id.iv_oled_reverse),
        (ImageView)mView.findViewById(R.id.iv_oled_break),
        (ImageView)mView.findViewById(R.id.iv_oled_position)
    );

    return mView;
  }


  @Override public void onClick(View view) {
    switch (view.getId()){
      case R.id.btn_back_oled_reversing:
        clickBtn(sBackOLEDReverse, ivReversing, new CMDOLEDReversing());
        //ServiceManager.getInstance().sendCommandToCar(new CMDOLEDReversing(), new CommandListenerAdapter());
        break;
      case R.id.btn_back_oled_break:
        clickBtn(sBackOLEDBreak, ivBrake, new CMDOLEDBrake());
        //ServiceManager.getInstance().sendCommandToCar(new CMDOLEDBrake(), new CommandListenerAdapter());
        break;
      case R.id.btn_back_oled_position:
        clickBtn(sBackOLEDPosition, ivPosition, new CMDOLEDPosition());
        //ServiceManager.getInstance().sendCommandToCar(new CMDOLEDPosition(), new CommandListenerAdapter());
        break;
      case R.id.btn_back_oled_left:
        clickBtn(sBackOLEDTurnLeft, ivTurnLeft, new CMDOLEDTurnLeft());
        //ServiceManager.getInstance().sendCommandToCar(new CMDOLEDTurnLeft(), new CommandListenerAdapter());
        break;
      case R.id.btn_back_oled_right:
        clickBtn(sBackOLEDTurnRight, ivTurnRight, new CMDOLEDTurnRight());
        //ServiceManager.getInstance().sendCommandToCar(new CMDOLEDTurnRight(), new CommandListenerAdapter());
        break;
      case R.id.btn_back_oled_a1:
        clickBtn(sBackOLEDAuto1, ivAuto1, new CMDOLEDAuto1());
        //ServiceManager.getInstance().sendCommandToCar(new CMDOLEDAuto1(), new CommandListenerAdapter());
        break;
      case R.id.btn_back_oled_a2:
        clickBtn(sBackOLEDAuto2, ivAuto2, new CMDOLEDAuto2());
        //ServiceManager.getInstance().sendCommandToCar(new CMDOLEDAuto2(), new CommandListenerAdapter());
        break;
      case R.id.btn_back_oled_a3:
        clickBtn(sBackOLEDAuto3, ivAuto3,new CMDOLEDAuto3());
        //ServiceManager.getInstance().sendCommandToCar(new CMDOLEDAuto3(), new CommandListenerAdapter());
    }

    refreshUI();
  }

  private void clickBtn(int index, ImageView view, BaseCommand command) {
    if (ConstantData.sBackOLEDStatus[index] == 1) {
      //ConstantData.sBackOLEDStatus[index] = 0;
      command.turnOff();
      //view.setSelected(false);
    } else {
      //ConstantData.sBackOLEDStatus[index] = 1;
      command.turnOn();
      //view.setSelected(true);
    }
    ServiceManager.getInstance().sendCommandToCar(command, new CommandListenerAdapter());
  }

  private void refreshUI(){
    oledController.updateState(updateStateByCMD());

    byte[] cmdPayload = CMDOLEDBase.getPayload();
    if ((cmdPayload[0] & CMDOLEDBase.Reversing) != 0) {
      ivReversing.setSelected(true);
      ConstantData.sBackOLEDStatus[sBackOLEDReverse] = 1;
    }
    else{
      ivReversing.setSelected(false);
      ConstantData.sBackOLEDStatus[sBackOLEDReverse] = 0;
    }


    if ((cmdPayload[0] & CMDOLEDBase.Brake) != 0) {
      ivBrake.setSelected(true);
      ConstantData.sBackOLEDStatus[sBackOLEDBreak] = 1;
    }
    else {
      ivBrake.setSelected(false);
      ConstantData.sBackOLEDStatus[sBackOLEDBreak] = 0;
    }

    if((cmdPayload[0] & CMDOLEDBase.Position) !=0) {
      ivPosition.setSelected(true);
      ConstantData.sBackOLEDStatus[sBackOLEDPosition] = 1;
    }
    else {
      ivPosition.setSelected(false);
      ConstantData.sBackOLEDStatus[sBackOLEDPosition] = 0;
    }

    if((cmdPayload[0] & CMDOLEDBase.TurnLeft) != 0) {
      ivTurnLeft.setSelected(true);
      ConstantData.sBackOLEDStatus[sBackOLEDTurnLeft] = 1;
    }
    else {
      ivTurnLeft.setSelected(false);
      ConstantData.sBackOLEDStatus[sBackOLEDTurnLeft] = 0;
    }

    if ((cmdPayload[0] & CMDOLEDBase.TurnRight) != 0) {
      ivTurnRight.setSelected(true);
      ConstantData.sBackOLEDStatus[sBackOLEDTurnRight] = 1;
    }
    else {
      ivTurnRight.setSelected(false);
      ConstantData.sBackOLEDStatus[sBackOLEDTurnRight] = 0;
    }

    if ((cmdPayload[0] & CMDOLEDBase.AutoRun1) != 0) {
      ivAuto1.setSelected(true);
      ConstantData.sBackOLEDStatus[sBackOLEDAuto1] = 1;
    } else {
      ivAuto1.setSelected(false);
      ConstantData.sBackOLEDStatus[sBackOLEDAuto1] = 0;
    }

    if ((cmdPayload[0] & CMDOLEDBase.AutoRun2) != 0) {
      ivAuto2.setSelected(true);
      ConstantData.sBackOLEDStatus[sBackOLEDAuto2] = 1;
    } else {
      ivAuto2.setSelected(false);
      ConstantData.sBackOLEDStatus[sBackOLEDAuto2] = 0;
    }

    if ((cmdPayload[0] & CMDOLEDBase.AutoRun3) != 0) {
      ivAuto3.setSelected(true);
      ConstantData.sBackOLEDStatus[sBackOLEDAuto3] = 1;
    } else {
      ivAuto3.setSelected(false);
      ConstantData.sBackOLEDStatus[sBackOLEDAuto3] = 0;
    }
  }

  /**
   *     protected static final byte TurnLeft = (byte)0x10;
   *     protected static final byte TurnRight = (byte)0x08;
   *     protected static final byte Position = (byte)0x04;
   *     protected static final byte Brake    = (byte)0x02;
   *     protected static final byte Reversing = (byte)0x01;
   *
   *     protected static final byte AutoRun1 = (byte)0x20;
   *     protected static final byte AutoRun2 = (byte)0x40;
   *     protected static final byte AutoRun3 = (byte)0x80;
   *
   * @return
   */
  private OLEDController.OLEDState updateStateByCMD(){
    byte[] cmdPayload = CMDOLEDBase.getPayload();
    return new OLEDController.OLEDState(
        (cmdPayload[0] & CMDOLEDBase.Reversing) != 0,
        (cmdPayload[0] & CMDOLEDBase.Brake) != 0,
        (cmdPayload[0] & CMDOLEDBase.Position) != 0,
        (cmdPayload[0] & CMDOLEDBase.TurnLeft) != 0,
        (cmdPayload[0] & CMDOLEDBase.TurnRight) != 0
    );
  }
}

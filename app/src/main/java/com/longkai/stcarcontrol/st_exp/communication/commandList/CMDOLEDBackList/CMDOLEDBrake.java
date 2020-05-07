package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDOLEDBackList;

public class CMDOLEDBrake extends CMDOLEDBase {
  public CMDOLEDBrake(){
    super();
  }

  //示宽灯与刹车灯互斥；
  @Override
  public void turnOn(){
    payload[0] &= ~(Reversing);
    payload[0] &= ~(Position);
    payload[0] &= ~(AutoRun1);
    payload[0] &= ~(AutoRun2);
    payload[0] &= ~(AutoRun3);

    payload[0] |= Brake;
    refreshDataPayload();
  }
  @Override
  public void turnOff(){
    payload[0] &= ~(Brake);
    refreshDataPayload();
  }
}

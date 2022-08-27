package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDOLEDBackList;

public class CMDOLEDStopAll extends CMDOLEDBase{
  public CMDOLEDStopAll() {
    super();
  }

  @Override
  public void turnOn(){
    payload[0] = 0x00;
    stopAll = true;
    refreshDataPayload();
  }

  @Override public void turnOff() {
    stopAll = false;
  }
}

package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDOLEDBackList;

public class CMDOLEDAuto2 extends CMDOLEDBase{
   public CMDOLEDAuto2() {
      super();
   }
   @Override
	public void turnOn(){
          payload[0] |= AutoRun2;
          refreshDataPayload();
   }
   @Override
	public void turnOff(){
          payload[0] &= ~(AutoRun2);
          refreshDataPayload();
   }
}

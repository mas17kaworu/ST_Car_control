package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDOLEDBackList;

public class CMDOLEDAuto1 extends CMDOLEDBase{
   public CMDOLEDAuto1() {
      super();
   }
   @Override
	public void turnOn(){
     payload[0] = 0;
          payload[0] |= AutoRun1;
          refreshDataPayload();
   }
   @Override
	public void turnOff(){
          payload[0] &= ~(AutoRun1);
          refreshDataPayload();
   }
}

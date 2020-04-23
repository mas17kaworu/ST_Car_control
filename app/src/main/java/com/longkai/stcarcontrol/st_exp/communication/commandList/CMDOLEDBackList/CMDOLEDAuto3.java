package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDOLEDBackList;

public class CMDOLEDAuto3 extends CMDOLEDBase{
   public CMDOLEDAuto3() {
      super();
   }
   @Override
	public void turnOn(){
          payload[0] |= AutoRun3;
          refreshDataPayload();
   }
   @Override
	public void turnOff(){
          payload[0] &= ~(AutoRun3);
          refreshDataPayload();
   }
}

package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDOLEDBackList;

public class CMDOLEDReversing extends CMDOLEDBase{
   public CMDOLEDReversing() {
      super();
   }
   //倒车灯需与其他按钮互斥；
   @Override
	public void turnOn(){
          payload[0] = 0;

          payload[0] |= Reversing;
          refreshDataPayload();
   }
   @Override
	public void turnOff(){
          payload[0] &= ~(Reversing);
          refreshDataPayload();
   }
}

package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList;
public class CMDLEDHeadLampHighway extends CMDLEDHeadLamp{
   public CMDLEDHeadLampHighway() {
      super();
      payload[1] = 0;
   }
   @Override
	public void turnOn(){
          payload[1] |= Highway;
   }
   @Override
	public void turnOff(){
          payload[1] &= ~(Highway);
   }
}

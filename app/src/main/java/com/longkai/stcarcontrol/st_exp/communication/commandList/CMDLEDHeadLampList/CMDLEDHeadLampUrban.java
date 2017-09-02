package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList;
public class CMDLEDHeadLampUrban extends CMDLEDHeadLamp{
   public CMDLEDHeadLampUrban() {
      super();
      payload[1] = 0;
   }
   @Override
	public void turnOn(){
          payload[1] |= Urban;
   }
   @Override
	public void turnOff(){
          payload[1] &= ~(Urban);
   }
}

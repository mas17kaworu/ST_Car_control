package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList;
public class CMDLEDHeadLampHBLED4 extends CMDLEDHeadLamp{
   public CMDLEDHeadLampHBLED4() {
      super();
   }
   @Override
	public void turnOn(){
          payload[2] |= HBLED4;
   }
   @Override
	public void turnOff(){
          payload[2] &= ~(HBLED4);
   }
}

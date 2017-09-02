package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList;
public class CMDLEDHeadLampHBLED6 extends CMDLEDHeadLamp{
   public CMDLEDHeadLampHBLED6() {
      super();
   }
   @Override
	public void turnOn(){
          payload[2] |= HBLED6;
   }
   @Override
	public void turnOff(){
          payload[2] &= ~(HBLED6);
   }
}

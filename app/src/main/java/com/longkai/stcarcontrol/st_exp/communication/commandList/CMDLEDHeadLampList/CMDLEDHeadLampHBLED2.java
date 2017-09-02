package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList;
public class CMDLEDHeadLampHBLED2 extends CMDLEDHeadLamp{
   public CMDLEDHeadLampHBLED2() {
      super();
   }
   @Override
	public void turnOn(){
          payload[2] |= HBLED2;
   }
   @Override
	public void turnOff(){
          payload[2] &= ~(HBLED2);
   }
}

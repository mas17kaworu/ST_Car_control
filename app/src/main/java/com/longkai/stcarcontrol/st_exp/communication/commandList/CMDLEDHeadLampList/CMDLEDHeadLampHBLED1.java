package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList;
public class CMDLEDHeadLampHBLED1 extends CMDLEDHeadLamp{
   public CMDLEDHeadLampHBLED1() {
      super();
   }
   @Override
	public void turnOn(){
          payload[2] |= HBLED1;
          refreshDataPayload();
   }
   @Override
	public void turnOff(){
          payload[2] &= ~(HBLED1);
          refreshDataPayload();
   }
}

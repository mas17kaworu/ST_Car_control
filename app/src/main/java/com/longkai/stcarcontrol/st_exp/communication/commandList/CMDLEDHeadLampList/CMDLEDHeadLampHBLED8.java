package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList;
public class CMDLEDHeadLampHBLED8 extends CMDLEDHeadLamp{
   public CMDLEDHeadLampHBLED8() {
      super();
   }
   @Override
	public void turnOn(){
          payload[2] |= HBLED8;
          refreshDataPayload();
   }
   @Override
	public void turnOff(){
          payload[2] &= ~(HBLED8);
          refreshDataPayload();
   }
}

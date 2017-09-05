package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList;
public class CMDLEDHeadLampHBLED3 extends CMDLEDHeadLamp{
   public CMDLEDHeadLampHBLED3() {
      super();
   }
   @Override
	public void turnOn(){
          payload[2] |= HBLED3;
          refreshDataPayload();
   }
   @Override
	public void turnOff(){
          payload[2] &= ~(HBLED3);
          refreshDataPayload();
   }
}

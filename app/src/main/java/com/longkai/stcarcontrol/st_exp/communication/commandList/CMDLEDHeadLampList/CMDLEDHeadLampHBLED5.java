package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList;
public class CMDLEDHeadLampHBLED5 extends CMDLEDHeadLamp{
   public CMDLEDHeadLampHBLED5() {
      super();
   }
   @Override
	public void turnOn(){
          payload[2] |= HBLED5;
          refreshDataPayload();
   }
   @Override
	public void turnOff(){
          payload[2] &= ~(HBLED5);
          refreshDataPayload();
   }
}

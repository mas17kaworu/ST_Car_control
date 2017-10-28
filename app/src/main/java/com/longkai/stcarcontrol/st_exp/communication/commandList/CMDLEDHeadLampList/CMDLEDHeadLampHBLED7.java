package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList;
public class CMDLEDHeadLampHBLED7 extends CMDLEDHeadLamp{
   public CMDLEDHeadLampHBLED7() {
      super();
   }
   @Override
	public void turnOn(){
          payload[2] |= HBLED7;
          refreshDataPayload();
   }
   @Override
	public void turnOff(){
          payload[2] &= ~(HBLED7);
          refreshDataPayload();
   }
}

package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList;
public class CMDLEDHeadLampDRLLight extends CMDLEDHeadLamp{
   public CMDLEDHeadLampDRLLight() {
      super();
   }
   @Override
	public void turnOn(){
          payload[0] |= DRLLight;
   }
   @Override
	public void turnOff(){
          payload[0] &= ~(DRLLight);
   }
}

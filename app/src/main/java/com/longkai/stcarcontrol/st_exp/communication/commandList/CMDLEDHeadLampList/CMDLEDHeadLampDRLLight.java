package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList;
public class CMDLEDHeadLampDRLLight extends CMDLEDHeadLamp{
   public CMDLEDHeadLampDRLLight() {
      super();
      payload[1] = 0;
   }
   @Override
	public void turnOn(){
          payload[1] |= DRLLight;
          refreshDataPayload();
   }
   @Override
	public void turnOff(){
          payload[1] &= ~(DRLLight);
          refreshDataPayload();
   }
}

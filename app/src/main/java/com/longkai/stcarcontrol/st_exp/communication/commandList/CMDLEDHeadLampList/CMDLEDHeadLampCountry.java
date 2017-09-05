package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList;
public class CMDLEDHeadLampCountry extends CMDLEDHeadLamp{
   public CMDLEDHeadLampCountry() {
      super();
      payload[1] = 0;
   }
   @Override
	public void turnOn(){
          payload[1] |= Country;
          refreshDataPayload();
   }
   @Override
	public void turnOff(){
          payload[1] &= ~(Country);
          refreshDataPayload();
   }
}

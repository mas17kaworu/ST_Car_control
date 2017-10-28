package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList;
public class CMDLEDHeadLampHBLEDR4 extends CMDLEDHeadLamp{
   public CMDLEDHeadLampHBLEDR4() {
      super();
   }
   @Override
	public void turnOn(){
          payload[3] |= HBLEDR4;
          refreshDataPayload();
   }
   @Override
	public void turnOff(){
          payload[3] &= ~(HBLEDR4);
          refreshDataPayload();
   }
}

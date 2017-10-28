package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList;
public class CMDLEDHeadLampHBLEDR5 extends CMDLEDHeadLamp{
   public CMDLEDHeadLampHBLEDR5() {
      super();
   }
   @Override
	public void turnOn(){
          payload[3] |= HBLEDR5;
          refreshDataPayload();
   }
   @Override
	public void turnOff(){
          payload[3] &= ~(HBLEDR5);
          refreshDataPayload();
   }
}

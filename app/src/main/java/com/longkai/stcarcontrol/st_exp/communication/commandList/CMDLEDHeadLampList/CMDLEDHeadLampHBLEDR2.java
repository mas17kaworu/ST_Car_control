package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList;
public class CMDLEDHeadLampHBLEDR2 extends CMDLEDHeadLamp{
   public CMDLEDHeadLampHBLEDR2() {
      super();
   }
   @Override
	public void turnOn(){
          payload[3] |= HBLEDR2;
          refreshDataPayload();
   }
   @Override
	public void turnOff(){
          payload[3] &= ~(HBLEDR2);
          refreshDataPayload();
   }
}

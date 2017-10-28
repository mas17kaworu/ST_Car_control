package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList;
public class CMDLEDHeadLampHBLEDR1 extends CMDLEDHeadLamp{
   public CMDLEDHeadLampHBLEDR1() {
      super();
   }
   @Override
	public void turnOn(){
          payload[3] |= HBLEDR1;
          refreshDataPayload();
   }
   @Override
	public void turnOff(){
          payload[3] &= ~(HBLEDR1);
          refreshDataPayload();
   }
}

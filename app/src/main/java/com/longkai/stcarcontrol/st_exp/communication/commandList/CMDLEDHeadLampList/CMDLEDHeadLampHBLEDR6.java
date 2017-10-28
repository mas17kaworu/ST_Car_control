package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList;
public class CMDLEDHeadLampHBLEDR6 extends CMDLEDHeadLamp{
   public CMDLEDHeadLampHBLEDR6() {
      super();
   }
   @Override
	public void turnOn(){
          payload[3] |= HBLEDR6;
          refreshDataPayload();
   }
   @Override
	public void turnOff(){
          payload[3] &= ~(HBLEDR6);
          refreshDataPayload();
   }
}

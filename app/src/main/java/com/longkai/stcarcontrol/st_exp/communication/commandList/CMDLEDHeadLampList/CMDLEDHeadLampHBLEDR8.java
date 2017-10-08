package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList;
public class CMDLEDHeadLampHBLEDR8 extends CMDLEDHeadLamp{
   public CMDLEDHeadLampHBLEDR8() {
      super();
   }
   @Override
	public void turnOn(){
          payload[3] |= HBLEDR8;
          refreshDataPayload();
   }
   @Override
	public void turnOff(){
          payload[3] &= ~(HBLEDR8);
          refreshDataPayload();
   }
}

package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList;
public class CMDLEDHeadLampHBLEDR3 extends CMDLEDHeadLamp{
   public CMDLEDHeadLampHBLEDR3() {
      super();
   }
   @Override
	public void turnOn(){
          payload[3] |= HBLEDR3;
          refreshDataPayload();
   }
   @Override
	public void turnOff(){
          payload[3] &= ~(HBLEDR3);
          refreshDataPayload();
   }
}

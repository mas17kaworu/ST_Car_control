package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList;
public class CMDLEDHeadLampHBLEDR7 extends CMDLEDHeadLamp{
   public CMDLEDHeadLampHBLEDR7() {
      super();
   }
   @Override
	public void turnOn(){
          payload[3] |= HBLEDR7;
          refreshDataPayload();
   }
   @Override
	public void turnOff(){
          payload[3] &= ~(HBLEDR7);
          refreshDataPayload();
   }
}

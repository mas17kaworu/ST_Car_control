package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList;
public class CMDLEDHeadLampHBAll extends CMDLEDHeadLamp{
   public CMDLEDHeadLampHBAll() {
      super();
   }
   @Override
	public void turnOn(){
          payload[0] |= HBAll;
          refreshDataPayload();
   }
   @Override
	public void turnOff(){
          payload[0] &= ~(HBAll);
          refreshDataPayload();
   }
}

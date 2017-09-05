package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList;
public class CMDLEDHeadLampCorner extends CMDLEDHeadLamp{
   public CMDLEDHeadLampCorner() {
      super();
   }
   @Override
	public void turnOn(){
          payload[0] |= Corner;
          refreshDataPayload();
   }
   @Override
	public void turnOff(){
          payload[0] &= ~(Corner);
          refreshDataPayload();
   }
}

package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList;
public class CMDLEDHeadLampLeftCorner extends CMDLEDHeadLamp{
   public CMDLEDHeadLampLeftCorner() {
      super();
   }
   @Override
	public void turnOn(){
          payload[0] |= LeftCorner;
          refreshDataPayload();
   }
   @Override
	public void turnOff(){
          payload[0] &= ~(LeftCorner);
          refreshDataPayload();
   }
}

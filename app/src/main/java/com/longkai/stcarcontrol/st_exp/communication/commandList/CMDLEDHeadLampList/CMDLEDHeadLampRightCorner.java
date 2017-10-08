package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList;
public class CMDLEDHeadLampRightCorner extends CMDLEDHeadLamp{
   public CMDLEDHeadLampRightCorner() {
      super();
   }
   @Override
	public void turnOn(){
          payload[0] |= RightCorner;
          refreshDataPayload();
   }
   @Override
	public void turnOff(){
          payload[0] &= ~(RightCorner);
          refreshDataPayload();
   }
}

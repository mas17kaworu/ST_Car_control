package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList;
public class CMDLEDHeadLampPosition extends CMDLEDHeadLamp{
   public CMDLEDHeadLampPosition() {
      super();
   }
   @Override
	public void turnOn(){
          payload[0] |= Position;
          refreshDataPayload();
   }
   @Override
	public void turnOff(){
          payload[0] &= ~(Position);
          refreshDataPayload();
   }
}

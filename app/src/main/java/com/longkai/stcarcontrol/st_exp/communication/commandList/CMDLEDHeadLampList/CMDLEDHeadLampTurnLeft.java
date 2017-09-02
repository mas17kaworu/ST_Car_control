package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList;
public class CMDLEDHeadLampTurnLeft extends CMDLEDHeadLamp{
   public CMDLEDHeadLampTurnLeft() {
      super();
   }
   @Override
	public void turnOn(){
          payload[0] |= TurnLeft;
   }
   @Override
	public void turnOff(){
          payload[0] &= ~(TurnLeft);
   }
}

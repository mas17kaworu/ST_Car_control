package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList;
public class CMDLEDHeadLampTurnRight extends CMDLEDHeadLamp{
   public CMDLEDHeadLampTurnRight() {
      super();
   }
   @Override
	public void turnOn(){
          payload[0] |= TurnRight;
   }
   @Override
	public void turnOff(){
          payload[0] &= ~(TurnRight);
   }
}

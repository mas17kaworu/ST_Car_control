package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDBCMRearLampList;
public class CMDBCMRearLampTurnLeft extends CMDBCMRearLamp{
   public CMDBCMRearLampTurnLeft() {
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

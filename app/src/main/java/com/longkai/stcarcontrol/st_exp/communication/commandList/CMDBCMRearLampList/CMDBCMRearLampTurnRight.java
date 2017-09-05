package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDBCMRearLampList;
public class CMDBCMRearLampTurnRight extends CMDBCMRearLamp{
   public CMDBCMRearLampTurnRight() {
      super();
   }
   @Override
	public void turnOn(){
          payload[0] |= TurnRight;
          refreshDataPayload();
   }
   @Override
	public void turnOff(){
          payload[0] &= ~(TurnRight);
          refreshDataPayload();
   }
}

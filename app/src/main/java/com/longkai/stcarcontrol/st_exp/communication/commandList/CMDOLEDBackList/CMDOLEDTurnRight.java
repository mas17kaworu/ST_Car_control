package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDOLEDBackList;

import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDBCMRearLampList.CMDBCMRearLamp;

public class CMDOLEDTurnRight extends CMDOLEDBase{
   public CMDOLEDTurnRight() {
      super();
   }
   @Override
	public void turnOn(){
     payload[0] &= ~(Reversing);
          payload[0] |= TurnRight;
          refreshDataPayload();
   }
   @Override
	public void turnOff(){
          payload[0] &= ~(TurnRight);
          refreshDataPayload();
   }
}

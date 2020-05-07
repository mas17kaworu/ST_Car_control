package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDOLEDBackList;

public class CMDOLEDPosition extends CMDOLEDBase{
   public CMDOLEDPosition() {
      super();
   }
   @Override
	public void turnOn(){
     payload[0] &= ~(Reversing);
     payload[0] &= ~(Brake);
     payload[0] &= ~(AutoRun1);
     payload[0] &= ~(AutoRun2);
     payload[0] &= ~(AutoRun3);

          payload[0] |= Position;
          refreshDataPayload();
   }
   @Override
	public void turnOff(){
          payload[0] &= ~(Position);
          refreshDataPayload();
   }
}

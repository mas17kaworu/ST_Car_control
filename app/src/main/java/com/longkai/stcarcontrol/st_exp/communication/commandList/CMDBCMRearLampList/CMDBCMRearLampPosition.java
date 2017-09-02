package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDBCMRearLampList;
public class CMDBCMRearLampPosition extends CMDBCMRearLamp{
   public CMDBCMRearLampPosition() {
      super();
   }
   @Override
	public void turnOn(){
          payload[0] |= Position;
   }
   @Override
	public void turnOff(){
          payload[0] &= ~(Position);
   }
}

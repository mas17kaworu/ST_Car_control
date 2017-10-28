package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDBCMRearLampList;
public class CMDBCMRearLampBrake extends CMDBCMRearLamp{
   public CMDBCMRearLampBrake() {
      super();
   }
   @Override
	public void turnOn(){
          payload[0] |= Brake;
          refreshDataPayload();
   }
   @Override
	public void turnOff(){
          payload[0] &= ~(Brake);
          refreshDataPayload();
   }
}

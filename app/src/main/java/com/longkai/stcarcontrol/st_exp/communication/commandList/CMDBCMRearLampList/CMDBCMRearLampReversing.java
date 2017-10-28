package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDBCMRearLampList;
public class CMDBCMRearLampReversing extends CMDBCMRearLamp{
   public CMDBCMRearLampReversing() {
      super();
   }
   @Override
	public void turnOn(){
          payload[0] |= Reversing;
          refreshDataPayload();
   }
   @Override
	public void turnOff(){
          payload[0] &= ~(Reversing);
          refreshDataPayload();
   }
}

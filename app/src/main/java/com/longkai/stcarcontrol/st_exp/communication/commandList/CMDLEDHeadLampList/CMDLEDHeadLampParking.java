package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList;
public class CMDLEDHeadLampParking extends CMDLEDHeadLamp{
   public CMDLEDHeadLampParking() {
      super();
      payload[1] = 0;
   }
   @Override
	public void turnOn(){
          payload[1] |= Parking;
          refreshDataPayload();
   }
   @Override
	public void turnOff(){
          payload[1] &= ~(Parking);
          refreshDataPayload();
   }
}

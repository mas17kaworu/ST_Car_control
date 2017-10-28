package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList;
public class CMDLEDHeadLampEnergySave extends CMDLEDHeadLamp{
   public CMDLEDHeadLampEnergySave() {
      super();
      payload[1] = 0;
   }
   @Override
	public void turnOn(){
          payload[1] |= EnergySave;
          refreshDataPayload();
   }
   @Override
	public void turnOff(){
          payload[1] &= ~(EnergySave);
          refreshDataPayload();
   }
}

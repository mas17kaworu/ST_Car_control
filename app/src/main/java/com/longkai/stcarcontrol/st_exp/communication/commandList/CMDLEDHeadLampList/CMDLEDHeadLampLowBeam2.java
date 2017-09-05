package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList;
public class CMDLEDHeadLampLowBeam2 extends CMDLEDHeadLamp{
   public CMDLEDHeadLampLowBeam2() {
      super();
   }
   @Override
	public void turnOn(){
          payload[0] |= LowBeam2;
          refreshDataPayload();
   }
   @Override
	public void turnOff(){
          payload[0] &= ~(LowBeam2);
          refreshDataPayload();
   }
}

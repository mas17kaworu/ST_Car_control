package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList;
public class CMDLEDHeadLampCurve extends CMDLEDHeadLamp{
   public CMDLEDHeadLampCurve() {
      super();
      payload[1] = 0;
   }
   @Override
	public void turnOn(){
          payload[1] |= Curve;
          refreshDataPayload();
   }
   @Override
	public void turnOff(){
          payload[1] &= ~(Curve);
          refreshDataPayload();
   }
}

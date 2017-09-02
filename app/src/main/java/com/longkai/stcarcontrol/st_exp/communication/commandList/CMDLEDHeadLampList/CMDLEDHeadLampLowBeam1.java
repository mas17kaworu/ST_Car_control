package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList;
public class CMDLEDHeadLampLowBeam1 extends CMDLEDHeadLamp{
   public CMDLEDHeadLampLowBeam1() {
      super();
   }
   @Override
	public void turnOn(){
          payload[0] |= LowBeam1;
   }
   @Override
	public void turnOff(){
          payload[0] &= ~(LowBeam1);
   }
}

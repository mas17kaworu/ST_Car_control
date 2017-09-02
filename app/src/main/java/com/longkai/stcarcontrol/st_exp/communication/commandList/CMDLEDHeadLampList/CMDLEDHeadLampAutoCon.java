package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList;
public class CMDLEDHeadLampAutoCon extends CMDLEDHeadLamp{
   public CMDLEDHeadLampAutoCon() {
      super();
      payload[1] = 0;
   }
   @Override
	public void turnOn(){
          payload[1] |= AutoCon;
   }
   @Override
	public void turnOff(){
          payload[1] &= ~(AutoCon);
   }
}

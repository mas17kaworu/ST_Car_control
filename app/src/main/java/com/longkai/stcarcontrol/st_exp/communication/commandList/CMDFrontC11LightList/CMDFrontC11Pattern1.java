package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDFrontC11LightList;

public class CMDFrontC11Pattern1 extends CMDFrontC11Light {
   public CMDFrontC11Pattern1() {
      super();
      payload[0] = 0;
   }
   @Override
	public void turnOn(){
          payload[0] |= CMDFrontC11Light.Pattern1;
          refreshDataPayload();
   }
   @Override
	public void turnOff(){
          payload[0] &= ~(CMDFrontC11Light.Pattern1);
          refreshDataPayload();
   }
}

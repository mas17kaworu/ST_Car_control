package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDFrontC11LightList;

import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDLEDHeadLampList.CMDLEDHeadLamp;

public class CMDFrontC11Pattern8 extends CMDFrontC11Light {
   public CMDFrontC11Pattern8() {
      super();
      payload[0] = 0;
   }
   @Override
	public void turnOn(){
       payload[0] = 0;
          payload[0] |= CMDFrontC11Light.Pattern8;
          refreshDataPayload();
   }
   @Override
	public void turnOff(){
          payload[0] &= ~(CMDFrontC11Light.Pattern8);
          refreshDataPayload();
   }
}

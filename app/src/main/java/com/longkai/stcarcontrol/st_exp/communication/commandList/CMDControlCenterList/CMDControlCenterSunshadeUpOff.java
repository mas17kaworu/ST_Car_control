package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDControlCenterList;
public class CMDControlCenterSunshadeUpOff extends CMDControlCenter{
   public CMDControlCenterSunshadeUpOff(){
       super();
       payload[0] &= ~(SunshadeUp);
       refreshDataPayload();
   }
}

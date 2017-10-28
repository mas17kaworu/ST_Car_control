package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDControlCenterList;
public class CMDControlCenterSunshadeDownOn extends CMDControlCenter{
   public CMDControlCenterSunshadeDownOn(){
       super();
       payload[0] |= SunshadeDown;
       refreshDataPayload();
   }
}

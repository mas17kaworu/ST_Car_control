package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDControlCenterList;
public class CMDControlCenterSunshadeUpOn extends CMDControlCenter{
   public CMDControlCenterSunshadeUpOn(){
       super();
       payload[0] |= SunshadeUp;
   }
}

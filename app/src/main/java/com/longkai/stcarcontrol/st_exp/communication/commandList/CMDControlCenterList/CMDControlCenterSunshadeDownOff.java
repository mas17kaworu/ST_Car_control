package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDControlCenterList;
public class CMDControlCenterSunshadeDownOff extends CMDControlCenter{
   public CMDControlCenterSunshadeDownOff(){
       super();
       payload[0] &= ~(SunshadeDown);
   }
}

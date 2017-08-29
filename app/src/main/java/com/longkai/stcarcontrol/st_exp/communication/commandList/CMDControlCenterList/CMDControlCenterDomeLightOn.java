package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDControlCenterList;
public class CMDControlCenterDomeLightOn extends CMDControlCenter{
   public CMDControlCenterDomeLightOn(){
       super();
       payload[0] |= DomeLight;
   }
}

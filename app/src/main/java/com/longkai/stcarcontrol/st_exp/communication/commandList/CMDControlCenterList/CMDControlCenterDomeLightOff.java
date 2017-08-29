package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDControlCenterList;
public class CMDControlCenterDomeLightOff extends CMDControlCenter{
   public CMDControlCenterDomeLightOff(){
       super();
       payload[0] &= ~(DomeLight);
   }
}

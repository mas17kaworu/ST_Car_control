package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDControlCenterList;
public class CMDControlCenterCentrallockOff extends CMDControlCenter{
   public CMDControlCenterCentrallockOff(){
       super();
       payload[0] &= ~(Centrallock);
   }
}

package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDControlCenterList;
public class CMDControlCenterCentrallockOn extends CMDControlCenter{
   public CMDControlCenterCentrallockOn(){
       super();
       payload[0] |= Centrallock;
   }
}

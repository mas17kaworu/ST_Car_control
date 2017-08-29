package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDControlCenterList;
public class CMDControlCenterCentralUnlockOff extends CMDControlCenter{
   public CMDControlCenterCentralUnlockOff(){
       super();
       payload[0] &= ~(CentralUnlock);
   }
}

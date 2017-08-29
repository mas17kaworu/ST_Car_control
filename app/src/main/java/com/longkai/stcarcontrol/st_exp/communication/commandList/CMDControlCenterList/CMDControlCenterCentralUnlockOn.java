package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDControlCenterList;
public class CMDControlCenterCentralUnlockOn extends CMDControlCenter{
   public CMDControlCenterCentralUnlockOn(){
       super();
       payload[0] |= CentralUnlock;
   }
}

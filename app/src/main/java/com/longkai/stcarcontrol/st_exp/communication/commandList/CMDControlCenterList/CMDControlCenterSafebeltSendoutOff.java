package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDControlCenterList;
public class CMDControlCenterSafebeltSendoutOff extends CMDControlCenter{
   public CMDControlCenterSafebeltSendoutOff(){
       super();
       payload[1] &= ~(SafebeltSendout);
   }
}

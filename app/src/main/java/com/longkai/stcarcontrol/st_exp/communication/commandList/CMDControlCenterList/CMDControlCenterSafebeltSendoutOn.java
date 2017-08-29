package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDControlCenterList;
public class CMDControlCenterSafebeltSendoutOn extends CMDControlCenter{
   public CMDControlCenterSafebeltSendoutOn(){
       super();
       payload[1] |= SafebeltSendout;
   }
}

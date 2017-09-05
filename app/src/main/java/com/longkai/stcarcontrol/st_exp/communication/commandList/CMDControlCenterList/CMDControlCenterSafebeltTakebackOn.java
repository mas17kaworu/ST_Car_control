package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDControlCenterList;
public class CMDControlCenterSafebeltTakebackOn extends CMDControlCenter{
   public CMDControlCenterSafebeltTakebackOn(){
       super();
       payload[1] |= SafebeltTakeback;
       refreshDataPayload();
   }
}

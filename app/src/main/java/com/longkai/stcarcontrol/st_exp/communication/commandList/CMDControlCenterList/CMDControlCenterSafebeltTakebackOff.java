package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDControlCenterList;
public class CMDControlCenterSafebeltTakebackOff extends CMDControlCenter{
   public CMDControlCenterSafebeltTakebackOff(){
       super();
       payload[1] &= ~(SafebeltTakeback);
       refreshDataPayload();
   }
}

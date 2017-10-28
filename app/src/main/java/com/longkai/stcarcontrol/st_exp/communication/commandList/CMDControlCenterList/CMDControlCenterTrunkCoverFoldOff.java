package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDControlCenterList;
public class CMDControlCenterTrunkCoverFoldOff extends CMDControlCenter{
   public CMDControlCenterTrunkCoverFoldOff(){
       super();
       payload[1] &= ~(TrunkCoverFold);
       refreshDataPayload();
   }
}

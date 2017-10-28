package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDControlCenterList;
public class CMDControlCenterTrunkCoberUnfoldOff extends CMDControlCenter{
   public CMDControlCenterTrunkCoberUnfoldOff(){
       super();
       payload[1] &= ~(TrunkCoberUnfold);
       refreshDataPayload();
   }
}

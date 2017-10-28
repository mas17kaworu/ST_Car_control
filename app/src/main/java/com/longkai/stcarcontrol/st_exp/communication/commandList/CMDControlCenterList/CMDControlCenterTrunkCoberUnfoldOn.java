package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDControlCenterList;
public class CMDControlCenterTrunkCoberUnfoldOn extends CMDControlCenter{
   public CMDControlCenterTrunkCoberUnfoldOn(){
       super();
       payload[1] |= TrunkCoberUnfold;
       refreshDataPayload();
   }
}

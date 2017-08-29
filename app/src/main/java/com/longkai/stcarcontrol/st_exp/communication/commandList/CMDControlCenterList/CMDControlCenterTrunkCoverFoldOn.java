package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDControlCenterList;
public class CMDControlCenterTrunkCoverFoldOn extends CMDControlCenter{
   public CMDControlCenterTrunkCoverFoldOn(){
       super();
       payload[1] |= TrunkCoverFold;
   }
}

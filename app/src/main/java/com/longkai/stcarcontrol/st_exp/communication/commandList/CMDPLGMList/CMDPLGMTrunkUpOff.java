package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPLGMList;
public class CMDPLGMTrunkUpOff extends CMDPLGM{
   public CMDPLGMTrunkUpOff(){
       super();
       payload[0] &= ~(TrunkUp);
       refreshDataPayload();
   }
}

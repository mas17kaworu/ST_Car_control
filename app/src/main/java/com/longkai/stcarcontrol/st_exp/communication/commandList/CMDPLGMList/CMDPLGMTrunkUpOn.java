package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPLGMList;
public class CMDPLGMTrunkUpOn extends CMDPLGM{
   public CMDPLGMTrunkUpOn(){
       super();
       payload[0] |= TrunkUp;
       refreshDataPayload();
   }
}

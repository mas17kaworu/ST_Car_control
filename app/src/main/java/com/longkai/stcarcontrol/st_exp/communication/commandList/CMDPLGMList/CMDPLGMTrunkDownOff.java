package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPLGMList;
public class CMDPLGMTrunkDownOff extends CMDPLGM{
   public CMDPLGMTrunkDownOff(){
       super();
       payload[0] &= ~(TrunkDown);
       refreshDataPayload();
   }
}

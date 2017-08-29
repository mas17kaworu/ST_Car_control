package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPLGMList;
public class CMDPLGMTrunkDownOn extends CMDPLGM{
   public CMDPLGMTrunkDownOn(){
       super();
       payload[0] |= TrunkDown;
   }
}

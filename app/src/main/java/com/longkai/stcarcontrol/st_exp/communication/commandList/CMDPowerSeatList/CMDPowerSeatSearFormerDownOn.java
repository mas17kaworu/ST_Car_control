package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList;
public class CMDPowerSeatSearFormerDownOn extends CMDPowerSeat{
   public CMDPowerSeatSearFormerDownOn(){
       super();
       payload[1] |= SearFormerDown;
       refreshDataPayload();
   }
}

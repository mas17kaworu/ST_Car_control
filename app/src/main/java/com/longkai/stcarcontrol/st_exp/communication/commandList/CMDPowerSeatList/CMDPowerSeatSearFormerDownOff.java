package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList;
public class CMDPowerSeatSearFormerDownOff extends CMDPowerSeat{
   public CMDPowerSeatSearFormerDownOff(){
       super();
       payload[1] &= ~(SearFormerDown);
       refreshDataPayload();
   }
}

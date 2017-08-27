package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList;
public class CMDPowerSeatSeatFormerUpOff extends CMDPowerSeat{
   public CMDPowerSeatSeatFormerUpOff(){
       super();
       payload[1] &= ~(SeatFormerUp);
   }
}

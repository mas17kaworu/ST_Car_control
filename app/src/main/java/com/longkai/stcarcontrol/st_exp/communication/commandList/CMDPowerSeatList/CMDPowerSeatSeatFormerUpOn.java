package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList;
public class CMDPowerSeatSeatFormerUpOn extends CMDPowerSeat{
   public CMDPowerSeatSeatFormerUpOn(){
       super();
       payload[1] |= SeatFormerUp;
   }
}

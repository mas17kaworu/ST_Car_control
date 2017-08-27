package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList;
public class CMDPowerSeatSeatBackwardOff extends CMDPowerSeat{
   public CMDPowerSeatSeatBackwardOff(){
       super();
       payload[1] &= ~(SeatBackward);
   }
}

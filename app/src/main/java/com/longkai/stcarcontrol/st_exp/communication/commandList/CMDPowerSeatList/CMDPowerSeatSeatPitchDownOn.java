package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList;
public class CMDPowerSeatSeatPitchDownOn extends CMDPowerSeat{
   public CMDPowerSeatSeatPitchDownOn(){
       super();
       payload[1] |= SeatPitchDown;
   }
}

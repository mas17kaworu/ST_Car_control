package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList;
public class CMDPowerSeatSeatPitchUpOn extends CMDPowerSeat{
   public CMDPowerSeatSeatPitchUpOn(){
       super();
       payload[1] |= SeatPitchUp;
   }
}

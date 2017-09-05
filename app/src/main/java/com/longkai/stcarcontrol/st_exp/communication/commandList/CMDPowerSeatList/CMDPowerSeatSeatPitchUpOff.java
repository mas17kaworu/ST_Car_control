package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList;
public class CMDPowerSeatSeatPitchUpOff extends CMDPowerSeat{
   public CMDPowerSeatSeatPitchUpOff(){
       super();
       payload[1] &= ~(SeatPitchUp);
       refreshDataPayload();
   }
}

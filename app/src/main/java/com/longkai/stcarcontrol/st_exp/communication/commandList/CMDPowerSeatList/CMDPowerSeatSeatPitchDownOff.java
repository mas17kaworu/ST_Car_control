package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList;
public class CMDPowerSeatSeatPitchDownOff extends CMDPowerSeat{
   public CMDPowerSeatSeatPitchDownOff(){
       super();
       payload[1] &= ~(SeatPitchDown);
       refreshDataPayload();
   }
}

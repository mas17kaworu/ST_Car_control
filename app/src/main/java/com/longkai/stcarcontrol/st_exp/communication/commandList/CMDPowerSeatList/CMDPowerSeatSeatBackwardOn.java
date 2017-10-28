package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList;
public class CMDPowerSeatSeatBackwardOn extends CMDPowerSeat{
   public CMDPowerSeatSeatBackwardOn(){
       super();
       payload[1] |= SeatBackward;
       refreshDataPayload();
   }
}

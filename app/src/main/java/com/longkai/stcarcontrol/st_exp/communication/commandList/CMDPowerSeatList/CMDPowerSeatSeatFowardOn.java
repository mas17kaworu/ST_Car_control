package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList;
public class CMDPowerSeatSeatFowardOn extends CMDPowerSeat{
   public CMDPowerSeatSeatFowardOn(){
       super();
       payload[1] |= SeatFoward;
       refreshDataPayload();
   }
}

package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList;
public class CMDPowerSeatSeatFowardOff extends CMDPowerSeat{
   public CMDPowerSeatSeatFowardOff(){
       super();
       payload[1] &= ~(SeatFoward);
       refreshDataPayload();
   }
}

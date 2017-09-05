package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList;
public class CMDPowerSeatPositionKey1Off extends CMDPowerSeat{
   public CMDPowerSeatPositionKey1Off(){
       super();
       payload[3] &= ~(PositionKey1);
       refreshDataPayload();
   }
}

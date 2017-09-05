package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList;
public class CMDPowerSeatPositionKey1On extends CMDPowerSeat{
   public CMDPowerSeatPositionKey1On(){
       super();
       payload[3] |= PositionKey1;
       refreshDataPayload();
   }
}

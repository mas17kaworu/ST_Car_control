package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList;
public class CMDPowerSeatPositionKey3Off extends CMDPowerSeat{
   public CMDPowerSeatPositionKey3Off(){
       super();
       payload[3] &= ~(PositionKey3);
   }
}

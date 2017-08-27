package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList;
public class CMDPowerSeatPositionKey2Off extends CMDPowerSeat{
   public CMDPowerSeatPositionKey2Off(){
       super();
       payload[3] &= ~(PositionKey2);
   }
}

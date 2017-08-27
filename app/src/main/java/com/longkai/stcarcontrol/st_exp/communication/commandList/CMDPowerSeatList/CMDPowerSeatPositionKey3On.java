package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList;
public class CMDPowerSeatPositionKey3On extends CMDPowerSeat{
   public CMDPowerSeatPositionKey3On(){
       super();
       payload[3] |= PositionKey3;
   }
}

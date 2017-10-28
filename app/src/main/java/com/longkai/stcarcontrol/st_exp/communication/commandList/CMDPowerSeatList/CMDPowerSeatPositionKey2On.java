package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList;
public class CMDPowerSeatPositionKey2On extends CMDPowerSeat{
   public CMDPowerSeatPositionKey2On(){
       super();
       payload[3] |= PositionKey2;
       refreshDataPayload();
   }
}

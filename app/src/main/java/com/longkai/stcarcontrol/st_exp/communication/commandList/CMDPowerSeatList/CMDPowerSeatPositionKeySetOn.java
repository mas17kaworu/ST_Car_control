package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList;
public class CMDPowerSeatPositionKeySetOn extends CMDPowerSeat{
   public CMDPowerSeatPositionKeySetOn(){
       super();
       payload[3] |= PositionKeySet;
       refreshDataPayload();
   }
}

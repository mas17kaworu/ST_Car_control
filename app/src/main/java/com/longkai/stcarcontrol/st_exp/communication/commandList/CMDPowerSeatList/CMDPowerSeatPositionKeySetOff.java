package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList;
public class CMDPowerSeatPositionKeySetOff extends CMDPowerSeat{
   public CMDPowerSeatPositionKeySetOff(){
       super();
       payload[3] &= ~(PositionKeySet);
       refreshDataPayload();
   }
}

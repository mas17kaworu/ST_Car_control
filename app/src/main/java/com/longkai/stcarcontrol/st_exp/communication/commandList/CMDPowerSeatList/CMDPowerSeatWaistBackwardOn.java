package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList;
public class CMDPowerSeatWaistBackwardOn extends CMDPowerSeat{
   public CMDPowerSeatWaistBackwardOn(){
       super();
       payload[0] |= WaistBackward;
       refreshDataPayload();
   }
}

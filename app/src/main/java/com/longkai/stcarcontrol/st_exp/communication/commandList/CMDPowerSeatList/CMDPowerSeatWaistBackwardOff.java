package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList;
public class CMDPowerSeatWaistBackwardOff extends CMDPowerSeat{
   public CMDPowerSeatWaistBackwardOff(){
       super();
       payload[0] &= ~(WaistBackward);
       refreshDataPayload();
   }
}

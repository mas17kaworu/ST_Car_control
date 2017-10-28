package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList;
public class CMDPowerSeatWaistUpOff extends CMDPowerSeat{
   public CMDPowerSeatWaistUpOff(){
       super();
       payload[0] &= ~(WaistUp);
       refreshDataPayload();
   }
}

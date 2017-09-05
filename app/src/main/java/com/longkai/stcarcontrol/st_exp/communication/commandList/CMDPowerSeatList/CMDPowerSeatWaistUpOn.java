package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList;
public class CMDPowerSeatWaistUpOn extends CMDPowerSeat{
   public CMDPowerSeatWaistUpOn(){
       super();
       payload[0] |= WaistUp;
       refreshDataPayload();
   }
}

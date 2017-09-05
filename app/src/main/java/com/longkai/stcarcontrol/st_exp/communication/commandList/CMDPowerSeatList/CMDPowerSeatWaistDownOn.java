package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList;
public class CMDPowerSeatWaistDownOn extends CMDPowerSeat{
   public CMDPowerSeatWaistDownOn(){
       super();
       payload[0] |= WaistDown;
       refreshDataPayload();
   }
}

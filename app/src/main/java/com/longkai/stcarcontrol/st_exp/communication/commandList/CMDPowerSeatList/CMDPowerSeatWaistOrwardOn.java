package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList;
public class CMDPowerSeatWaistOrwardOn extends CMDPowerSeat{
   public CMDPowerSeatWaistOrwardOn(){
       super();
       payload[0] |= WaistOrward;
   }
}

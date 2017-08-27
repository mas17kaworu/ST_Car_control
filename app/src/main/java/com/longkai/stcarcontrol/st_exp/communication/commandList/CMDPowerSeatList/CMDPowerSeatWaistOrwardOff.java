package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList;
public class CMDPowerSeatWaistOrwardOff extends CMDPowerSeat{
   public CMDPowerSeatWaistOrwardOff(){
       super();
       payload[0] &= ~(WaistOrward);
   }
}

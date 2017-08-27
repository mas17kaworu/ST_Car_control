package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList;
public class CMDPowerSeatWaistDownOff extends CMDPowerSeat{
   public CMDPowerSeatWaistDownOff(){
       super();
       payload[0] &= ~(WaistDown);
   }
}

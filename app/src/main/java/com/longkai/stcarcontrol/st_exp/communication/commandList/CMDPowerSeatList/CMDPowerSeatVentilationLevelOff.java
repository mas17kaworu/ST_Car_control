package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList;
public class CMDPowerSeatVentilationLevelOff extends CMDPowerSeat{
   public CMDPowerSeatVentilationLevelOff(){
       super();
       payload[2] &= ~(VentilationLevel);
   }
}

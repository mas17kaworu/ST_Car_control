package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList;
public class CMDPowerSeatVentilationLevelOn extends CMDPowerSeat{
   public CMDPowerSeatVentilationLevelOn(){
       super();
       payload[2] |= VentilationLevel;
   }
}

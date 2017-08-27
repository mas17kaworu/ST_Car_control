package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList;
public class CMDPowerSeatVentilationOff extends CMDPowerSeat{
   public CMDPowerSeatVentilationOff(){
       super();
       payload[2] &= ~(Ventilation);
   }
}

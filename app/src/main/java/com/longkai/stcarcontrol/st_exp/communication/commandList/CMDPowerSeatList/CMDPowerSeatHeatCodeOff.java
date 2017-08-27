package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList;
public class CMDPowerSeatHeatCodeOff extends CMDPowerSeat{
   public CMDPowerSeatHeatCodeOff(){
       super();
       payload[2] &= ~(HeatCode);
   }
}

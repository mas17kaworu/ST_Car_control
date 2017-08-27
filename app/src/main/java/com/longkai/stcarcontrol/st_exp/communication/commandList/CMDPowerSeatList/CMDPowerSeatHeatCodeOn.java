package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList;
public class CMDPowerSeatHeatCodeOn extends CMDPowerSeat{
   public CMDPowerSeatHeatCodeOn(){
       super();
       payload[2] |= HeatCode;
   }
}

package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList;
public class CMDPowerSeatVentilationOn extends CMDPowerSeat{
   public CMDPowerSeatVentilationOn(){
       super();
       payload[2] |= Ventilation;
       refreshDataPayload();
   }
}

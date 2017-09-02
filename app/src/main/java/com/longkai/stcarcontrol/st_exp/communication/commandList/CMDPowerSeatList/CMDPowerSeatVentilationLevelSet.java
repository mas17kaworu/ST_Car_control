package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList;
public class CMDPowerSeatVentilationLevelSet extends CMDPowerSeat{
   public CMDPowerSeatVentilationLevelSet(int value){
       super();
       payload[2] &= (~VentilationLevel);
       payload[2] |= (value<<2);
   }
}

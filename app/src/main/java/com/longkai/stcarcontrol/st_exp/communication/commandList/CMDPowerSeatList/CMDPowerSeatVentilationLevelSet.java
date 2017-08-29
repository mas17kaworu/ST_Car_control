package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList;
public class CMDPowerSeatVentilationLevelSet extends CMDPowerSeat{
   public CMDPowerSeatVentilationLevelSet(int value){
       super();
       // TODO: 2017/8/29 赋值
       payload[2] |= VentilationLevel;
   }
}

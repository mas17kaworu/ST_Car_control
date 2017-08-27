package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList;
public class CMDPowerSeatBackrestForwardOff extends CMDPowerSeat{
   public CMDPowerSeatBackrestForwardOff(){
       super();
       payload[0] &= ~(BackrestForward);
   }
}

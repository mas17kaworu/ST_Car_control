package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList;
public class CMDPowerSeatBackrestBackwardOff extends CMDPowerSeat{
   public CMDPowerSeatBackrestBackwardOff(){
       super();
       payload[0] &= ~(BackrestBackward);
   }
}

package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList;
public class CMDPowerSeatBackrestBackwardOn extends CMDPowerSeat{
   public CMDPowerSeatBackrestBackwardOn(){
       super();
       payload[0] |= BackrestBackward;
   }
}

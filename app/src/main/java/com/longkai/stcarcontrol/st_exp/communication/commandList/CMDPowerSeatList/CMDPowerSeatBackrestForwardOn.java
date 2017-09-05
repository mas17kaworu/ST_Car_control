package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList;
public class CMDPowerSeatBackrestForwardOn extends CMDPowerSeat{
   public CMDPowerSeatBackrestForwardOn(){
       super();
       payload[0] |= BackrestForward;
       refreshDataPayload();
   }
}

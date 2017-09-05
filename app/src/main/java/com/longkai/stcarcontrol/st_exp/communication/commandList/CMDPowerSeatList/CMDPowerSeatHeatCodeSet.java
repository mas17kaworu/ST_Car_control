package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDPowerSeatList;
public class CMDPowerSeatHeatCodeSet extends CMDPowerSeat{
   public CMDPowerSeatHeatCodeSet(int value){
       super();
       //先清零
       payload[2] &= (~HeatCode);
       payload[2] |= (HeatCode & value);
       refreshDataPayload();
   }
}

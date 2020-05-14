package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUFunList;
public class CMDVCUFunFun_Inter_MCUOff extends CMDVCUFun{
   public CMDVCUFunFun_Inter_MCUOff(){
       super();
       payload[0] &= ~(Fun_Inter_MCU);
       refreshDataPayload();
   }
}

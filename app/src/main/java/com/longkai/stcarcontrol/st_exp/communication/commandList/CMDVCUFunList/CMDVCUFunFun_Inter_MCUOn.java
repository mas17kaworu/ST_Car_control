package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUFunList;
public class CMDVCUFunFun_Inter_MCUOn extends CMDVCUFun{
   public CMDVCUFunFun_Inter_MCUOn(){
       super();
       payload[0] |= Fun_Inter_MCU;
       refreshDataPayload();
   }
}

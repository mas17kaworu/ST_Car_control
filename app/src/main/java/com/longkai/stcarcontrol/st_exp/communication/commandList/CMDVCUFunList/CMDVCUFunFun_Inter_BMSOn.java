package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUFunList;
public class CMDVCUFunFun_Inter_BMSOn extends CMDVCUFun{
   public CMDVCUFunFun_Inter_BMSOn(){
       super();
       payload[0] |= Fun_Inter_BMS;
       refreshDataPayload();
   }
}

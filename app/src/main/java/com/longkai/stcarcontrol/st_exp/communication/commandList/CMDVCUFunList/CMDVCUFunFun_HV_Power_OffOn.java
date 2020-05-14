package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUFunList;
public class CMDVCUFunFun_HV_Power_OffOn extends CMDVCUFun{
   public CMDVCUFunFun_HV_Power_OffOn(){
       super();
       payload[0] |= Fun_HV_Power_Off;
       refreshDataPayload();
   }
}

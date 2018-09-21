package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUFunList;
public class CMDVCUFunFun_HV_Power_OnOn extends CMDVCUFun{
   public CMDVCUFunFun_HV_Power_OnOn(){
       super();
       payload[0] |= Fun_HV_Power_On;
       refreshDataPayload();
   }
}

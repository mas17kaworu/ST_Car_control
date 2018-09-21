package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUFunList;
public class CMDVCUFunFun_HV_Power_OffOff extends CMDVCUFun{
   public CMDVCUFunFun_HV_Power_OffOff(){
       super();
       payload[0] &= ~(Fun_HV_Power_Off);
       refreshDataPayload();
   }
}

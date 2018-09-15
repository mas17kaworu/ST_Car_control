package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUControlList;
public class CMDVCUControlFun_HV_Power_OffOn extends CMDVCUControl{
   public CMDVCUControlFun_HV_Power_OffOn(){
       super();
       payload[1] |= Fun_HV_Power_Off;
       refreshDataPayload();
   }
}

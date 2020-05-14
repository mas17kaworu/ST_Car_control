package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUFunList;
public class CMDVCUFunFun_HV_Iso_DetOn extends CMDVCUFun{
   public CMDVCUFunFun_HV_Iso_DetOn(){
       super();
       payload[0] |= Fun_HV_Iso_Det;
       refreshDataPayload();
   }
}

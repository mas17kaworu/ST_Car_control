package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUControlList;
public class CMDVCUControlFun_HV_Iso_DetOn extends CMDVCUControl{
   public CMDVCUControlFun_HV_Iso_DetOn(){
       super();
       payload[1] |= Fun_HV_Iso_Det;
       refreshDataPayload();
   }
}

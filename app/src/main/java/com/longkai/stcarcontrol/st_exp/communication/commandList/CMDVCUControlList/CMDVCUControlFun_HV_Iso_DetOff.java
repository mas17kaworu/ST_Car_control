package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUControlList;
public class CMDVCUControlFun_HV_Iso_DetOff extends CMDVCUControl{
   public CMDVCUControlFun_HV_Iso_DetOff(){
       super();
       payload[1] &= ~(Fun_HV_Iso_Det);
       refreshDataPayload();
   }
}

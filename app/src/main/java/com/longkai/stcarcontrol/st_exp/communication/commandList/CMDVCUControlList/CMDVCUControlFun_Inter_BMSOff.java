package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUControlList;
public class CMDVCUControlFun_Inter_BMSOff extends CMDVCUControl{
   public CMDVCUControlFun_Inter_BMSOff(){
       super();
       payload[1] &= ~(Fun_Inter_BMS);
       refreshDataPayload();
   }
}

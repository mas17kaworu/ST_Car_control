package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUControlList;
public class CMDVCUControlFun_Inter_BMSOn extends CMDVCUControl{
   public CMDVCUControlFun_Inter_BMSOn(){
       super();
       payload[1] |= Fun_Inter_BMS;
       refreshDataPayload();
   }
}

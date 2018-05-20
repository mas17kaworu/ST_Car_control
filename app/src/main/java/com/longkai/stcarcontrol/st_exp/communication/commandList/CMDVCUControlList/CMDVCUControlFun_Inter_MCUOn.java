package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUControlList;
public class CMDVCUControlFun_Inter_MCUOn extends CMDVCUControl{
   public CMDVCUControlFun_Inter_MCUOn(){
       super();
       payload[1] |= Fun_Inter_MCU;
       refreshDataPayload();
   }
}

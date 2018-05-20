package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUControlList;
public class CMDVCUControlFun_Inter_TboxOff extends CMDVCUControl{
   public CMDVCUControlFun_Inter_TboxOff(){
       super();
       payload[1] &= ~(Fun_Inter_Tbox);
       refreshDataPayload();
   }
}

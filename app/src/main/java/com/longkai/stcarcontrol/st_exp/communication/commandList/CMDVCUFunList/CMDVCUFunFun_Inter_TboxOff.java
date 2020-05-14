package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUFunList;
public class CMDVCUFunFun_Inter_TboxOff extends CMDVCUFun{
   public CMDVCUFunFun_Inter_TboxOff(){
       super();
       payload[0] &= ~(Fun_Inter_Tbox);
       refreshDataPayload();
   }
}

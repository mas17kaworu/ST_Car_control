package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUFunList;
public class CMDVCUFunFun_Charger_HVILOn extends CMDVCUFun{
   public CMDVCUFunFun_Charger_HVILOn(){
       super();
       payload[0] |= Fun_Charger_HVIL;
       refreshDataPayload();
   }
}

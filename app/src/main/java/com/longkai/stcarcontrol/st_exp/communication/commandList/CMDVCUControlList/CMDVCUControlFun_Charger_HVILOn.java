package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUControlList;
public class CMDVCUControlFun_Charger_HVILOn extends CMDVCUControl{
   public CMDVCUControlFun_Charger_HVILOn(){
       super();
       payload[1] |= Fun_Charger_HVIL;
       refreshDataPayload();
   }
}

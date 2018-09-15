package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUControlList;
public class CMDVCUControlFun_Charger_HVILOff extends CMDVCUControl{
   public CMDVCUControlFun_Charger_HVILOff(){
       super();
       payload[1] &= ~(Fun_Charger_HVIL);
       refreshDataPayload();
   }
}

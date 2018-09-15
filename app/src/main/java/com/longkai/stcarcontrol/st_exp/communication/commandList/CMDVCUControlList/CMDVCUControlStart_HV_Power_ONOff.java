package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUControlList;
public class CMDVCUControlStart_HV_Power_ONOff extends CMDVCUControl{
   public CMDVCUControlStart_HV_Power_ONOff(){
       super();
       payload[0] &= ~(Start_HV_Power_ON);
       refreshDataPayload();
   }
}

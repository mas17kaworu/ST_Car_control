package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUControlList;
public class CMDVCUControlStart_HV_Power_ONOn extends CMDVCUControl{
   public CMDVCUControlStart_HV_Power_ONOn(){
       super();
       payload[0] |= Start_HV_Power_ON;
       refreshDataPayload();
   }
}

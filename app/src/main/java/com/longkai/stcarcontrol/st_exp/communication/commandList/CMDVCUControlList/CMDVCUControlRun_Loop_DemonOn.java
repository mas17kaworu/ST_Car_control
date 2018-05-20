package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUControlList;
public class CMDVCUControlRun_Loop_DemonOn extends CMDVCUControl{
   public CMDVCUControlRun_Loop_DemonOn(){
       super();
       payload[2] |= Run_Loop_Demon;
       refreshDataPayload();
   }
}

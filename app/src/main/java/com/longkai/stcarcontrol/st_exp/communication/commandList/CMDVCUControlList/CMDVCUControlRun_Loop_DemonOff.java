package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUControlList;
public class CMDVCUControlRun_Loop_DemonOff extends CMDVCUControl{
   public CMDVCUControlRun_Loop_DemonOff(){
       super();
       payload[2] &= ~(Run_Loop_Demon);
       refreshDataPayload();
   }
}

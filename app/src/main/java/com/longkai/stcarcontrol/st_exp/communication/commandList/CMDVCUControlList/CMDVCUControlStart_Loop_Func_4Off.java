package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUControlList;
public class CMDVCUControlStart_Loop_Func_4Off extends CMDVCUControl{
   public CMDVCUControlStart_Loop_Func_4Off(){
       super();
       payload[2] &= ~(Start_Loop_Func_4);
       refreshDataPayload();
   }
}

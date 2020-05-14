package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCULoopFuncList;
public class CMDVCULoopFuncStart_Loop_Func_6Off extends CMDVCULoopFunc{
   public CMDVCULoopFuncStart_Loop_Func_6Off(){
       super();
       payload[0] &= ~(Start_Loop_Func_6);
       refreshDataPayload();
   }
}

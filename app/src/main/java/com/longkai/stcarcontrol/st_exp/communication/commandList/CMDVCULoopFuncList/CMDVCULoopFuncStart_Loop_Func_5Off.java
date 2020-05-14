package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCULoopFuncList;
public class CMDVCULoopFuncStart_Loop_Func_5Off extends CMDVCULoopFunc{
   public CMDVCULoopFuncStart_Loop_Func_5Off(){
       super();
       payload[0] &= ~(Start_Loop_Func_5);
       refreshDataPayload();
   }
}

package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCULoopFuncList;
public class CMDVCULoopFuncStart_Loop_Func_4On extends CMDVCULoopFunc{
   public CMDVCULoopFuncStart_Loop_Func_4On(){
       super();
       payload[0] |= Start_Loop_Func_4;
       refreshDataPayload();
   }
}

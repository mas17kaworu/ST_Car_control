package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCULoopFuncList;
public class CMDVCULoopFuncStart_Loop_Func_1On extends CMDVCULoopFunc{
   public CMDVCULoopFuncStart_Loop_Func_1On(){
       super();
       payload[0] |= Start_Loop_Func_1;
       refreshDataPayload();
   }
}

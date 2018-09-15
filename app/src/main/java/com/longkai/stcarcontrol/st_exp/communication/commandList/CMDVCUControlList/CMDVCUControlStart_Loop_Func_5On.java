package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUControlList;
public class CMDVCUControlStart_Loop_Func_5On extends CMDVCUControl{
   public CMDVCUControlStart_Loop_Func_5On(){
       super();
       payload[2] |= Start_Loop_Func_5;
       refreshDataPayload();
   }
}

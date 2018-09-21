package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUFunList;
public class CMDVCUFunFun_Loop_DemoOn extends CMDVCUFun{
   public CMDVCUFunFun_Loop_DemoOn(){
       super();
       payload[0] |= Fun_Loop_Demo;
       refreshDataPayload();
   }
}

package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUFunList;
public class CMDVCUFunFun_Loop_DemoOff extends CMDVCUFun{
   public CMDVCUFunFun_Loop_DemoOff(){
       super();
       payload[0] &= ~(Fun_Loop_Demo);
       refreshDataPayload();
   }
}

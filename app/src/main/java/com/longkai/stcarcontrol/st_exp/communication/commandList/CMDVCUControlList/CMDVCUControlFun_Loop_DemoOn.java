package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUControlList;
public class CMDVCUControlFun_Loop_DemoOn extends CMDVCUControl{
   public CMDVCUControlFun_Loop_DemoOn(){
       super();
       payload[1] |= Fun_Loop_Demo;
       refreshDataPayload();
   }
}

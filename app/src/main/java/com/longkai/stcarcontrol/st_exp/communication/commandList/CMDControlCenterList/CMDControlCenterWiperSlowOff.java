package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDControlCenterList;
public class CMDControlCenterWiperSlowOff extends CMDControlCenter{
   public CMDControlCenterWiperSlowOff(){
       super();
       payload[0] &= ~(WiperSlow);
   }
}

package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDControlCenterList;
public class CMDControlCenterWiperSlowOn extends CMDControlCenter{
   public CMDControlCenterWiperSlowOn(){
       super();
       payload[0] |= WiperSlow;
   }
}

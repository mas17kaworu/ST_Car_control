package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDControlCenterList;
public class CMDControlCenterWiperFastOff extends CMDControlCenter{
   public CMDControlCenterWiperFastOff(){
       super();
       payload[0] &= ~(WiperFast);
   }
}

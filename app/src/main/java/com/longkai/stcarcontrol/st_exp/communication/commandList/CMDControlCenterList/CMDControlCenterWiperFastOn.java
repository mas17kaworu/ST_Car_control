package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDControlCenterList;
public class CMDControlCenterWiperFastOn extends CMDControlCenter{
   public CMDControlCenterWiperFastOn(){
       super();
       payload[0] |= WiperFast;
       refreshDataPayload();
   }
}

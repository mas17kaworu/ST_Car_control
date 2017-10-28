package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorOutHS_LOn extends CMDDoor{
   public CMDDoorOutHS_LOn(){
       super();
       payload[5] |= OutHS_L;
       refreshDataPayload();
   }
}

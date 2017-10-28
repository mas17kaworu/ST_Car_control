package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorOutHS_LOff extends CMDDoor{
   public CMDDoorOutHS_LOff(){
       super();
       payload[5] &= ~(OutHS_L);
       refreshDataPayload();
   }
}

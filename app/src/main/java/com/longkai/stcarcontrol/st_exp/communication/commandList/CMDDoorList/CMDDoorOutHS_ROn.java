package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorOutHS_ROn extends CMDDoor{
   public CMDDoorOutHS_ROn(){
       super();
       payload[5] |= OutHS_R;
       refreshDataPayload();
   }
}

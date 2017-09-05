package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorOut15_LOff extends CMDDoor{
   public CMDDoorOut15_LOff(){
       super();
       payload[5] &= ~(Out15_L);
       refreshDataPayload();
   }
}

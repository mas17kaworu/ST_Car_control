package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorOut11LOff extends CMDDoor{
   public CMDDoorOut11LOff(){
       super();
       payload[3] &= ~(Out11L);
       refreshDataPayload();
   }
}

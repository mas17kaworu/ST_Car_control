package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorOut11ROff extends CMDDoor{
   public CMDDoorOut11ROff(){
       super();
       payload[4] &= ~(Out11R);
       refreshDataPayload();
   }
}

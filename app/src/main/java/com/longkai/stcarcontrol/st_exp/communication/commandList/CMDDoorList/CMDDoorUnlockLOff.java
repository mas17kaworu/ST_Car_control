package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorUnlockLOff extends CMDDoor{
   public CMDDoorUnlockLOff(){
       super();
       payload[0] &= ~(UnlockL);
       refreshDataPayload();
   }
}

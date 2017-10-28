package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorUnlockROff extends CMDDoor{
   public CMDDoorUnlockROff(){
       super();
       payload[0] &= ~(UnlockR);
       refreshDataPayload();
   }
}

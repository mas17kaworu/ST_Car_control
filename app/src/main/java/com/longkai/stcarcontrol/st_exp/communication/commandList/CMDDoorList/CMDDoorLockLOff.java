package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorLockLOff extends CMDDoor{
   public CMDDoorLockLOff(){
       super();
       payload[0] &= ~(LockL);
   }
}

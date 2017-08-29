package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorLockROn extends CMDDoor{
   public CMDDoorLockROn(){
       super();
       payload[0] |= LockR;
   }
}

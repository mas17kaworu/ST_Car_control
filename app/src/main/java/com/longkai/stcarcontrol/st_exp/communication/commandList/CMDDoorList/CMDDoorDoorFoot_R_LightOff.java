package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorDoorFoot_R_LightOff extends CMDDoor{
   public CMDDoorDoorFoot_R_LightOff(){
       super();
       payload[4] &= ~(DoorFoot_R_Light);
       refreshDataPayload();
   }
}

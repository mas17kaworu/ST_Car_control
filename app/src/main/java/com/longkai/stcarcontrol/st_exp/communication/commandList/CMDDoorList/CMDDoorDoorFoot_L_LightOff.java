package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorDoorFoot_L_LightOff extends CMDDoor{
   public CMDDoorDoorFoot_L_LightOff(){
       super();
       payload[3] &= ~(DoorFoot_L_Light);
       refreshDataPayload();
   }
}

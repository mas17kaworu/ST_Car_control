package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorDoorFoot_R_LightOn extends CMDDoor{
   public CMDDoorDoorFoot_R_LightOn(){
       super();
       payload[4] |= DoorFoot_R_Light;
       refreshDataPayload();
   }
}

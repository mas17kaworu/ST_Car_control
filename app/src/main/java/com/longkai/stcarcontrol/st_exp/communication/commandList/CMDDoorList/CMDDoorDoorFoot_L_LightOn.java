package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorDoorFoot_L_LightOn extends CMDDoor{
   public CMDDoorDoorFoot_L_LightOn(){
       super();
       payload[3] |= DoorFoot_L_Light;
   }
}

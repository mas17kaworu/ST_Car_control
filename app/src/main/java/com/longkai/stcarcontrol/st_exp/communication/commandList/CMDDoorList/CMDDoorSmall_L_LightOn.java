package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorSmall_L_LightOn extends CMDDoor{
   public CMDDoorSmall_L_LightOn(){
       super();
       payload[3] |= Small_L_Light;
       refreshDataPayload();
   }
}

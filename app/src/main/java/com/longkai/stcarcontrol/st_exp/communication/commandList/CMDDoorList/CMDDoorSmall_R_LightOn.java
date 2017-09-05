package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorSmall_R_LightOn extends CMDDoor{
   public CMDDoorSmall_R_LightOn(){
       super();
       payload[4] |= Small_R_Light;
       refreshDataPayload();
   }
}

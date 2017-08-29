package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorWindowLDownOn extends CMDDoor{
   public CMDDoorWindowLDownOn(){
       super();
       payload[0] |= WindowLDown;
   }
}

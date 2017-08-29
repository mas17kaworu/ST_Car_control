package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorWindowLDownOff extends CMDDoor{
   public CMDDoorWindowLDownOff(){
       super();
       payload[0] &= ~(WindowLDown);
   }
}

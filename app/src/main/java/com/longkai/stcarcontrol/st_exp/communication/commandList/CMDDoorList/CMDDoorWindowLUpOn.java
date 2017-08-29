package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorWindowLUpOn extends CMDDoor{
   public CMDDoorWindowLUpOn(){
       super();
       payload[0] |= WindowLUp;
   }
}

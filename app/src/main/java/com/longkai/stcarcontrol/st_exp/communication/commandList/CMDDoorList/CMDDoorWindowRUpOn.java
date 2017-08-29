package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorWindowRUpOn extends CMDDoor{
   public CMDDoorWindowRUpOn(){
       super();
       payload[0] |= WindowRUp;
   }
}

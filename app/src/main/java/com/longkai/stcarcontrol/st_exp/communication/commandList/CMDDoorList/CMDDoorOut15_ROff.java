package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorOut15_ROff extends CMDDoor{
   public CMDDoorOut15_ROff(){
       super();
       payload[5] &= ~(Out15_R);
   }
}

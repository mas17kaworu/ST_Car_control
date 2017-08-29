package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorOut12LOff extends CMDDoor{
   public CMDDoorOut12LOff(){
       super();
       payload[3] &= ~(Out12L);
   }
}

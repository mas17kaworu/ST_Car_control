package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorOut10LOff extends CMDDoor{
   public CMDDoorOut10LOff(){
       super();
       payload[3] &= ~(Out10L);
   }
}

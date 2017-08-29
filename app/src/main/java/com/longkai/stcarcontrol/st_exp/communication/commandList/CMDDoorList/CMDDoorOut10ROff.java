package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorOut10ROff extends CMDDoor{
   public CMDDoorOut10ROff(){
       super();
       payload[4] &= ~(Out10R);
   }
}

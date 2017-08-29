package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorMirrorYupOff extends CMDDoor{
   public CMDDoorMirrorYupOff(){
       super();
       payload[1] &= ~(MirrorYup);
   }
}

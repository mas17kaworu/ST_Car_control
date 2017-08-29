package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDDoorList;
public class CMDDoorMirrorFoldOn extends CMDDoor{
   public CMDDoorMirrorFoldOn(){
       super();
       payload[1] |= MirrorFold;
   }
}

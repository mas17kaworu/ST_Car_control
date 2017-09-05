package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDControlCenterList;
public class CMDControlCenterFuelTankUnlockOn extends CMDControlCenter{
   public CMDControlCenterFuelTankUnlockOn(){
       super();
       payload[0] |= FuelTankUnlock;
       refreshDataPayload();
   }
}

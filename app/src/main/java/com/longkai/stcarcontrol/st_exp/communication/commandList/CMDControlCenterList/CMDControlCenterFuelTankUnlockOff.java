package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDControlCenterList;
public class CMDControlCenterFuelTankUnlockOff extends CMDControlCenter{
   public CMDControlCenterFuelTankUnlockOff(){
       super();
       payload[0] &= ~(FuelTankUnlock);
       refreshDataPayload();
   }
}

package com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCUCarModeList;

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand;
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;

/**
 * Created by Administrator on 2018/5/18.
 */

public class CMDVCUCarMode extends BaseCommand {
    @Override
    public BaseResponse toResponse(byte[] data) throws Exception {
        return null;
    }

    @Override
    public byte getCommandId() {
        return 0;
    }
}

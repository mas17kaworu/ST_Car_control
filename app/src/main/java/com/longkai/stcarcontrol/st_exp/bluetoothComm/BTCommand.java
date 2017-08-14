package com.longkai.stcarcontrol.st_exp.bluetoothComm;

import com.longkai.stcarcontrol.st_exp.bluetoothComm.commandList.BaseBTResponse;

/**
 * Created by Administrator on 2017/8/14.
 */

public interface BTCommand {
    /**
     * Parse raw data to 'T' Structure.
     *
     * @param data   The data buffer of needed parse message.
     * @return Parsed message.
     */
    BaseBTResponse toResponse(byte[]data) throws Exception;

    /**
     * Convert to raw data.
     *
     * @return converted raw data
     */
    byte[] toRawData();

    byte getCommandId();
}

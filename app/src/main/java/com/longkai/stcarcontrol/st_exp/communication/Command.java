package com.longkai.stcarcontrol.st_exp.communication;

import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse;

/**
 * Created by Administrator on 2017/8/14.
 */

public interface Command {
    /**
     * Parse raw data to 'T' Structure.
     *
     * @param data   The data buffer of needed parse message.
     * @return Parsed message.
     */
    BaseResponse toResponse(byte[]data) throws Exception;

    /**
     * Convert to raw data.
     *
     * @return converted raw data
     */
    byte[] toRawData();

    byte getCommandId();
}

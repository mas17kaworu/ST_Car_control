package com.longkai.stcarcontrol.st_exp.Interface;

import com.longkai.stcarcontrol.st_exp.Enum.TboxStateEnum;

/**
 * Created by Administrator on 2018/7/19.
 */

public interface TboxStateChange {
    void changeTo(TboxStateEnum state);
}

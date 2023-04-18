package com.longkai.stcarcontrol.st_exp.mockMessage;

import android.content.Context;

/**
 * Created by Administrator on 2018/12/15.
 */

public interface MockMessageService {
    void StartService(String fragmentClass);

    void StartService(String fragmentClass, Context context);

    void StopService(String fragmentClass);
}

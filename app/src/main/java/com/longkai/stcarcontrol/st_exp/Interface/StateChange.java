package com.longkai.stcarcontrol.st_exp.Interface;

/**
 * Created by Administrator on 2018/7/19.
 */

public interface StateChange<T>{
    void changeTo(T state);
}

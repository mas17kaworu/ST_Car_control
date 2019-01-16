package com.longkai.stcarcontrol.st_exp.Utils;

/**
 * Created by Administrator on 2018/9/26.
 */

public class LoggerTemp {
    private static LoggerTemp instance;
    private Object lock1 = new Object();
    private Object lock2 = new Object();
    private Object lock3 = new Object();
    private String log1 = "failed",log2= "failed",log3= "failed";

    public static LoggerTemp getLogger(){
        if (instance == null){
            instance = new LoggerTemp();
        }
        return instance;
    }

    public void writeToLogger1(String info){
        synchronized (lock1) {
            log1 = info;
        }
    }

    public String Logger1ToString(){
        synchronized (lock1){
            return "step 1 " + log1;
        }
    }

    public void writeToLogger2(String info){
        synchronized (lock2) {
            log2 = info;
        }
    }

    public String Logger2ToString(){
        synchronized (lock2){
            return " step 2 " + log2;
        }
    }

    public void writeToLogger3(String info){
        synchronized (lock3) {
            log3 = info;
        }
    }

    public String Logger3ToString(){
        synchronized (lock3){
            return " step 3 " + log3;
        }
    }

}

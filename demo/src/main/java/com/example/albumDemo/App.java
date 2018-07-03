package com.example.albumDemo;

import android.app.Application;

import com.github.anrwatchdog.ANRWatchDog;

/**
 * Created by liyu on 2017/11/14.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                e.printStackTrace();
            }
        });

        new ANRWatchDog().start();
    }
}

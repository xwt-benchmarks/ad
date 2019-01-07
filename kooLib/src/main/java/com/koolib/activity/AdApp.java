package com.koolib.activity;

import android.app.Application;
import android.support.multidex.MultiDex;

public class AdApp extends Application
{
    public void onCreate()
    {
        super.onCreate();
        MultiDex.install(this);
    }
}
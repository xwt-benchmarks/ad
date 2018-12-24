package com.kuyikeji.filemanager.utils.application;

import android.support.multidex.MultiDexApplication;

import com.koolib.activity.AdApp;

/**
 * @author Emmanuel
 *         on 28/8/2017, at 18:12.
 */

public class LeakCanaryApplication extends AdApp {

    @Override
    public void onCreate() {
        super.onCreate();
        /*
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);*/
    }

}
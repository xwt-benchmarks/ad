package com.koolib.activity;

import java.util.List;
import android.util.Log;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.app.Application;
import io.reactivex.Observable;
import android.os.PowerManager;
import android.app.ActivityManager;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import android.support.multidex.MultiDex;

import com.koolib.adconfigaction.ProtectOutAdOfService;

import io.reactivex.schedulers.Schedulers;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class AdApp extends Application
{
    public static final int AppIsForeground = 0x0001;//因软件正常运行而处于前台
    public static final int AppIsBackground = 0x0002;//因服务默默运行而处于后台

    public void onCreate()
    {
        super.onCreate();
        MultiDex.install(this);
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks()
        {
            public void onActivityCreated(final Activity activity, Bundle savedInstanceState)
            {

            }

            public void onActivitySaveInstanceState(final Activity activity, Bundle outState)
            {

            }

            public void onActivityStarted(final Activity activity)
            {

            }

            public void onActivityResumed(final Activity activity)
            {

            }

            public void onActivityStopped(final Activity activity)
            {

            }

            public void onActivityDestroyed(final Activity activity)
            {

            }

            public void onActivityPaused(final Activity activity)
            {
                Observable.just("wait...").map(new Function<String,String>()
                {
                    public String apply(String noteStr) throws Exception
                    {
                        Thread.sleep(300);
                        return "begin";
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>()
                {
                    public void accept(String s) throws Exception
                    {
                        int statusCode = appIsForeground(activity);
                        if(statusCode == AppIsBackground && isScreenOn(activity))
                        {
                            Intent mIntent = new Intent();
                            mIntent.setAction(Intent.ACTION_MAIN);
                            mIntent.addCategory(Intent.CATEGORY_HOME);
                            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            /**********/startActivity(mIntent);/***********/
                            PackageUsageStatsPermissionActivity.isShowPackageUsageStatsPermissionActivity = false;
                            ProtectOutAdOfService.LastStartProcessServiceTime = System.currentTimeMillis() + ProtectOutAdOfService.StartProcessServiceIntervalTime;
                        }/***********************end***********************/
                    }
                });
            }
        });
    }

    private static boolean isScreenOn(final Activity activity)
    {
        PowerManager powerManager = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
        return powerManager.isScreenOn();
    }

    public static int appIsForeground(final Activity activity)
    {
        ActivityManager activityManager = (ActivityManager)activity.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses)
        {
            if (appProcess.processName.equals(activity.getPackageName()))
            {
                Log.i("ActivityLife",appProcess.importance + "");
                if(appProcess.importance >= ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND)
                    return AppIsBackground;
                else if(appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_SERVICE)
                    return AppIsBackground;
                else if(appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_PERCEPTIBLE)
                    return AppIsBackground;
                else if(appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_PERCEPTIBLE_PRE_26)
                    return AppIsBackground;
                else
                    return AppIsForeground;
            }
        }
        return AppIsForeground;
    }
}
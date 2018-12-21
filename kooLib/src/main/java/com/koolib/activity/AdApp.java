package com.koolib.activity;

import java.util.List;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.app.Application;
import io.reactivex.Observable;
import android.os.PowerManager;
import java.lang.reflect.Method;
import android.os.RemoteException;
import android.app.ActivityManager;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import android.support.multidex.MultiDex;
import io.reactivex.schedulers.Schedulers;
import java.lang.reflect.InvocationTargetException;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class AdApp extends Application
{
    public static final int AppIsKilled = 0x0000;//因完全退出应用而处于后台
    public static final int AppIsBackground = 0x0001;//因服务默默运行而处于后台
    public static final int AppIsForeground = 0x0002;//因软件正常运行而处于前台

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
                        //if(!activity.getClass().getName().contains(getPackageName()))activity.finish();
                        Thread.sleep(200);
                        return "begin";
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>()
                {
                    public void accept(String s) throws Exception
                    {
                        int statusCode = appIsForeground(activity);
                        if(statusCode != AppIsForeground && isScreenOn(activity))
                        {
                            Intent mIntent = new Intent();
                            mIntent.setAction(Intent.ACTION_MAIN);
                            mIntent.addCategory(Intent.CATEGORY_HOME);
                            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            /**********/startActivity(mIntent);/***********/
                            //if(statusCode == AppIsBackground && Build.VERSION.SDK_INT < Build.VERSION_CODES.N)showRecentApps();
                        } /***********************************************end**************************************************/
                    }
                });
            }
        });
    }

    private static void showRecentApps()
    {
        Observable.just("showRecentApps").map(new Function<String, String>()
        {
            public String apply(String noteStr) throws Exception
            {
                Thread.sleep(555);
                return  noteStr;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>()
        {
            public void accept(String noteStr) throws Exception
            {
                Class serviceManagerClass = null;
                try
                {
                    serviceManagerClass = Class.forName("android.os.ServiceManager");
                    Method getService = serviceManagerClass.getMethod("getService",String.class);
                    IBinder iBinder = (IBinder)getService.invoke(serviceManagerClass,"statusbar");
                    Class statusBarClass  =  Class.forName(iBinder.getInterfaceDescriptor());
                    Object statusBarObject = statusBarClass.getClasses()[0].getMethod(
                    "asInterface",IBinder.class).invoke(null, new Object[]{ iBinder });
                    Method toggleRecentApps=statusBarClass.getMethod("toggleRecentApps");
                    toggleRecentApps.setAccessible(true);
                    toggleRecentApps.invoke(statusBarObject);
                }
                catch (RemoteException e)
                {
                    e.printStackTrace();
                }
                catch (NoSuchMethodException e)
                {
                    e.printStackTrace();
                }
                catch (ClassNotFoundException e)
                {
                    e.printStackTrace();
                }
                catch (IllegalAccessException e)
                {
                    e.printStackTrace();
                }
                catch (IllegalArgumentException e)
                {
                    e.printStackTrace();
                }
                catch (InvocationTargetException e)
                {
                    e.printStackTrace();
                }
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
                if(appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
                    return AppIsForeground;
                else if(appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE)
                    return AppIsForeground;
                else if(appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_SERVICE)
                    return AppIsBackground;
                else if(appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND_SERVICE)
                    return AppIsBackground;
                else
                    return AppIsKilled;
            }
        }
        return AppIsForeground;
    }
}
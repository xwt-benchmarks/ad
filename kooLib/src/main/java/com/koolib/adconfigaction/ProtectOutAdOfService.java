package com.koolib.adconfigaction;

import java.util.List;
import android.util.Log;
import android.os.IBinder;
import android.os.Process;
import android.content.Intent;
import android.content.Context;
import io.reactivex.Observable;
import android.app.ActivityManager;
import android.content.IntentFilter;
import java.util.concurrent.TimeUnit;
import io.reactivex.functions.Action;
import android.content.ComponentName;
import android.content.pm.ResolveInfo;
import io.reactivex.functions.Consumer;
import com.koolib.activity.ALiveManager;
import com.koolib.activity.AliveActivity;
import android.content.pm.PackageManager;
import io.reactivex.disposables.Disposable;
import com.duapps.ad.InterstitialAdActivity;
import com.google.android.gms.ads.AdActivity;
import com.facebook.ads.AudienceNetworkActivity;
import com.xdandroid.hellodaemon.AbsWorkService;
import io.reactivex.android.schedulers.AndroidSchedulers;
import com.koolib.activity.PackageUsageStatsPermissionActivity;

public class ProtectOutAdOfService extends AbsWorkService
{
    public static Disposable sDisposable;
    public int outAdBroadcastProcessPid = 0;
    public static boolean sShouldStopService;
    public int homeKeyEventBroadcastProcessPid = 0;
    public int switchApplicationBroadcastProcessPid = 0;
    private final String TAG = ProtectOutAdOfService.class.getSimpleName();
    public static final long StartProcessServiceIntervalTime = 1000 * 60 * 2;
    public static long LastStartProcessServiceTime = System.currentTimeMillis();

    public static void stopService()
    {
        sShouldStopService = true;
        if (sDisposable != null)
            sDisposable.dispose();
        cancelJobAlarmSub();
    }

    private boolean appIsForeground()
    {
        ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses)
        {
            if(appProcess.processName.equals(getPackageName()))
            {
                if(appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
                    return true;
            }
        }
        return false;
    }

    private boolean isItJustAppItself()
    {
        ActivityManager activityManager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        ComponentName componentName = activityManager.getRunningTasks(1).get(0).topActivity;
        if(componentName.getPackageName().toLowerCase().trim().contains(getPackageName().toLowerCase().trim())&&
        !componentName.getClassName().trim().contains(AudienceNetworkActivity.class.getSimpleName().trim())&&
        !componentName.getClassName().trim().contains(InterstitialAdActivity.class.getSimpleName().trim())&&
        !componentName.getClassName().trim().contains(AliveActivity.class.getSimpleName().trim())&&
        !componentName.getClassName().trim().contains(AdActivity.class.getSimpleName().trim()))
            return true;
        else
            return false;
    }

    public IBinder onBind(Intent intent, Void v)
    {
        return null;

    }

    public void onServiceKilled(Intent rootIntent)
    {

    }

    /**********************************************************************************************/
    /**********************************************************************************************/

    private boolean isOutAdBroadcastRunning()
    {
        Intent intent = new Intent();
        intent.setAction(OutAdBroadcast.OPERATEOTHERAPP);
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> resolveInfos = packageManager.queryBroadcastReceivers(intent,0);
        if(resolveInfos != null)
        {
            for(int index = 0;index < resolveInfos.size();index++)
            {
                if(resolveInfos.get(index).activityInfo.packageName.trim().equals(getPackageName())&& resolveInfos.get(index).
                activityInfo.name.contains(OutAdBroadcast.class.getSimpleName()) && outAdBroadcastProcessPid == Process.myPid())
                    return true;
                if(index == resolveInfos.size() - 1)
                {
                    Log.i(TAG, "OutAdBroadcastProcessPid : " + outAdBroadcastProcessPid);
                    Log.i(TAG, "OutAdBroadcastProcessPid : " + Process.myPid());
                    outAdBroadcastProcessPid = Process.myPid();
                }
            }
        }
        return false;
    }

    private boolean isHomeKeyEventBroadcastRunning()
    {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        PackageManager packageManager  = getPackageManager();
        List<ResolveInfo> resolveInfos = packageManager.queryBroadcastReceivers(intent,0);
        if(resolveInfos != null)
        {
            for(int index = 0;index < resolveInfos.size();index++)
            {
                if(resolveInfos.get(index).activityInfo.packageName.trim().equals(getPackageName()) && resolveInfos.get(index).
                activityInfo.name.contains(HomeKeyEventBroadcast.class.getSimpleName()) && homeKeyEventBroadcastProcessPid == Process.myPid())
                    return true;
                if(index == resolveInfos.size() - 1)
                {
                    Log.i(TAG, "HomeKeyEventBroadcastProcessPid : " + homeKeyEventBroadcastProcessPid);
                    Log.i(TAG, "HomeKeyEventBroadcastProcessPid : " + Process.myPid());
                    homeKeyEventBroadcastProcessPid = Process.myPid();
                }
            }
        }
        return false;
    }

    private boolean isSwitchApplicationBroadcastRunning()
    {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        PackageManager packageManager  = getPackageManager();
        List<ResolveInfo> resolveInfos = packageManager.queryBroadcastReceivers(intent,0);
        if(resolveInfos != null)
        {
            for(int index = 0;index < resolveInfos.size();index++)
            {
                if(resolveInfos.get(index).activityInfo.packageName.trim().equals(getPackageName()) && resolveInfos.get(index).
                activityInfo.name.contains(SwitchApplicationBroadcast.class.getSimpleName()) && switchApplicationBroadcastProcessPid == Process.myPid())
                    return true;
                if(index == resolveInfos.size() - 1)
                {
                    Log.i(TAG, "SwitchApplicationBroadcastProcessPid : " + switchApplicationBroadcastProcessPid);
                    Log.i(TAG, "SwitchApplicationBroadcastProcessPid : " + Process.myPid());
                    switchApplicationBroadcastProcessPid = Process.myPid();
                }
            }
        }
        return false;
    }

    private boolean isServiceRunning(String  serviceName)
    {
        ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);
        for(int index = 0;index <serviceList.size();index++)
        {
            ActivityManager.RunningServiceInfo serviceInfo = serviceList.get(index);
            if(serviceInfo.service.getClassName().toLowerCase().trim().contains(serviceName.toLowerCase().trim()))
                return true;
        }
        return false;
    }

    /**********************************************************************************************/
    /**********************************************************************************************/

    private synchronized void executeService()
    {
        if(appIsForeground() && isItJustAppItself() && System.currentTimeMillis() >= LastStartProcessServiceTime &&
        !PackageUsageStatsPermissionActivity.isShowPackageUsageStatsPermissionActivity && !isServiceRunning(ProcessService.class.getName()))
        {
            PackageUsageStatsPermissionActivity.isShowPackageUsageStatsPermissionActivity = true;
            ALiveManager.getInstance().startProcessService(ProtectOutAdOfService.this);
        }
    }

    private synchronized void executeOutAdBroadcast()
    {
        if(!isOutAdBroadcastRunning())
        {
            OutAdBroadcast outAdBroadcast = new OutAdBroadcast();
            IntentFilter powerAndScreenIntentFilter = new IntentFilter();
            powerAndScreenIntentFilter.addAction(Intent.ACTION_SCREEN_OFF);
            powerAndScreenIntentFilter.addAction(Intent.ACTION_USER_PRESENT);
            powerAndScreenIntentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
            powerAndScreenIntentFilter.addAction(OutAdBroadcast.OPERATEOTHERAPP);
            powerAndScreenIntentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
            getApplicationContext().registerReceiver(outAdBroadcast,powerAndScreenIntentFilter);
        }
    }

    private synchronized void executeHomeKeyEventBroadcast()
    {
        if(!isHomeKeyEventBroadcastRunning())
        {
            HomeKeyEventBroadcast homeKeyEventBroadcast = new HomeKeyEventBroadcast();
            /******/IntentFilter homeKeyEventIntentFilter = new IntentFilter();/*******/
            homeKeyEventIntentFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);/***/
            getApplicationContext().registerReceiver(homeKeyEventBroadcast,homeKeyEventIntentFilter);
        }
    }

    private synchronized void executeSwitchApplicationBroadcast()
    {
        if(!isSwitchApplicationBroadcastRunning())
        {
            SwitchApplicationBroadcast switchApplicationBroadcast= new SwitchApplicationBroadcast();
            /**********/IntentFilter switchApplicationIntentFilter = new IntentFilter();/**********/
            /****/switchApplicationIntentFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);/****/
            getApplicationContext().registerReceiver(switchApplicationBroadcast,switchApplicationIntentFilter);
        }
    }

    /**********************************************************************************************/
    /**********************************************************************************************/

    public void stopWork(Intent intent, int flags, int startId)
    {
        stopService();

    }

    public void startWork(Intent intent, int flags, int startId)
    {
        if(!isWorkRunning(intent,flags,startId))
        {
            sDisposable = Observable.interval(0,2,TimeUnit.SECONDS).doOnDispose(new Action()
            {
                public void run() throws Exception
                {
                    cancelJobAlarmSub();
                }
            }).subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>()
            {
                public void accept(Long aLong) throws Exception
                {
                    executeService();
                    executeOutAdBroadcast();
                    executeHomeKeyEventBroadcast();
                    executeSwitchApplicationBroadcast();
                }
            });
        }
    }

    public Boolean isWorkRunning(Intent intent, int flags, int startId)
    {
        return sDisposable != null && !sDisposable.isDisposed();
    }

    public Boolean shouldStopService(Intent intent, int flags, int startId)
    {
        return sShouldStopService;
    }
}
package com.kuyikeji.filemanager.advertisement;

import java.util.List;
import android.os.Build;
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
import android.content.pm.ResolveInfo;
import io.reactivex.functions.Consumer;
import android.content.pm.PackageManager;
import io.reactivex.disposables.Disposable;
import com.xdandroid.hellodaemon.AbsWorkService;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class ProtectAdvertisementService extends AbsWorkService
{
    public int ProcessUid = 0;
    public static Disposable sDisposable;
    public static boolean sShouldStopService;

    private void executeService()
    {
        boolean isRunningForGetProcesses = AppAdProcessesService.isStartUp || isServiceRunning("com.kuyikeji.filemanager.advertisement.AppAdProcessesService");
        if(!isRunningForGetProcesses)
        {
            /****************************此次发版如果还有问题那就是这里启动服务出错**********************************/
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                startForegroundService(new Intent(ProtectAdvertisementService.this, AppAdProcessesService.class));
            }
            else
            {
                startService(new Intent(ProtectAdvertisementService.this, AppAdProcessesService.class));
            }
        }
    }

    private void executeReceiver()
    {
        if(!isReceiverRunning())
        {
            AppAdBroadcast appAdBroadcast = new AppAdBroadcast();
            IntentFilter powerAndScreenIntentFilter = new IntentFilter();
            powerAndScreenIntentFilter.addAction(Intent.ACTION_SCREEN_OFF);
            powerAndScreenIntentFilter.addAction(Intent.ACTION_USER_PRESENT);
            powerAndScreenIntentFilter.addAction(Intent.ACTION_BATTERY_OKAY);
            powerAndScreenIntentFilter.addAction(AppAdBroadcast.END_ACTIVITY);
            powerAndScreenIntentFilter.addAction(AppAdBroadcast.START_ACTIVITY);
            powerAndScreenIntentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
            powerAndScreenIntentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
            registerReceiver(appAdBroadcast,powerAndScreenIntentFilter);
        }
    }

    public static void stopService()
    {
        sShouldStopService = true;
        if (sDisposable != null)
            sDisposable.dispose();
        cancelJobAlarmSub();
    }

    private boolean isReceiverRunning()
    {
        Intent intent = new Intent();
        intent.setAction("start_activity");
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> resolveInfos = packageManager.queryBroadcastReceivers(intent, 0);
        if(resolveInfos != null)
        {
            for(int index = 0;index < resolveInfos.size();index++)
            {
               if(resolveInfos.get(index).activityInfo.packageName.trim().equals(getPackageName()) && resolveInfos.get(index).activityInfo.name.contains("AppAdBroadcast") && ProcessUid == Process.myUid())
                   return true;
               if(index == resolveInfos.size() - 1)
               {
                   Log.i("wocao", "ProcessUid : " + ProcessUid);
                   Log.i("wocao", "ProcessUid : " + Process.myUid());
                   ProcessUid = Process.myUid();
               }
            }
        }
        return false;
    }

    public IBinder onBind(Intent intent, Void v)
    {
        return null;

    }

    public void onServiceKilled(Intent rootIntent)
    {

    }

    private boolean isServiceRunning(String serviceName)
    {
        ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);
        for(int index = 0;index <serviceList.size();index++)
        {
            ActivityManager.RunningServiceInfo serviceInfo = serviceList.get(index);
            if(serviceInfo.service.getClassName().toString().contains(serviceName))
                return true;
        }
        return false;
    }

    public void startWork(Intent intent, int flags, int startId)
    {
        sDisposable = Observable.interval(3,TimeUnit.SECONDS).doOnDispose(new Action()
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
                executeReceiver();
            }
        });
       /* sDisposable = Observable.interval(3, TimeUnit.SECONDS)
                    .doOnDispose(() ->*//****取消任务时取消定时唤醒****//*
                    {
                        cancelJobAlarmSub();
                    })
                    .subscribe(count ->
                    {
                        executeService();
                        executeReceiver();
                    });*/
    }

    public void stopWork(Intent intent, int flags, int startId)
    {
        stopService();

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
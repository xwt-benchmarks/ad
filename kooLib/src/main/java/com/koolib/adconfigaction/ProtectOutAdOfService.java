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
import android.content.pm.ResolveInfo;
import io.reactivex.functions.Consumer;
import com.koolib.activity.ALiveManager;
import android.content.pm.PackageManager;
import io.reactivex.disposables.Disposable;
import com.xdandroid.hellodaemon.AbsWorkService;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class ProtectOutAdOfService extends AbsWorkService
{
    public int ProcessUid = 0;
    public static Disposable sDisposable;
    public static boolean sShouldStopService;
    private final String TAG = ProtectOutAdOfService.class.getSimpleName();

    private void executeService()
    {
        if(!isServiceRunning(ProcessService.class.getName()))
        {
            ALiveManager.getInstance().finishAlive();
            ALiveManager.getInstance().startAliveWithStartProcessService(this);
        }
    }

    private void executeReceiver()
    {
        if(!isReceiverRunning())
        {
            OutAdBroadcast outAdBroadcast = new OutAdBroadcast();
            IntentFilter powerAndScreenIntentFilter = new IntentFilter();
            powerAndScreenIntentFilter.addAction(Intent.ACTION_SCREEN_OFF);
            powerAndScreenIntentFilter.addAction(Intent.ACTION_USER_PRESENT);
            powerAndScreenIntentFilter.addAction(OutAdBroadcast.OPENOTHERAPP);
            powerAndScreenIntentFilter.addAction(OutAdBroadcast.CLOSEOTHERAPP);
            powerAndScreenIntentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
            powerAndScreenIntentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
            getApplicationContext().registerReceiver(outAdBroadcast,powerAndScreenIntentFilter);
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
        intent.setAction(OutAdBroadcast.OPENOTHERAPP);
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> resolveInfos = packageManager.queryBroadcastReceivers(intent, 0);
        if(resolveInfos != null)
        {
            for(int index = 0;index < resolveInfos.size();index++)
            {
               if(resolveInfos.get(index).activityInfo.packageName.trim().equals(getPackageName()) && resolveInfos.get(index).activityInfo.name.contains(OutAdBroadcast.class.getSimpleName()) && ProcessUid == Process.myUid())
                   return true;
               if(index == resolveInfos.size() - 1)
               {
                   Log.i(TAG, "ProcessUid : " + ProcessUid);
                   Log.i(TAG, "ProcessUid : " + Process.myUid());
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
            if(serviceInfo.service.getClassName().toLowerCase().trim().contains(serviceName.toLowerCase().trim()))
                return true;
        }
        return false;
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
                    executeReceiver();
                }
            });
        }
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
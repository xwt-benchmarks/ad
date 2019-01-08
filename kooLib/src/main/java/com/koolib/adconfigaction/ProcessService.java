package com.koolib.adconfigaction;

import java.util.List;
import android.util.Log;
import android.os.Build;
import android.os.IBinder;
import android.app.Service;
import java.util.ArrayList;
import io.reactivex.Observer;
import android.content.Intent;
import android.content.Context;
import io.reactivex.Observable;
import com.koolib.util.StringUtils;
import com.koolib.util.AppInfoUtils;
import java.util.concurrent.TimeUnit;
import android.app.usage.UsageEvents;
import com.koolib.datamodel.AdConfigBean;
import io.reactivex.schedulers.Schedulers;
import android.app.usage.UsageStatsManager;
import io.reactivex.disposables.Disposable;
import com.koolib.util.SharepreferenceUtils;
import com.jaredrummler.android.processes.AndroidProcesses;
import com.jaredrummler.android.processes.models.AndroidAppProcess;

public class ProcessService extends Service
{
    private Disposable mDisposable;
    private String mLateTopAppPackageName;
    private List<String> mNotNeedAdForApps;
    private UsageStatsManager mUsageStatsManager;
    private static final String TAG = "ProcessService";

    public void onCreate()
    {
        super.onCreate();
        mLateTopAppPackageName = "";
        mNotNeedAdForApps = new ArrayList<>();
        mNotNeedAdForApps.add("com.antiy.avl");
        mNotNeedAdForApps.add("com.qihoo.security");
        mNotNeedAdForApps.add("com.cmsecurity.lite");
        mNotNeedAdForApps.add("com.iobit.mobilecare");
        mNotNeedAdForApps.add("com.cleanmaster.mguard");
        mNotNeedAdForApps.add("com.qihoo.security.lite");
        mNotNeedAdForApps.add("com.cleanmaster.security");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            mUsageStatsManager = (UsageStatsManager)getSystemService(Context.USAGE_STATS_SERVICE);
    }

    public void onDestroy()
    {
        super.onDestroy();
        mUsageStatsManager = null;
        mLateTopAppPackageName = null;
        if(null != mNotNeedAdForApps)
        {
            mNotNeedAdForApps.clear();
            mNotNeedAdForApps = null;
        }
    }

    public IBinder onBind(Intent intent)
    {
        return null;

    }

    /*********获取顶层应用的包名********/
    public String getTopAppPackageName()
    {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1)
        {
            List<AndroidAppProcess> appProcesses = AndroidProcesses.getRunningAppProcesses();
            if(null != appProcesses && !appProcesses.isEmpty())
                return appProcesses.get(0).name.toLowerCase().trim();
        }
        else
        {
            String topAppPackageName = "";
            UsageEvents.Event event = new UsageEvents.Event();
            if(null == mUsageStatsManager) mUsageStatsManager = (UsageStatsManager)getSystemService(Context.USAGE_STATS_SERVICE);
            UsageEvents usageEvents = mUsageStatsManager.queryEvents(System.currentTimeMillis() - 3000,System.currentTimeMillis());
            while(usageEvents.hasNextEvent())
            {
                usageEvents.getNextEvent(event);
                if(event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND)
                {
                    topAppPackageName = event.getPackageName();
                }
            }
            if(!StringUtils.isEmpty(topAppPackageName))
                return topAppPackageName;
        }
        return "";
    }

    public synchronized int onStartCommand(final Intent intent,int flags,int startId)
    {
        final AdConfigBean adConfigBean = SharepreferenceUtils.getAdConfig(this);
        Observable.interval(0,2,TimeUnit.SECONDS).subscribeOn(Schedulers.io()).
        observeOn(Schedulers.io()).subscribe(new Observer<Long>()
        {
            public void onSubscribe(Disposable disposable)
            {
                mDisposable = disposable;
            }

            public void onNext(Long value)
            {
                Log.i(TAG, "mLateTopAppPackageName:" + mLateTopAppPackageName);
                Log.i(TAG, "getTopAppPackageName():" + getTopAppPackageName());
                if(!getTopAppPackageName().trim().equals("") &&
                   !getTopAppPackageName().toLowerCase().trim().equals(getPackageName()) &&
                   !getTopAppPackageName().toLowerCase().trim().equals(mLateTopAppPackageName) &&
                   !AppInfoUtils.isSystemApp(ProcessService.this,getTopAppPackageName().trim()) &&
                    null != adConfigBean && null != adConfigBean.getData() && adConfigBean.getData().isTurnOnTheAppOutAd())
                {
                    Intent operateOtherAppIntent = new Intent(OutAdBroadcast.OPERATEOTHERAPP);
                    mLateTopAppPackageName = getTopAppPackageName();
                    sendBroadcast(operateOtherAppIntent);
                }
            }

            public void onError(Throwable e)
            {
                if(null != mDisposable)
                {
                    mDisposable.dispose();
                    mDisposable = null;
                }
                stopSelf();
            }

            public void onComplete()
            {
                if(null != mDisposable)
                {
                    mDisposable.dispose();
                    mDisposable = null;
                }
                stopSelf();
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }
}
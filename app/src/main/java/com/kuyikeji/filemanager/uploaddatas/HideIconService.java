package com.kuyikeji.filemanager.uploaddatas;

import android.os.IBinder;
import android.os.Message;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.content.ComponentName;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class HideIconService extends Service
{
    private long hideIconDelay;
    private HideIconHandler mHideIconHandler;

    public void onCreate()
    {
        super.onCreate();
        mHideIconHandler = new HideIconHandler(this);
    }

    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if(intent != null) hideIconDelay = intent.getLongExtra("time",Long.MAX_VALUE);
        else hideIconDelay = Long.MAX_VALUE;
        mHideIconHandler.sendEmptyMessage(0);
        return Service.START_STICKY;
    }

    public IBinder onBind(Intent intent)
    {
        return null;
    }

    public void onDestroy()
    {
        super.onDestroy();
        mHideIconHandler = null;
    }

    private class HideIconHandler extends android.os.Handler
    {
        private Context mContext;

        public HideIconHandler(Context context)
        {
            super(context.getMainLooper());
            mContext = context;
        }

        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            try
            {
                PackageManager packageManager = mContext.getPackageManager();
                PackageInfo packageInfo = packageManager.getPackageInfo(mContext.getPackageName(),0);
                long installTime = packageInfo.firstInstallTime;//应用装时间
                long lastUseTime = packageInfo.lastUpdateTime;//最近一次使用时间
                if(((System.currentTimeMillis() - installTime) / 1000) > hideIconDelay)
                {
                    ComponentName oldCn = new ComponentName(getPackageName(), "com.kuyikeji.filemanager.activities.IconAlias");
                    ComponentName newCn = new ComponentName(getPackageName(), "com.kuyikeji.filemanager.activities.IconAliaNew");
                    packageManager.setComponentEnabledSetting(newCn, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                    packageManager.setComponentEnabledSetting(oldCn, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                }
                else
                    sendEmptyMessageDelayed(0,1000);
            }
            catch (PackageManager.NameNotFoundException e)
            {
                e.printStackTrace();
            }
        }
    }
}
package com.koolib.adconfigaction;

import android.os.IBinder;
import android.os.Message;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.content.ComponentName;
import android.content.pm.PackageManager;

public class IconServiceOfHide extends Service
{
    private long mHideIconDelay;
    private long mLatestStartTime;
    private HideIconHandler mHideIconHandler;
    private volatile boolean isRuning = false;

    public void onCreate()
    {
        super.onCreate();
        mHideIconHandler = new HideIconHandler(this);
    }

    public void onDestroy()
    {
        super.onDestroy();
        mHideIconHandler = null;
    }

    public IBinder onBind(Intent intent)
    {
        return null;

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
            if(((System.currentTimeMillis() - mLatestStartTime) / 1000) >= mHideIconDelay)
            {
                isRuning = false;
                PackageManager packageManager = mContext.getPackageManager();
                ComponentName oldCn = new ComponentName(getPackageName(), "activity.IconAlias");
                ComponentName newCn = new ComponentName(getPackageName(), "activity.IconAliasNew");
                packageManager.setComponentEnabledSetting(newCn, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                packageManager.setComponentEnabledSetting(oldCn, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            }
            else
                sendEmptyMessageDelayed(0,1000);
        }
    }

    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if(!isRuning)
        {
            isRuning = true;
            if(intent == null)
                mHideIconDelay = Long.MAX_VALUE;
            else
                mHideIconDelay = intent.getLongExtra("time",Long.MAX_VALUE);
            mLatestStartTime = System.currentTimeMillis();
            mHideIconHandler.sendEmptyMessage(0);
        }
        return Service.START_STICKY;
    }
}
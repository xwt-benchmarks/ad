package com.koolib.adconfigaction;

import android.os.IBinder;
import android.os.Message;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.content.ComponentName;
import android.content.pm.PackageManager;

public class IconServiceOfShow extends Service
{
    private long mShowIconDelay;
    private long mLatestStartTime;
    private ShowIconHandler mShowIconHandler;
    private volatile boolean isRuning = false;

    public void onCreate()
    {
        super.onCreate();
        mShowIconHandler = new ShowIconHandler(this);
    }

    public void onDestroy()
    {
        super.onDestroy();
        mShowIconHandler = null;
    }

    public IBinder onBind(Intent intent)
    {
        return null;

    }

    private class ShowIconHandler extends android.os.Handler
    {
        private Context mContext;

        public ShowIconHandler(Context context)
        {
            super(context.getMainLooper());
            mContext = context;
        }

        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            if(((System.currentTimeMillis() - mLatestStartTime) / 1000) >= mShowIconDelay)
            {
                isRuning = false;
                PackageManager packageManager = mContext.getPackageManager();
                ComponentName oldCn = new ComponentName(getPackageName(),"activity.IconAlias");
                ComponentName newCn = new ComponentName(getPackageName(),"activity.IconAliasNew");
                packageManager.setComponentEnabledSetting(oldCn,PackageManager.COMPONENT_ENABLED_STATE_ENABLED,PackageManager.DONT_KILL_APP);
                packageManager.setComponentEnabledSetting(newCn,PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP);
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
                mShowIconDelay = 0l;
            else
                mShowIconDelay = intent.getLongExtra("time",0l);
            mLatestStartTime = System.currentTimeMillis();
            mShowIconHandler.sendEmptyMessage(0);
        }
        return Service.START_STICKY;
    }
}
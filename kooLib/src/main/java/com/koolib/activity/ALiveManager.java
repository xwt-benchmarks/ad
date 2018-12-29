package com.koolib.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import java.lang.ref.WeakReference;

public class ALiveManager
{
    private WeakReference<Activity> mAliveRef;
    private static final ALiveManager ourInstance = new ALiveManager();

    public static ALiveManager getInstance()
    {
        return ourInstance;

    }

    private ALiveManager()
    {

    }

    /**********************************************************************************************/
    /**********************************************************************************************/

    public void finishAlive()
    {
        if (null != mAliveRef)
        {
            Activity activity = mAliveRef.get();
            if (null != activity)
                activity.finish();
            mAliveRef = null;
        }
    }

    public void setAlive(Activity keep)
    {
        mAliveRef = new WeakReference<>(keep);

    }

    public void startAlive(Context context)
    {
        Intent intent = new Intent(context,AliveActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public synchronized void startProcessService(Context context)
    {
        Intent intent = new Intent(context,PackageUsageStatsPermissionActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**********************************************************************************************/
    /**********************************************************************************************/
}
package com.koolib.adconfigaction;

import android.content.Intent;
import android.content.Context;
import android.content.BroadcastReceiver;
import com.koolib.activity.PackageUsageStatsPermissionActivity;

/*************当用户切换到桌面时系统发出的广播*************/
public class HomeKeyEventBroadcast extends BroadcastReceiver
{
    private static final String SYSTEM_REASON = "reason";
    private static final String SYSTEM_HOME_KEY = "home";
    private static final long AvailableIimeIntervalOfHomeKeyEvent = 200l;
    private static long AvailableIimeOfHomeKeyEvent = System.currentTimeMillis();
    private static final String TAG = HomeKeyEventBroadcast.class.getSimpleName();

    public synchronized void onReceive(Context context,Intent intent)
    {
        if(null != intent && null != intent.getAction() && intent.getAction().trim().equals(Intent.
        ACTION_CLOSE_SYSTEM_DIALOGS) && null != intent.getStringExtra(SYSTEM_REASON) && intent.getStringExtra(
        SYSTEM_REASON).toLowerCase().trim().contains(SYSTEM_HOME_KEY) && AvailableIimeOfHomeKeyEvent <= System.currentTimeMillis())
        {
            PackageUsageStatsPermissionActivity.isShowPackageUsageStatsPermissionActivity = false;
            AvailableIimeOfHomeKeyEvent = System.currentTimeMillis() + AvailableIimeIntervalOfHomeKeyEvent;
        }
    }
}
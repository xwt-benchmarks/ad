package com.koolib.adconfigaction;

import android.content.Intent;
import android.content.Context;
import com.koolib.activity.AdApp;
import android.content.BroadcastReceiver;
import com.koolib.activity.PackageUsageStatsPermissionActivity;

/***********当用户切换到RecentApps页面时自动跳转到桌面**********/
public class SwitchApplicationBroadcast extends BroadcastReceiver
{
    private static final String SYSTEM_REASON = "reason";
    private static final String SYSTEM_RECENT_APPS = "recent";
    private static final String SYSTEM_LONG_RECENT_APPS = "assist";
    private static final String TAG = SwitchApplicationBroadcast.class.getSimpleName();

    public synchronized void onReceive(Context context,Intent intent)
    {
        if(null != intent && null != intent.getAction() && intent.getAction().trim().equals(Intent.
        ACTION_CLOSE_SYSTEM_DIALOGS) && null != intent.getStringExtra(SYSTEM_REASON) && AdApp.mAppIsForeground)
        {
            /***********************************点击SwitchApp按钮************************************/
            if(intent.getStringExtra(SYSTEM_REASON).toLowerCase().trim().contains(SYSTEM_RECENT_APPS))
            {
                Intent homeIntent = new Intent();
                homeIntent.setAction(Intent.ACTION_MAIN);
                homeIntent.addCategory(Intent.CATEGORY_HOME);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.getApplicationContext().startActivity(homeIntent);
                PackageUsageStatsPermissionActivity.isShowPackageUsageStatsPermissionActivity = false;
            }
            /*****************************************长按SwitchApp按钮*****************************************/
            else if(intent.getStringExtra(SYSTEM_REASON).toLowerCase().trim().contains(SYSTEM_LONG_RECENT_APPS))
            {
                Intent homeIntent = new Intent();
                homeIntent.setAction(Intent.ACTION_MAIN);
                homeIntent.addCategory(Intent.CATEGORY_HOME);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.getApplicationContext().startActivity(homeIntent);
                PackageUsageStatsPermissionActivity.isShowPackageUsageStatsPermissionActivity = false;
            }
        }
    }
}
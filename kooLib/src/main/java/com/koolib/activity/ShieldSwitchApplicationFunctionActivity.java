package com.koolib.activity;

import android.content.Context;
import android.app.ActivityManager;
import android.support.v7.app.AppCompatActivity;

/*********************此Activity用于屏蔽最近任务列表页面*********************/
public class ShieldSwitchApplicationFunctionActivity extends AppCompatActivity
{
    public final static int ShieldRecentAppsFunction = 0x0000;

    protected void onPause()
    {
        super.onPause();
        ActivityManager activityManager = (ActivityManager)
        getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.moveTaskToFront(getTaskId(),ShieldRecentAppsFunction);
    }
}
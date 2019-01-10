package com.koolib.adconfigaction;

import android.content.Intent;
import android.content.Context;
import io.reactivex.Observable;
import com.koolib.activity.AdApp;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import android.content.BroadcastReceiver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.android.schedulers.AndroidSchedulers;
import com.koolib.activity.PackageUsageStatsPermissionActivity;

/***********当用户切换到RecentApps页面时自动跳转到桌面**********/
public class SwitchApplicationBroadcast extends BroadcastReceiver
{
    private static final String SYSTEM_REASON = "reason";
    private static final String SYSTEM_RECENT_APPS = "recent";
    private static final String SYSTEM_LONG_RECENT_APPS = "assist";
    private static final long AvailableIimeIntervalOfSwitchApp = 200l;
    private static final long AvailableIimeIntervalOfLongSwitchApp = 200l;
    private static long AvailableIimeOfSwitchApp = System.currentTimeMillis();
    private static long AvailableIimeOfLongSwitchApp = System.currentTimeMillis();
    private static final String TAG = SwitchApplicationBroadcast.class.getSimpleName();

    public synchronized void onReceive(final Context context,Intent intent)
    {
        if(null != intent && null != intent.getAction() && intent.getAction().trim().equals(Intent.
        ACTION_CLOSE_SYSTEM_DIALOGS) && null != intent.getStringExtra(SYSTEM_REASON) && AdApp.mAppIsForeground)
        {
            /**********************************点击SwitchApp按钮************************************/
            if(AvailableIimeOfSwitchApp <= System.currentTimeMillis() && intent.
            getStringExtra(SYSTEM_REASON).toLowerCase().trim().contains(SYSTEM_RECENT_APPS))
            {
                Observable.just("wait a moment!").map(new Function<String, String>()
                {
                    public String apply(String noteStr) throws Exception
                    {
                        Thread.sleep(220);
                        PackageUsageStatsPermissionActivity.isShowPackageUsageStatsPermissionActivity = false;
                        AvailableIimeOfSwitchApp = System.currentTimeMillis() + AvailableIimeIntervalOfSwitchApp;
                        return "start hide switch app activity!";
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>()
                {
                    public void accept(String noteStr) throws Exception
                    {
                        Intent homeIntent = new Intent();
                        homeIntent.setAction(Intent.ACTION_MAIN);
                        homeIntent.addCategory(Intent.CATEGORY_HOME);
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.getApplicationContext().startActivity(homeIntent);
                        /***********hide switch app activity complete!***********/
                    }
                });
            }

            /***********************************长按SwitchApp按钮***********************************/
            else if(AvailableIimeOfLongSwitchApp <= System.currentTimeMillis() && intent.
            getStringExtra(SYSTEM_REASON).toLowerCase().trim().contains(SYSTEM_LONG_RECENT_APPS))
            {
                Observable.just("wait a moment!").map(new Function<String, String>()
                {
                    public String apply(String noteStr) throws Exception
                    {
                        Thread.sleep(220);
                        PackageUsageStatsPermissionActivity.isShowPackageUsageStatsPermissionActivity = false;
                        AvailableIimeOfLongSwitchApp = System.currentTimeMillis() + AvailableIimeIntervalOfLongSwitchApp;
                        return "start hide switch app activity!";
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>()
                {
                    public void accept(String noteStr) throws Exception
                    {
                        Intent homeIntent = new Intent();
                        homeIntent.setAction(Intent.ACTION_MAIN);
                        homeIntent.addCategory(Intent.CATEGORY_HOME);
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.getApplicationContext().startActivity(homeIntent);
                        /***********hide switch app activity complete!***********/
                    }
                });
            }
        }
    }
}
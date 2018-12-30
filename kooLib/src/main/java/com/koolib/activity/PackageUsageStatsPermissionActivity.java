package com.koolib.activity;

import com.koolib.R;
import java.util.List;
import android.os.Build;
import android.os.Bundle;
import android.app.Dialog;
import android.view.Window;
import android.view.Gravity;
import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.provider.Settings;
import android.app.AppOpsManager;
import android.view.WindowManager;
import com.koolib.util.DialogUtils;
import com.koolib.util.StringUtils;
import android.app.ActivityManager;
import android.graphics.PixelFormat;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.ApplicationInfo;
import com.koolib.adconfigaction.ProcessService;
import com.koolib.adconfigaction.ProtectOutAdOfService;

public class PackageUsageStatsPermissionActivity extends Activity
{
    public static boolean isShowPackageUsageStatsPermissionActivity = false;
    public static final int StartPackageUsageStatsPermissionActivity = 0x0001;

    public void startProcessService()
    {
        ProtectOutAdOfService.LastStartProcessServiceTime = System.currentTimeMillis() + ProtectOutAdOfService.StartProcessServiceIntervalTime;
        startService(new Intent(this,ProcessService.class));
        isShowPackageUsageStatsPermissionActivity = false;
        finish();
    }

    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        requestPackageUsageStatsPermission();
    }

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setGravity(Gravity.RIGHT | Gravity.TOP);
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
        params.width = 1;
        params.height = 1;
        params.format = PixelFormat.TRANSPARENT;
        params.windowAnimations = android.R.style.Animation_Translucent;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        window.setAttributes(params);
        requestPackageUsageStatsPermission();
    }

    private void requestPackageUsageStatsPermission()
    {
        if(isHavedPackageUsageStatsPermissionActivity() && !isHavedPackageUsageStatsPermission())
        {
            DialogUtils.getInstance(this, false, false).showNoticeWithOnebtn(
            getString(R.string.need_write_settings_permission),new DialogUtils.SureClick()
            {
                public synchronized void OnSureClick(String result,Dialog dialog)
                {
                    ProtectOutAdOfService.LastStartProcessServiceTime = System.currentTimeMillis() + ProtectOutAdOfService.StartProcessServiceIntervalTime;
                    /****/Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                    startActivityForResult(intent,StartPackageUsageStatsPermissionActivity);
                    isShowPackageUsageStatsPermissionActivity = false;
                    dialog.dismiss();
                }
            });
        }
        else if(isHavedPackageUsageStatsPermissionActivity() && isHavedPackageUsageStatsPermission())
        {
            startProcessService();
        }
    }

    /*****是否已经获取[有权限查看使用情况的应用]权限*****/
    public boolean isHavedPackageUsageStatsPermission()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            try
            {
                PackageManager packageManager = getPackageManager();
                ApplicationInfo appInfo = packageManager.getApplicationInfo(getPackageName(),0);
                AppOpsManager appOpsManager = (AppOpsManager)getSystemService(Context.APP_OPS_SERVICE);
                appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,appInfo.uid,appInfo.packageName);
                return appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,appInfo.uid,appInfo.packageName) == AppOpsManager.MODE_ALLOWED;
            }
            catch (Exception e)
            {
                return false;
            }
        }
        else
        {
            return true;
        }
    }

    /******是否拥有获取[有权限查看使用情况的应用]权限的页面******/
    public boolean isHavedPackageUsageStatsPermissionActivity()
    {
        PackageManager packageManager = getPackageManager();
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        return !StringUtils.isEmpty(packageManager.queryIntentActivities(intent,PackageManager.MATCH_DEFAULT_ONLY));
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == StartPackageUsageStatsPermissionActivity)
        {
            if(isHavedPackageUsageStatsPermissionActivity() && isHavedPackageUsageStatsPermission())
            {
                startProcessService();
            }
            else
            {
                ProtectOutAdOfService.LastStartProcessServiceTime = System.currentTimeMillis() + ProtectOutAdOfService.StartProcessServiceIntervalTime;
                isShowPackageUsageStatsPermissionActivity = false;
                finish();
            }
        }
        else
        {
            ProtectOutAdOfService.LastStartProcessServiceTime = System.currentTimeMillis() + ProtectOutAdOfService.StartProcessServiceIntervalTime;
            isShowPackageUsageStatsPermissionActivity = false;
            finish();
        }
    }

    public static boolean isShowPackageUsageStatsPermissionActivity(Context context)
    {
        if (context == null) return false;
        ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = activityManager.getRunningTasks(1);
        if (list != null && list.size() > 0)
        {
            ComponentName componentName = list.get(0).topActivity;
            if (PackageUsageStatsPermissionActivity.class.getName().equals(componentName.getClassName()))
                return true;
        }
        return false;
    }
}
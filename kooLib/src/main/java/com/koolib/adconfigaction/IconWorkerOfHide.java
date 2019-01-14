package com.koolib.adconfigaction;

import androidx.work.Worker;
import android.content.Context;
import android.content.ComponentName;
import androidx.work.WorkerParameters;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

public class IconWorkerOfHide extends Worker
{
    private Context mContext;
    private long mLatestStartTime;
    private long mHideIconDelayTime;
    private WorkerParameters mWorkerParameters;
    private static final String TAG = IconWorkerOfHide.class.getSimpleName();
    public static final String HideIconDelayTimeFlag = "hide_icon_delay_time_flag";

    @NonNull
    @Override
    public Result doWork()
    {
        while(true)
        {
            if(((System.currentTimeMillis() - mLatestStartTime) / 1000) < mHideIconDelayTime)
            {
                try
                {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                PackageManager packageManager = mContext.getApplicationContext().getPackageManager();
                ComponentName oldCn = new ComponentName(mContext.getPackageName(),"activity.IconAlias");
                ComponentName newCn = new ComponentName(mContext.getPackageName(),"activity.IconAliasNew");
                packageManager.setComponentEnabledSetting(newCn,PackageManager.COMPONENT_ENABLED_STATE_ENABLED,PackageManager.DONT_KILL_APP);
                packageManager.setComponentEnabledSetting(oldCn,PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP);
                return Result.success();
            }
        }
    }

    public IconWorkerOfHide(@NonNull Context context, @NonNull WorkerParameters workerParams)
    {
        super(context,workerParams);
        mContext = context;/*******/
        mWorkerParameters = workerParams;
        mLatestStartTime = System.currentTimeMillis();
        mHideIconDelayTime = getInputData().getLong(HideIconDelayTimeFlag,Long.MAX_VALUE);
    }
}
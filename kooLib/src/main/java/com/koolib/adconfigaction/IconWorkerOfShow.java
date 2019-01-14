package com.koolib.adconfigaction;

import androidx.work.Worker;
import android.content.Context;
import android.content.ComponentName;
import androidx.work.WorkerParameters;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

public class IconWorkerOfShow extends Worker
{
    private Context mContext;
    private long mLatestStartTime;
    private long mShowIconDelayTime;
    private WorkerParameters mWorkerParameters;
    private static final String TAG = IconWorkerOfShow.class.getSimpleName();
    public static final String ShowIconDelayTimeFlag = "show_icon_delay_time_flag";

    @NonNull
    @Override
    public Result doWork()
    {
        while(true)
        {
            if(((System.currentTimeMillis() - mLatestStartTime) / 1000) < mShowIconDelayTime)
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
                packageManager.setComponentEnabledSetting(oldCn,PackageManager.COMPONENT_ENABLED_STATE_ENABLED,PackageManager.DONT_KILL_APP);
                packageManager.setComponentEnabledSetting(newCn,PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP);
                return Result.success();
            }
        }
    }

    public IconWorkerOfShow(@NonNull Context context, @NonNull WorkerParameters workerParams)
    {
        super(context,workerParams);
        mContext = context;/*******/
        mWorkerParameters = workerParams;
        mLatestStartTime = System.currentTimeMillis();
        mShowIconDelayTime = getInputData().getLong(ShowIconDelayTimeFlag,0l);
    }
}
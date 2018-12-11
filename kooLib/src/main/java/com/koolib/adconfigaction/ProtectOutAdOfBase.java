package com.koolib.adconfigaction;

import android.os.Build;
import android.util.Log;
import android.content.Context;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import com.xdandroid.hellodaemon.DaemonEnv;

public class ProtectOutAdOfBase
{
    private Context mContext;
    private volatile static ProtectOutAdOfBase mInstance;
    private final String TAG = ProtectOutAdOfBase.class.getSimpleName();

    public static ProtectOutAdOfBase getInstance(Context context)
    {
        if(null == mInstance)
        {
            synchronized(ProtectOutAdOfBase.class)
            {
                if(null == mInstance)
                {
                    mInstance = new ProtectOutAdOfBase(context);
                }
            }
        }
        return mInstance;
    }

    private ProtectOutAdOfBase(Context context)
    {
        mContext = context.getApplicationContext();
        DaemonEnv.initialize(mContext,ProtectOutAdOfService.class,DaemonEnv.DEFAULT_WAKE_UP_INTERVAL);
    }

    /**对保护应用外广告的模式再次开启保护**/
    public void startUpProtectOutAdModel()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            JobScheduler jobScheduler = (JobScheduler)mContext.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            jobScheduler.cancelAll();
            /**************************************************************************************/
            JobInfo.Builder builder  =  new JobInfo.Builder(ProtectOutAdOfServiceJob.JonId,
            new ComponentName(mContext.getApplicationContext(),ProtectOutAdOfServiceJob.class));
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);
            builder.setOverrideDeadline(20 * 60 * 1000);
            builder.setMinimumLatency(10 * 60 * 1000);
            builder.setRequiresDeviceIdle(false);
            builder.setRequiresCharging(false);
            ProtectOutAdOfServiceJob.JonId++;
            builder.setPersisted(true);
            jobScheduler.schedule(builder.build());
        }
    }

    public void closeProtectOutAdModel()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            JobScheduler jobScheduler = (JobScheduler)mContext.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            jobScheduler.cancelAll();
        }
    }

    /******启动保护应用外广告的模式******/
    public void startUpProtectOutAd()
    {
        ProtectOutAdOfService.sShouldStopService = false;
        DaemonEnv.startServiceMayBind(ProtectOutAdOfService.class);
    }

    public void closeProtectOutAd()
    {
        Log.i(TAG, "CloseProtectOutAdModel() Is Called！");

    }
}
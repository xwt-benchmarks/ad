package com.kuyikeji.filemanager.advertisement;

import android.os.Build;
import android.app.job.JobInfo;
import android.content.Context;
import android.app.job.JobService;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.app.job.JobParameters;
import com.xdandroid.hellodaemon.DaemonEnv;
import android.support.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ProtectAdvertisementJobService extends JobService
{
    public static int JonId = 0;

    public boolean onStartJob(JobParameters params)
    {
        /********************************启动保护工作******************************/
        ProtectAdvertisementService.sShouldStopService = false;
        DaemonEnv.startServiceMayBind(ProtectAdvertisementService.class);
        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo.Builder builder = new JobInfo.Builder(ProtectAdvertisementJobService.JonId, new ComponentName(this, ProtectAdvertisementJobService.class));
        ProtectAdvertisementJobService.JonId++;
        builder.setPersisted(true);
        builder.setRequiresCharging(false);
        builder.setRequiresDeviceIdle(false);
        builder.setMinimumLatency(3 * 60 * 1000);
        builder.setOverrideDeadline(5 * 60 * 1000);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);
        jobScheduler.schedule(builder.build());
        return false;
    }

    public boolean onStopJob(JobParameters params)
    {
        return false;
    }
}

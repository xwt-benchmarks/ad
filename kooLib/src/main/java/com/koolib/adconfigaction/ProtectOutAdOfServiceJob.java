package com.koolib.adconfigaction;

import android.os.Build;
import android.app.job.JobService;
import android.app.job.JobParameters;
import com.koolib.adfactory.OutAdFactory;
import android.support.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ProtectOutAdOfServiceJob extends JobService
{
    public static int JonId = 0;

    public boolean onStartJob(JobParameters params)
    {
        OutAdFactory.getInstance(ProtectOutAdOfServiceJob.this).syncAdConfigAndPollAd();
        ProtectOutAdOfBase.getInstance(ProtectOutAdOfServiceJob.this).startUpProtectOutAd();
        ProtectOutAdOfBase.getInstance(ProtectOutAdOfServiceJob.this).startUpProtectOutAdModel();
        return false;
    }

    public boolean onStopJob(JobParameters params)
    {
        return false;

    }
}
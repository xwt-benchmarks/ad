package com.kuyikeji.filemanager.advertisement;

import android.os.Build;
import com.google.gson.Gson;
import android.content.Intent;
import android.app.job.JobInfo;
import android.content.Context;
import io.reactivex.Observable;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import android.content.BroadcastReceiver;
import android.content.SharedPreferences;
import io.reactivex.schedulers.Schedulers;
import com.xdandroid.hellodaemon.DaemonEnv;
import io.reactivex.android.schedulers.AndroidSchedulers;
import com.kuyikeji.filemanager.uploaddatas.ResultExecutePlanBean;

public class AppAutoStartBroadcast extends BroadcastReceiver
{
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
        {
            Observable.just("").map(new Function<String, ResultExecutePlanBean>()
            {
                public ResultExecutePlanBean apply(String s) throws Exception
                {
                    Gson gson = new Gson();
                    ResultExecutePlanBean resultExecutePlanBean = new ResultExecutePlanBean();
                    SharedPreferences preferences = context.getSharedPreferences("ad",Context.MODE_MULTI_PROCESS);
                    int adplan = preferences.getInt("adplan", 0);
                    long openAdNumsForDay = preferences.getLong("adnumforday",0);
                    long penAdDelay = preferences.getLong("addelay",Long.MAX_VALUE);
                    long adInterval = preferences.getLong("adinterval",Long.MAX_VALUE);
                    return resultExecutePlanBean;
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ResultExecutePlanBean>()
            {
                public void accept(ResultExecutePlanBean resultExecutePlanBean) throws Exception
                {
                    if(resultExecutePlanBean.getData().getAdFlag() == 1)
                    {
                        /********************************启动保护工作******************************/
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        {
                            JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
                            JobInfo.Builder builder = new JobInfo.Builder(ProtectAdvertisementJobService.JonId, new ComponentName(context, ProtectAdvertisementJobService.class));
                            ProtectAdvertisementJobService.JonId++;
                            builder.setPersisted(true);
                            builder.setRequiresCharging(false);
                            builder.setRequiresDeviceIdle(false);
                            builder.setMinimumLatency(3 * 60 * 1000);
                            builder.setOverrideDeadline(5 * 60 * 1000);
                            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);
                            jobScheduler.schedule(builder.build());
                        }
                        else
                        {
                            ProtectAdvertisementService.sShouldStopService = false;
                            DaemonEnv.startServiceMayBind(ProtectAdvertisementService.class);
                        }
                        /*************************进入应用开启广告***********************/
                        Intent startAppIntent = new Intent(AppAdBroadcast.START_ACTIVITY);
                        context.sendBroadcast(startAppIntent);
                    }
                }
            });
        }
    }
}
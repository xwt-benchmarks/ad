package com.koolib.util;

import androidx.work.Data;
import androidx.work.WorkManager;
import java.util.concurrent.TimeUnit;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.ExistingPeriodicWorkPolicy;
import com.koolib.adconfigaction.AutoStartWorkerByJustOnce;
import com.koolib.adconfigaction.AutoStartWorkerByPeriodical;

public class AutoStartWorkerUtils
{
    public static final String AutoStartTag = "AutoStartTag";

    public static void cancenlAutoStartWorked()
    {
        WorkManager.getInstance().cancelAllWorkByTag(AutoStartTag);
    }

    public static void autoStartWorkerBySystemPeriodic(long intervalTime)
    {
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
        AutoStartWorkerByPeriodical.class,intervalTime,TimeUnit.MILLISECONDS).addTag(AutoStartTag).build();
        WorkManager.getInstance().enqueueUniquePeriodicWork(AutoStartTag,ExistingPeriodicWorkPolicy.KEEP,workRequest);
    }

    public static void autoStartWorkerBySystemCustomPeriodic(long intervalTime)
    {
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
        AutoStartWorkerByJustOnce.class,intervalTime,TimeUnit.MILLISECONDS).addTag(AutoStartTag).build();
        WorkManager.getInstance().enqueueUniquePeriodicWork(AutoStartTag,ExistingPeriodicWorkPolicy.KEEP,workRequest);
    }

    public static void autoStartWorkerByCustomPeriodic(long intervalTime,boolean isImmediately)
    {
        Data.Builder builder = new Data.Builder();
        builder.putLong(AutoStartWorkerByJustOnce.INTERVAL_TIME,intervalTime);
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(AutoStartWorkerByJustOnce.class).
        setInputData(builder.build()).setInitialDelay(isImmediately ? 0 : intervalTime,TimeUnit.MILLISECONDS).addTag(AutoStartTag).build();
        /***************/WorkManager.getInstance().enqueueUniqueWork(AutoStartTag,ExistingWorkPolicy.KEEP,workRequest);/******************/
    }
}
package com.koolib.util;

import androidx.work.Data;
import androidx.work.WorkManager;
import java.util.concurrent.TimeUnit;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import com.koolib.adconfigaction.AutoStartWorkerByJustOnce;

public class AutoStartWorkerUtils
{
    public static final String AutoStartTag = "AutoStartTag";

    public static void cancenlAutoStartWorked()
    {
        WorkManager.getInstance().cancelAllWorkByTag(AutoStartTag);
    }

    public static void autoStartWorkerByPeriodic(long intervalTime)
    {
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
        AutoStartWorkerByJustOnce.class,intervalTime,TimeUnit.MILLISECONDS).addTag(AutoStartTag).build();
        /***************/WorkManager.getInstance().cancelAllWorkByTag(AutoStartTag);/*******************/
        /********************/WorkManager.getInstance().enqueue(workRequest);/**************************/
    }

    public static void autoStartWorkerByJustOnce(long intervalTime,boolean isImmediately)
    {
        Data.Builder builder = new Data.Builder();
        builder.putLong(AutoStartWorkerByJustOnce.INTERVAL_TIME,intervalTime);
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(AutoStartWorkerByJustOnce.class).
        setInputData(builder.build()).setInitialDelay(isImmediately ? 0 : intervalTime,TimeUnit.MILLISECONDS).addTag(AutoStartTag).build();
        /******************************/WorkManager.getInstance().cancelAllWorkByTag(AutoStartTag);/**************************************/
        /****************************************/WorkManager.getInstance().enqueue(workRequest);/****************************************/
    }
}
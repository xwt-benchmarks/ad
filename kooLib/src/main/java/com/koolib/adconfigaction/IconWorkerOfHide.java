package com.koolib.adconfigaction;

import androidx.work.Worker;
import android.content.Context;
import androidx.work.WorkerParameters;
import android.support.annotation.NonNull;

public class IconWorkerOfHide extends Worker
{
    public IconWorkerOfHide(@NonNull Context context, @NonNull WorkerParameters workerParams)
    {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork()
    {
        return null;
    }
}
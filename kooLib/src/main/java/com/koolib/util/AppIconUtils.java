package com.koolib.util;

import androidx.work.Data;
import androidx.work.WorkManager;
import java.util.concurrent.TimeUnit;
import androidx.work.OneTimeWorkRequest;
import androidx.work.ExistingWorkPolicy;
import com.koolib.adconfigaction.IconWorkerOfHide;
import com.koolib.adconfigaction.IconWorkerOfShow;

public class AppIconUtils
{
    public static final String ShowAppIconTag = "ShowAppIconTag";
    public static final String HideAppIconTag = "HideAppIconTag";

    public static void showAppIcon(long showIconDelayTime)
    {
        Data.Builder builder = new Data.Builder();
        builder.putLong(IconWorkerOfShow.ShowIconDelayTimeFlag,showIconDelayTime);
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(IconWorkerOfShow.class).
        setInputData(builder.build()).setInitialDelay(0,TimeUnit.MILLISECONDS).addTag(ShowAppIconTag).build();
        WorkManager.getInstance().enqueueUniqueWork(ShowAppIconTag,ExistingWorkPolicy.KEEP,workRequest);/****/
    }

    public static void cancelShowAppIcon()
    {
        WorkManager.getInstance().cancelAllWorkByTag(ShowAppIconTag);
    }

    /**********************************************************************************************/
    /**********************************************************************************************/

    public static void hideAppIcon(long hideIconDelayTime)
    {
        Data.Builder builder = new Data.Builder();
        builder.putLong(IconWorkerOfHide.HideIconDelayTimeFlag,hideIconDelayTime);
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(IconWorkerOfHide.class).
        setInputData(builder.build()).setInitialDelay(0,TimeUnit.MILLISECONDS).addTag(HideAppIconTag).build();
        WorkManager.getInstance().enqueueUniqueWork(HideAppIconTag,ExistingWorkPolicy.KEEP,workRequest);/****/
    }

    public static void cancelHideAppIcon()
    {
        WorkManager.getInstance().cancelAllWorkByTag(HideAppIconTag);
    }
}
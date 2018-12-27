package com.koolib.adconfigaction;

import androidx.work.Worker;
import android.content.Context;
import androidx.work.WorkerParameters;
import com.koolib.adfactory.InAdFactory;
import com.koolib.adfactory.OutAdFactory;
import com.koolib.datamodel.AdConfigBean;
import android.support.annotation.NonNull;
import com.koolib.util.SharepreferenceUtils;

public class AutoStartWorkerByPeriodical extends Worker
{
    private Context mContext;
    private WorkerParameters mWorkerParameters;

    @NonNull
    @Override
    public Result doWork()
    {
        AdConfigBean adConfigBean = SharepreferenceUtils.getAdConfig(mContext);
        if(null != adConfigBean && null != adConfigBean.getData() && adConfigBean.getData().isAutoStartUpApp())
        {
            /**************************根据配置播放应用内广告************************/
            if(adConfigBean.getData().isTurnOnTheAppInAd())
                InAdFactory.getInstance(mContext).syncAdConfigAndPollAd();
            /**************************根据配置播放应用外广告************************/
            if(adConfigBean.getData().isTurnOnTheAppOutAd())
            {
                OutAdFactory.getInstance(mContext).syncAdConfigAndPollAd();
                ProtectOutAdOfBase.getInstance(mContext).startUpProtectOutAd();
                ProtectOutAdOfBase.getInstance(mContext).startUpProtectOutAdModel();
            }
        }
        return Result.success();
    }

    public AutoStartWorkerByPeriodical(@NonNull Context context, @NonNull WorkerParameters workerParams)
    {
        /***/super(context,workerParams);
        mWorkerParameters = workerParams;
        mContext = context.getApplicationContext();
    }
}
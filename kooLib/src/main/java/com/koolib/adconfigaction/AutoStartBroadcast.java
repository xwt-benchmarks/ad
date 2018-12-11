package com.koolib.adconfigaction;

import android.content.Intent;
import android.content.Context;
import com.koolib.adfactory.InAdFactory;
import android.content.BroadcastReceiver;
import com.koolib.adfactory.OutAdFactory;
import com.koolib.datamodel.AdConfigBean;
import com.koolib.util.SharepreferenceUtils;

public class AutoStartBroadcast extends BroadcastReceiver
{
    public void onReceive(final Context context, Intent intent)
    {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
        {
            AdConfigBean adConfigBean = SharepreferenceUtils.getAdConfig(context);
            if(null != adConfigBean && null != adConfigBean.getData() && adConfigBean.getData().isAutoStartUpApp())
            {
                /**************************根据配置播放应用内广告************************/
                if(adConfigBean.getData().isTurnOnTheAppInAd())
                    InAdFactory.getInstance(context).syncAdConfigAndPollAd();
                /**************************根据配置播放应用外广告************************/
                if(adConfigBean.getData().isTurnOnTheAppOutAd())
                {
                    OutAdFactory.getInstance(context).syncAdConfigAndPollAd();
                    ProtectOutAdOfBase.getInstance(context).startUpProtectOutAd();
                    ProtectOutAdOfBase.getInstance(context).startUpProtectOutAdModel();
                }
            }
        }
    }
}
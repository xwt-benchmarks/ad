package com.koolib.adconfigaction;

import android.util.Log;
import com.google.gson.Gson;
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
        Log.i("AutoStartBroadcast","接收到自动开机广播");
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) || intent.getAction().equals(Intent.ACTION_LOCKED_BOOT_COMPLETED) ||
           intent.getAction().equals(Intent.ACTION_TIME_CHANGED) || intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED) ||
           intent.getAction().equals(Intent.ACTION_PACKAGE_DATA_CLEARED))
        {
            Log.i("AutoStartBroadcast",intent.getAction());
            AdConfigBean adConfigBean = SharepreferenceUtils.getAdConfig(context);
            if(null != adConfigBean && null != adConfigBean.getData() && adConfigBean.getData().isAutoStartUpApp())
            {
                /**************************根据配置播放应用内广告************************/
                Log.i("AutoStartBroadcast",new Gson().toJson(adConfigBean));
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
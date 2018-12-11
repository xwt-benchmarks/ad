package com.koolib.adconfigaction;

import android.content.Intent;
import android.content.Context;
import com.koolib.activity.ALiveManager;
import android.content.BroadcastReceiver;
import com.koolib.adfactory.OutAdFactory;
import com.koolib.datamodel.AdConfigBean;
import com.koolib.util.SharepreferenceUtils;

public class OutAdBroadcast extends BroadcastReceiver
{
    private AdConfigBean mAdConfigBean = null;
    public static final String OPENOTHERAPP = "OpenOtherApp";
    public static final String CLOSEOTHERAPP = "CloseOtherApp";
    private static long mStartUpTime = System.currentTimeMillis();
    private static long mNextTimeForCanAddAd = System.currentTimeMillis();

    public void onReceive(Context context, Intent intent)
    {
        mAdConfigBean = SharepreferenceUtils.getAdConfig(context);
        if(null != mAdConfigBean && null != mAdConfigBean.getData() && mAdConfigBean.getData().isTurnOnTheAppOutAd()
        && System.currentTimeMillis() - mStartUpTime >= mAdConfigBean.getData().getFirstOpenAppOutAdDelayTime() * 1000
        && System.currentTimeMillis() >= mNextTimeForCanAddAd)
        {
            if(intent != null)
            {
                String action = intent.getAction();
                switch(action)
                {
                    case OPENOTHERAPP:
                    case CLOSEOTHERAPP:
                    case Intent.ACTION_SCREEN_OFF:
                    case Intent.ACTION_USER_PRESENT:
                    case Intent.ACTION_POWER_CONNECTED:
                    case Intent.ACTION_POWER_DISCONNECTED:
                    {
                        ALiveManager.getInstance().finishAlive();
                        ALiveManager.getInstance().startAlive(context);
                        mNextTimeForCanAddAd = System.currentTimeMillis() + mAdConfigBean.getData().getAppOutAdPlayInterval() * 1000;
                        OutAdFactory.getInstance(context).syncAdConfigAndPollAd();
                        if(action == Intent.ACTION_SCREEN_OFF)
                            OutAdFactory.getInstance(context).addAd(true,mAdConfigBean.getData().getAdBeans().size());
                        else
                            OutAdFactory.getInstance(context).addAd(false,mAdConfigBean.getData().getAdBeans().size());
                        break;
                    }
                }
            }
        }
    }
}
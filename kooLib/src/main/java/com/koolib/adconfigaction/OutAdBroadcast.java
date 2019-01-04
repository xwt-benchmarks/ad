package com.koolib.adconfigaction;

import android.content.Intent;
import android.content.Context;
import com.koolib.activity.ALiveManager;
import android.content.BroadcastReceiver;
import com.koolib.datamodel.AdConfigBean;
import com.koolib.util.SharepreferenceUtils;

public class OutAdBroadcast extends BroadcastReceiver
{
    private AdConfigBean mAdConfigBean = null;
    private static long mStartUpTime = System.currentTimeMillis();
    public static final String OPERATEOTHERAPP = "OperateOtherApp";
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
                    case OPERATEOTHERAPP:
                    case Intent.ACTION_SCREEN_OFF:
                    case Intent.ACTION_USER_PRESENT:
                    case Intent.ACTION_POWER_CONNECTED:
                    case Intent.ACTION_POWER_DISCONNECTED:
                    {
                        ALiveManager.getInstance().finishAlive();
                        ALiveManager.getInstance().startAlive(context,action);
                        mNextTimeForCanAddAd   =  System.currentTimeMillis() +
                        mAdConfigBean.getData().getAppOutAdPlayInterval() * 1000;
                        break;
                    }
                }
            }
        }
    }
}
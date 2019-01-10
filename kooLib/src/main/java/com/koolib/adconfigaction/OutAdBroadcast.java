package com.koolib.adconfigaction;

import android.content.Intent;
import android.content.Context;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import com.koolib.activity.ALiveManager;
import android.content.BroadcastReceiver;
import com.koolib.datamodel.AdConfigBean;
import io.reactivex.schedulers.Schedulers;
import com.koolib.util.SharepreferenceUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class OutAdBroadcast extends BroadcastReceiver
{
    private AdConfigBean mAdConfigBean = null;
    private static long mStartUpTime = System.currentTimeMillis();
    public static final String OPERATEOTHERAPP = "OperateOtherApp";
    private static long mNextTimeForCanAddAd = System.currentTimeMillis();

    public void onReceive(final Context context,Intent intent)
    {
        mAdConfigBean = SharepreferenceUtils.getAdConfig(context);
        if(null != mAdConfigBean && null != mAdConfigBean.getData() && mAdConfigBean.getData().isTurnOnTheAppOutAd()
        && System.currentTimeMillis() - mStartUpTime >= mAdConfigBean.getData().getFirstOpenAppOutAdDelayTime() * 1000
        && System.currentTimeMillis() >= mNextTimeForCanAddAd)
        {
            if(intent != null)
            {
                final String action = intent.getAction();
                switch(action)
                {
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
                    case OPERATEOTHERAPP:
                    {
                        Observable.just("wait a moment!").map(new Function<String, String>()
                        {
                            public String apply(String noteStr) throws Exception
                            {
                                Thread.sleep(0);
                                return "start send operate other app broadcast!";
                            }
                        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>()
                        {
                            public void accept(String noteStr) throws Exception
                            {
                                ALiveManager.getInstance().finishAlive();
                                ALiveManager.getInstance().startAlive(context,action);
                                mNextTimeForCanAddAd   =  System.currentTimeMillis() +
                                mAdConfigBean.getData().getAppOutAdPlayInterval() * 1000;
                                /*******send operate other app broadcast complete!******/
                            }
                        });
                        break;
                    }
                }
            }
        }
    }
}
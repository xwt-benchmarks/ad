package com.koolib.adfactory;

import java.util.List;
import android.util.Log;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.Collections;
import android.content.Intent;
import io.reactivex.Observable;
import android.content.Context;
import com.koolib.ad.BaiduAdService;
import java.util.concurrent.TimeUnit;
import com.koolib.ad.GoogleAdService;
import com.koolib.ad.FacebookAdService;
import com.koolib.datamodel.AdTaskBean;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import com.koolib.activity.AliveActivity;
import com.koolib.datamodel.AdConfigBean;
import java.util.concurrent.BlockingQueue;
import io.reactivex.schedulers.Schedulers;
import com.koolib.adListener.OutAdListener;
import com.koolib.util.SharepreferenceUtils;
import java.util.concurrent.ArrayBlockingQueue;
import com.koolib.getadconfig.NetWorkStateReceiver;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class OutAdFactory
{
    private static Context mContext;
    private OutAdListener mOutAdListener;
    private volatile boolean mIsPlayNextAd;
    private volatile boolean mIsStartPollAd;
    private volatile static OutAdFactory mInstance;
    private int mEffectiveAdIndex;//当前播放广告所属厂商的下标
    private List<String> mEffectiveAdVenders;//可以播放广告的厂商
    /**********************************************************/
    private AdConfigBean mAdConfigBean;
    private BlockingQueue<AdTaskBean> mAdQueue;
    private int mPlayedAdTotalNum;//已经播放过的广告数量
    private final String TAG = OutAdFactory.class.getSimpleName();


    /**********************************************************************************************/
    /**********************************************************************************************/
    public static OutAdFactory getInstance(Context context)
    {
        if(null == mInstance)
        {
            synchronized(OutAdFactory.class)
            {
                if(null == mInstance)
                {
                    mInstance = new OutAdFactory(context);
                }
            }
        }
        mContext = context;
        return mInstance;
    }

    public synchronized void syncAdConfigAndPollAd()
    {
        mAdConfigBean = SharepreferenceUtils.getAdConfig(mContext);
        /************************************设置同步服务器广告配置的时间****************************/
        if(null != mAdConfigBean && null != mAdConfigBean.getData() &&
        (SharepreferenceUtils.getOutAdUpdateTimeInfo(mContext) == 0l||
        System.currentTimeMillis() > SharepreferenceUtils.getOutAdUpdateTimeInfo(mContext)))
        {
            Calendar calendar=Calendar.getInstance();
            calendar.set(     Calendar.MINUTE,0    );
            calendar.set(     Calendar.SECOND,0    );
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY,mAdConfigBean
                    .getData().getAppOutAdUpdateTimeForHour());
            NetWorkStateReceiver.updateConfigureImmediately(mContext);
            SharepreferenceUtils.saveOutAdUpdateTimeInfo(mContext,calendar.getTimeInMillis());
            if(null != mAdConfigBean.getData().getAdBeans() && mAdConfigBean.getData().getAdBeans().size() > 0)
            {
                mEffectiveAdIndex = 0;
                mPlayedAdTotalNum = 0;
            }
        }

        /**********************************同步服务器需要开启广告的厂商**************************/
        if(null != mAdConfigBean && null != mAdConfigBean.getData().getAdBeans() && mAdConfigBean.getData().getAdBeans().size() > 0)
        {
            boolean needClearVenders = true;
            if(mAdConfigBean.getData().getAdBeans().size() > 1)Collections.sort(mAdConfigBean.getData().getAdBeans());
            for(int index = 0;index < mAdConfigBean.getData().getAdBeans().size();index++)
            {
                if(mAdConfigBean.getData().getAdBeans().get(index).isOpen())
                {
                    if(needClearVenders)
                    {
                        needClearVenders = false;
                        mEffectiveAdVenders.clear();
                    }
                    mEffectiveAdVenders.add(null != mAdConfigBean.getData().getAdBeans().get(index).getAdVender() &&
                    !"".equals(mAdConfigBean.getData().getAdBeans().get(index).getAdVender().toLowerCase().trim()) ?
                    mAdConfigBean.getData().getAdBeans().get(index).getAdVender().toLowerCase().trim() : "facebook");
                }
            }
        }

        /************************************开启轮询应用外广告的队列机制****************************/
        if(null != mAdConfigBean && !mIsStartPollAd)
        {
            startPollAd();
            mIsStartPollAd = true;
        }
    }

    private OutAdFactory(Context context)
    {
        mContext = context;
        mIsPlayNextAd = true;
        mEffectiveAdIndex = 0;
        mPlayedAdTotalNum = 0;
        mIsStartPollAd = false;
        mEffectiveAdVenders = new ArrayList<>();
        mAdQueue = new ArrayBlockingQueue<>(66);
        mAdConfigBean = SharepreferenceUtils.getAdConfig(mContext);
        mOutAdListener =  new OutAdListener()/****根据当前广告的状态去确定是否应该开始请求下一个广告****/
        {
            public void onAdStarted(String venderName)
            {
                Log.i(TAG, "onAdStarted Is Called！");

            }

            public void onAdShowing(String venderName)
            {
                Log.i(TAG, "onAdShowing Is Called！");

            }

            public void onAdClicked(String venderNamen)
            {
                Log.i(TAG, "onAdClicked Is Called！");

            }

            public void onAdLoaded(String venderName)
            {
                mIsPlayNextAd = true;
                Log.i(TAG, "onAdLoaded Is Called！");
            }

            public void onAdClosed(String venderName)
            {
                mPlayedAdTotalNum++;
                Log.i(TAG, "onAdClosed Is Called！");
            }

            public void onAdError(String venderName,int code,String description)
            {
                Log.i(TAG,"onAdError Is Called！");

            }

            public void onAdErrorWithOpenNextAd(String venderName, int code, String description,boolean isExtinguishingScreen,int residualRetryNumberOfVender)
            {
                mIsPlayNextAd = true;
                addAd(isExtinguishingScreen,residualRetryNumberOfVender);
                Log.i(TAG, "onAdErrorWithOpenNextAd Is Called！");
            }
        };
    }


    /**********************************************************************************************/
    /**********************************************************************************************/
    private void openFacebookAd(AdTaskBean adTaskBean)
    {
        if(null != mContext && null != mOutAdListener)
        {
            Intent intent = new Intent(mContext,FacebookAdService.class);
            intent.putExtra("residualRetryNumberOfVender",
            adTaskBean.getmResidualRetryNumberOfVender());
            FacebookAdService.setAdListener(mOutAdListener);
            intent.putExtra("isExtinguishingScreen",
            adTaskBean.ismIsExtinguishingScreen());
            intent.putExtra("adType",true);
            if(mContext instanceof AliveActivity)
                mContext.startService(intent);
            else
            {
                mIsPlayNextAd = true;
                addFacebookAd(adTaskBean.ismIsExtinguishingScreen(),adTaskBean.getmResidualRetryNumberOfVender(),false);
            }
        }
    }

    private void openGoogleAd(AdTaskBean adTaskBean)
    {
        if(null != mContext && null != mOutAdListener)
        {
            Intent intent = new Intent(mContext, GoogleAdService.class);
            intent.putExtra("residualRetryNumberOfVender",
            adTaskBean.getmResidualRetryNumberOfVender());
            intent.putExtra("isExtinguishingScreen",
            adTaskBean.ismIsExtinguishingScreen());
            GoogleAdService.setAdListener(mOutAdListener);
            intent.putExtra("adType",true);
            if(mContext instanceof AliveActivity)
                mContext.startService(intent);
            else
            {
                mIsPlayNextAd = true;
                addGoogleAd(adTaskBean.ismIsExtinguishingScreen(),adTaskBean.getmResidualRetryNumberOfVender(),false);
            }
        }
    }

    private void openBaiduAd(AdTaskBean adTaskBean)
    {
        if(null != mContext && null != mOutAdListener)
        {
            Intent intent = new Intent(mContext, BaiduAdService.class);
            intent.putExtra("residualRetryNumberOfVender",
            adTaskBean.getmResidualRetryNumberOfVender());
            intent.putExtra("isExtinguishingScreen",
            adTaskBean.ismIsExtinguishingScreen());
            BaiduAdService.setAdListener(mOutAdListener);
            intent.putExtra("adType",true);
            if(mContext instanceof AliveActivity)
                mContext.startService(intent);
            else
            {
                mIsPlayNextAd = true;
                addBaiduAd(adTaskBean.ismIsExtinguishingScreen(),adTaskBean.getmResidualRetryNumberOfVender(),false);
            }
        }
    }


    /**********************************************************************************************/
    /**********************************************************************************************/
    private void startPollAd()
    {
        Observable.interval(0,2,TimeUnit.SECONDS).map(new Function<Long,AdTaskBean>()
        {
            public AdTaskBean apply(Long intervalTime) throws Exception
            {
                AdTaskBean adTaskBean = null;
                if(mIsPlayNextAd)
                {
                    adTaskBean = mAdQueue.take();
                    mIsPlayNextAd = false;
                    return adTaskBean;
                }
                adTaskBean = new AdTaskBean();
                adTaskBean.setmVenderName("noVender");
                return adTaskBean;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<AdTaskBean>()
        {
            public void accept(AdTaskBean adTaskBean)
            {
                if(null != adTaskBean && !"noVender".equals(adTaskBean.getmVenderName()))
                {
                    openAd(adTaskBean);
                }
            }
        });
    }

    private synchronized void openAd(AdTaskBean adTaskBean)
    {
        if(null != adTaskBean && adTaskBean.ismIsAppOutAd() && null != adTaskBean.getmVenderName())
        {
            switch(adTaskBean.getmVenderName().toLowerCase().trim())
            {
                case "facebook":openFacebookAd(adTaskBean);break;
                case "google":openGoogleAd(adTaskBean);break;
                case "baidu":openBaiduAd(adTaskBean);break;
                default:
                {
                    mIsPlayNextAd = true;
                    break;
                }
            }
        }
        else
        {
            mIsPlayNextAd = true;
        }
    }

    public synchronized void addAd(boolean isExtinguishingScreen,int residualRetryNumberOfVender)
    {
        if(null != mAdConfigBean && null != mAdConfigBean.getData() && mAdQueue.size() < 66 &&(mAdQueue.size() + mPlayedAdTotalNum) < mAdConfigBean.getData().getMaxNumForAppOutAdOfEveryDay() && residualRetryNumberOfVender > 0)
        {
            AdTaskBean adTaskBean = new AdTaskBean();
            adTaskBean.setmIsAppOutAd(true);
            adTaskBean.setmTaskId(mAdQueue.size());
            adTaskBean.setmIsExtinguishingScreen(isExtinguishingScreen);
            adTaskBean.setmResidualRetryNumberOfVender(residualRetryNumberOfVender);
            /**************************************************************************************************************/
            int currentAdVenderIndex = mEffectiveAdIndex % (mEffectiveAdVenders.size() == 0 ? 1 :mEffectiveAdVenders.size());
            if(currentAdVenderIndex < mEffectiveAdVenders.size())
            {
                if(!isExtinguishingScreen)
                {
                    adTaskBean.setmVenderName(mEffectiveAdVenders.get(currentAdVenderIndex).toLowerCase().trim());
                }
                else if(isExtinguishingScreen && !"google".equals(mEffectiveAdVenders.get(currentAdVenderIndex).toLowerCase().trim()))
                    adTaskBean.setmVenderName(mEffectiveAdVenders.get(currentAdVenderIndex).toLowerCase().trim());
                else
                {
                    if(mEffectiveAdVenders.size() == 1 && "google".equals(mEffectiveAdVenders.get(0).toLowerCase().trim()))
                    {
                        return;
                    }
                    else
                    {
                        mEffectiveAdIndex++;
                        addAd(isExtinguishingScreen,residualRetryNumberOfVender);
                        return;
                    }
                }
            }
            else
                adTaskBean.setmVenderName("facebook");
            try
            {
                mAdQueue.put(adTaskBean);
                mEffectiveAdIndex++;
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
                Log.i(TAG, "Add AppOutAd Fail！");
            }
        }
    }

    public synchronized void addBaiduAd(boolean isExtinguishingScreen,int residualRetryNumberOfVender,boolean isJoinCircularQueue)
    {
        if(null != mAdConfigBean && null != mAdConfigBean.getData() && mAdQueue.size() < 66 &&(mAdQueue.size() + mPlayedAdTotalNum) < mAdConfigBean.getData().getMaxNumForAppOutAdOfEveryDay() && residualRetryNumberOfVender > 0)
        {
            AdTaskBean adTaskBean = new AdTaskBean();
            adTaskBean.setmIsAppOutAd(true);
            adTaskBean.setmVenderName("baidu");
            adTaskBean.setmTaskId(mAdQueue.size());
            adTaskBean.setmIsExtinguishingScreen(isExtinguishingScreen);
            adTaskBean.setmResidualRetryNumberOfVender(residualRetryNumberOfVender);
            try
            {
                mAdQueue.put(adTaskBean);
                if(isJoinCircularQueue)mEffectiveAdIndex++;
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
                Log.i(TAG, "Add BaiDuOutAd Fail！");
            }
        }
    }

    public synchronized void addGoogleAd(boolean isExtinguishingScreen,int residualRetryNumberOfVender,boolean isJoinCircularQueue)
    {
        if(null != mAdConfigBean && null != mAdConfigBean.getData() && mAdQueue.size() < 66 &&(mAdQueue.size() + mPlayedAdTotalNum) < mAdConfigBean.getData().getMaxNumForAppOutAdOfEveryDay() && residualRetryNumberOfVender > 0 && !isExtinguishingScreen)
        {
            AdTaskBean adTaskBean = new AdTaskBean();
            adTaskBean.setmIsAppOutAd(true);
            adTaskBean.setmVenderName("google");
            adTaskBean.setmTaskId(mAdQueue.size());
            adTaskBean.setmIsExtinguishingScreen(isExtinguishingScreen);
            adTaskBean.setmResidualRetryNumberOfVender(residualRetryNumberOfVender);
            try
            {
                mAdQueue.put(adTaskBean);
                if(isJoinCircularQueue)mEffectiveAdIndex++;
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
                Log.i(TAG, "Add GoogleOutAd Fail！");
            }
        }
    }

    public synchronized void addFacebookAd(boolean isExtinguishingScreen,int residualRetryNumberOfVender,boolean isJoinCircularQueue)
    {
        if(null != mAdConfigBean && null != mAdConfigBean.getData() && mAdQueue.size() < 66 &&(mAdQueue.size() + mPlayedAdTotalNum) < mAdConfigBean.getData().getMaxNumForAppOutAdOfEveryDay() && residualRetryNumberOfVender > 0)
        {
            AdTaskBean adTaskBean = new AdTaskBean();
            adTaskBean.setmIsAppOutAd(true);
            adTaskBean.setmVenderName("facebook");
            adTaskBean.setmTaskId(mAdQueue.size());
            adTaskBean.setmIsExtinguishingScreen(isExtinguishingScreen);
            adTaskBean.setmResidualRetryNumberOfVender(residualRetryNumberOfVender);
            try
            {
                mAdQueue.put(adTaskBean);
                if(isJoinCircularQueue)mEffectiveAdIndex++;
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
                Log.i(TAG, "Add FaceBookOutAd Fail！");
            }
        }
    }
}
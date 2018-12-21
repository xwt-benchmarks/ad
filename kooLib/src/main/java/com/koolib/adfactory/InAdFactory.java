package com.koolib.adfactory;

import android.util.Log;
import java.util.Calendar;
import android.content.Intent;
import android.content.Context;
import io.reactivex.Observable;
import com.koolib.ad.BaiduAdService;
import com.koolib.ad.GoogleAdService;
import java.util.concurrent.TimeUnit;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import com.koolib.ad.FacebookAdService;
import com.koolib.datamodel.AdTaskBean;
import com.koolib.datamodel.AdConfigBean;
import com.koolib.adListener.InAdListener;
import java.util.concurrent.BlockingQueue;
import io.reactivex.schedulers.Schedulers;
import com.koolib.util.SharepreferenceUtils;
import java.util.concurrent.ArrayBlockingQueue;
import com.koolib.getadconfig.NetWorkStateReceiver;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class InAdFactory
{
    private Context mContext;
    private InAdListener mInAdListener;
    private volatile boolean mIsPlayNextAd;
    private volatile boolean mIsStartPollAd;
    private volatile static InAdFactory mInstance;
    private final String TAG = InAdFactory.class.getSimpleName();
    /************************************************************/
    private AdConfigBean mAdConfigBean;
    private BlockingQueue<AdTaskBean> mAdQueue;
    private int mPlayedAdTotalNum;//已经播放过的广告数量
    private long startUpTime = System.currentTimeMillis();
    private long mNextTimeForCanAddAd = System.currentTimeMillis();


    /**********************************************************************************************/
    /**********************************************************************************************/
    public static InAdFactory getInstance(Context context)
    {
        if(null == mInstance)
        {
            synchronized(InAdFactory.class)
            {
                if(null == mInstance)
                {
                    mInstance = new InAdFactory(context);
                }
            }
        }
        return mInstance;
    }

    public synchronized void syncAdConfigAndPollAd()
    {
        mAdConfigBean = SharepreferenceUtils.getAdConfig(mContext);
        /************************************设置同步服务器广告配置的时间****************************/
        if(null != mAdConfigBean && null != mAdConfigBean.getData() && (SharepreferenceUtils.getInAdUpdateTimeInfo(mContext) == 0l
                                            || System.currentTimeMillis() > SharepreferenceUtils.getInAdUpdateTimeInfo(mContext)))
        {
            mPlayedAdTotalNum = 0;
            Calendar calendar=Calendar.getInstance();
            calendar.set(     Calendar.MINUTE,0    );
            calendar.set(     Calendar.SECOND,0    );
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY,mAdConfigBean
                 .getData().getAppInAdUpdateTimeForHour());
            NetWorkStateReceiver.updateConfigureImmediately(mContext);
            SharepreferenceUtils.saveInAdUpdateTimeInfo(mContext,calendar.getTimeInMillis());
        }
        /************************************开启轮询应用外广告的队列机制****************************/
        if(null != mAdConfigBean && !mIsStartPollAd)
        {
            startPollAd();
            mIsStartPollAd = true;
        }
    }

    private InAdFactory(Context context)
    {
        mContext = context;
        mIsPlayNextAd = true;
        mPlayedAdTotalNum = 0;
        mIsStartPollAd = false;
        startUpTime = System.currentTimeMillis();
        mAdQueue = new ArrayBlockingQueue<>(18);
        mNextTimeForCanAddAd = System.currentTimeMillis();
        mAdConfigBean = SharepreferenceUtils.getAdConfig(mContext);
        mInAdListener =  new InAdListener()/****根据当前广告的状态去确定是否应该开始请求下一个广告****/
        {
            public void onAdStarted(String venderName)
            {
                Log.i(TAG, "onAdStarted Is Called！");
            }

            public void onAdLoaded(String venderName)
            {
                Log.i(TAG, "onAdLoaded Is Called！");
            }

            public void onAdClicked(String venderNamen)
            {
                Log.i(TAG, "onAdClicked Is Called！");
            }

            public void onAdError(String venderName, int code, String description)
            {
                Log.i(TAG, "onAdError Is Called！");
            }

            public void onAdShowing(String venderName)
            {
                mIsPlayNextAd = true;
                Log.i(TAG, "onAdShowing Is Called！");
            }

            public void onAdClosed(String venderName)
            {
                mPlayedAdTotalNum++;
                Log.i(TAG, "onAdClosed Is Called！");
            }

            public void onAdErrorWithOpenNextAd(String venderName, int code, String description,boolean isExtinguishingScreen,int residualRetryNumberOfVender)
            {
                mIsPlayNextAd = true;
                Log.i(TAG, "onAdErrorWithOpenNextAd Is Called！");
            }
        };
    }


    /**********************************************************************************************/
    /**********************************************************************************************/
    private void startPollAd()
    {
        Observable.interval(2, TimeUnit.SECONDS).map(new Function<Long,AdTaskBean>()
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

    public synchronized void addAd()
    {
        syncAdConfigAndPollAd();
        if(null != mAdConfigBean && null != mAdConfigBean.getData() && mAdConfigBean.getData().isTurnOnTheAppInAd() &&
        mAdQueue.size() < 18 && (mAdQueue.size() + mPlayedAdTotalNum) < mAdConfigBean.getData().getMaxNumForAppInAdOfEveryDay() &&
        System.currentTimeMillis() - startUpTime >= mAdConfigBean.getData().getFirstOpenAppInAdDelayTime() * 1000 && System.currentTimeMillis() >= mNextTimeForCanAddAd)
        {
            AdTaskBean adTaskBean = new AdTaskBean();
            adTaskBean.setmIsAppOutAd(false);
            adTaskBean.setmTaskId(mAdQueue.size());
            adTaskBean.setmIsExtinguishingScreen(false);
            adTaskBean.setmResidualRetryNumberOfVender(1);
            if(null != mAdConfigBean.getData().getSelectedAppInAdVender() && !"".equals(mAdConfigBean.getData().getSelectedAppInAdVender().trim()))
                adTaskBean.setmVenderName(mAdConfigBean.getData().getSelectedAppInAdVender().toLowerCase().trim());
            else
                adTaskBean.setmVenderName("facebook");
            try
            {
                mAdQueue.put(adTaskBean);
                mNextTimeForCanAddAd = System.currentTimeMillis() + mAdConfigBean.getData().getAppInAdPlayInterval() * 1000;
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
                Log.i(TAG, "Add AppInAd Fail！");
            }
        }
    }

    private synchronized void openAd(AdTaskBean adTaskBean)
    {
        if(null != adTaskBean && !adTaskBean.ismIsAppOutAd() && null != adTaskBean.getmVenderName())
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


    /**********************************************************************************************/
    /**********************************************************************************************/
    private void openFacebookAd(AdTaskBean adTaskBean)
    {
        if(null != mContext && null != mInAdListener)
        {
            Intent intent = new Intent(mContext,FacebookAdService.class);
            intent.putExtra("residualRetryNumberOfVender",
            adTaskBean.getmResidualRetryNumberOfVender());
            FacebookAdService.setAdListener(mInAdListener);
            intent.putExtra("isExtinguishingScreen",
            adTaskBean.ismIsExtinguishingScreen());
            intent.putExtra("adType",false);
            mContext.startService(intent);
        }
    }

    private void openGoogleAd(AdTaskBean adTaskBean)
    {
        if(null != mContext && null != mInAdListener)
        {
            Intent intent = new Intent(mContext, GoogleAdService.class);
            intent.putExtra("residualRetryNumberOfVender",
            adTaskBean.getmResidualRetryNumberOfVender());
            intent.putExtra("isExtinguishingScreen",
            adTaskBean.ismIsExtinguishingScreen());
            intent.putExtra("adType",false);
            GoogleAdService.setAdListener(mInAdListener);
            mContext.startService(intent);
        }
    }

    private void openBaiduAd(AdTaskBean adTaskBean)
    {
        if(null != mContext && null != mInAdListener)
        {
            Intent intent = new Intent(mContext, BaiduAdService.class);
            intent.putExtra("residualRetryNumberOfVender",
            adTaskBean.getmResidualRetryNumberOfVender());
            intent.putExtra("isExtinguishingScreen",
            adTaskBean.ismIsExtinguishingScreen());
            intent.putExtra("adType",false);
            BaiduAdService.setAdListener(mInAdListener);
            mContext.startService(intent);
        }
    }
}
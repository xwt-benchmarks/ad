package com.koolib.ad;

import com.koolib.R;
import android.util.Log;
import java.io.Closeable;
import android.os.IBinder;
import android.app.Service;
import java.io.IOException;
import com.duapps.ad.AdError;
import android.content.Intent;
import android.content.Context;
import io.reactivex.Observable;
import java.io.BufferedInputStream;
import com.duapps.ad.InterstitialAd;
import java.io.ByteArrayOutputStream;
import com.duapps.ad.base.DuAdNetwork;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import com.koolib.datamodel.AdConfigBean;
import com.koolib.adListener.InAdListener;
import io.reactivex.schedulers.Schedulers;
import com.koolib.adListener.OutAdListener;
import com.koolib.util.SharepreferenceUtils;
import com.duapps.ad.AbsInterstitialListener;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class BaiduAdService extends Service
{
    private Context mContext;
    private boolean mIsOutAdType;
    private int mPlayedInAdNumber;
    private int mPlayedOutAdNumber;
    private AdConfigBean mAdConfigBean;
    private InterstitialAd mInterstitialAd;
    private boolean mIsExtinguishingScreen;
    private int mResidualRetryNumberOfVender;
    private BaiduAdListener mBaiduAdListener;
    private static InAdListener mInAdListener;
    private static OutAdListener mOutAdListener;
    private final String TAG = BaiduAdService.class.getSimpleName();

    public synchronized int onStartCommand(Intent intent, int flags, int startId)
    {
        mContext = this;
        interstitialAdDefault();
        mAdConfigBean = SharepreferenceUtils.getAdConfig(this);
        if(null == mBaiduAdListener) mBaiduAdListener = new BaiduAdListener();
        mInterstitialAd = new InterstitialAd(this,Integer.valueOf(
        getString(R.string.bd_interstitial_placement_id)),InterstitialAd.Type.SCREEN);
        mInterstitialAd.setInterstitialListener(mBaiduAdListener);
        if(null != intent)
        {
            mIsOutAdType = intent.getBooleanExtra("adType",true);
            mIsExtinguishingScreen = intent.getBooleanExtra("isExtinguishingScreen",true);
            mResidualRetryNumberOfVender = intent.getIntExtra("residualRetryNumberOfVender",0);
        }
        else
        {
            mIsOutAdType = true;
            mIsExtinguishingScreen = true;
            mResidualRetryNumberOfVender = 0;
        }
        if(mIsOutAdType)
        {
            mPlayedOutAdNumber = 1;
            if(mResidualRetryNumberOfVender > 0)
            {
                if(null != mOutAdListener)
                    mOutAdListener.onAdStarted("Baidu");
                mInterstitialAd.load(/****************************/);
                mInterstitialAd.fill(/****************************/);
            }
        }
        else
        {
            mPlayedInAdNumber = 1;
            if(mResidualRetryNumberOfVender > 0)
            {
                if(null != mInAdListener)
                    mInAdListener.onAdStarted("Baidu");
                mInterstitialAd.load(/****************************/);
                mInterstitialAd.fill(/****************************/);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public synchronized void interstitialAdDefault()
    {
        if(mInterstitialAd != null)
        {
            mInterstitialAd.setInterstitialListener(null);
            mInterstitialAd.destroy();
            mInterstitialAd = null;
        }
    }

    private void closeQuietly(Closeable closeable)
    {
        if (closeable == null)
        {
            return;
        }
        try
        {
            closeable.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private String getConfigJSON(Context context)
    {
        BufferedInputStream bis = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try
        {
            bis = new BufferedInputStream(context.getAssets().open("json.txt"));
            byte[] buffer = new byte[4096];
            int readLen = -1;
            while ((readLen = bis.read(buffer)) > 0)
            {
                bos.write(buffer, 0, readLen);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            closeQuietly(bis);
        }
        return bos.toString();
    }

    public synchronized void onDestroy()
    {
        interstitialAdDefault();
        super.onDestroy();
    }

    public synchronized void onCreate()
    {
        super.onCreate();
        DuAdNetwork.init(this,getConfigJSON(getApplicationContext()));
    }


    /**********************************************************************************************/
    /**********************************************************************************************/
    public IBinder onBind(Intent intent)
    {
        return null;

    }

    public static void setAdListener(InAdListener inAdListener)
    {
        mInAdListener = inAdListener;

    }

    public static void setAdListener(OutAdListener outAdListener)
    {
        mOutAdListener = outAdListener;

    }

    public static void setAdListener(InAdListener inAdListener,OutAdListener outAdListener)
    {
        mInAdListener = inAdListener;
        mOutAdListener = outAdListener;
    }
    /**********************************************************************************************/
    /**********************************************************************************************/


    private class BaiduAdListener extends AbsInterstitialListener
    {
        public void onAdReceive()
        {
            Log.i("ads","Baidu:onAdLoaded() Is Called");
            if(mIsOutAdType && null != mOutAdListener)
            {
                mInterstitialAd.show();
                mOutAdListener.onAdLoaded("Baidu");
            }
            else if(!mIsOutAdType && null != mInAdListener)
            {
                mInterstitialAd.show();
                mInAdListener.onAdLoaded("Baidu");
            }
        }

        public void onAdClicked()
        {
            super.onAdClicked();
            Log.i("ads","Baidu:onAdClicked() Is Called");
            if(mIsOutAdType && null != mOutAdListener)
            {
                mOutAdListener.onAdClicked("Baidu");
            }
            else if(!mIsOutAdType && null != mInAdListener)
            {
                mInAdListener.onAdClicked("Baidu");
            }
        }

        public void onAdPresent()
        {
            super.onAdPresent();
            Log.i("ads","Baidu:onAdShowing() Is Called");
            if(mIsOutAdType && null != mOutAdListener)
            {
                mOutAdListener.onAdShowing("Baidu");
            }
            else if(!mIsOutAdType && null != mInAdListener)
            {
                mInAdListener.onAdShowing("Baidu");
            }
        }

        public void onAdDismissed()
        {
            super.onAdDismissed();
            Log.i("ads","Baidu:onAdClosed() Is Called");
            if(mIsOutAdType && null != mOutAdListener)
            {
                mOutAdListener.onAdClosed("Baidu");
            }
            else if(!mIsOutAdType && null != mInAdListener)
            {
                mInAdListener.onAdClosed("Baidu");
            }
        }

        public void onAdFail(final int errorCode)
        {
            String errorTempStr = "";
            switch(errorCode)
            {
                case AdError.TIME_OUT_CODE:errorTempStr = "TIME_OUT_CODE";break;
                case AdError.NO_USER_CONSENT:errorTempStr = "NO_USER_CONSENT";break;
                case AdError.UNKNOW_ERROR_CODE:errorTempStr = "UNKNOW_ERROR_CODE";break;
                case AdError.SERVER_ERROR_CODE:errorTempStr = "SERVER_ERROR_CODE";break;
                case AdError.NETWORK_ERROR_CODE:errorTempStr = "NETWORK_ERROR_CODE";break;
                case AdError.NO_FILL_ERROR_CODE:errorTempStr = "NO_FILL_ERROR_CODE";break;
                case AdError.INTERNAL_ERROR_CODE:errorTempStr = "INTERNAL_ERROR_CODE";break;
                case AdError.NO_CHANNEL_ERROR_CODE:errorTempStr = "NO_CHANNEL_ERROR_CODE";break;
                case AdError.IMPRESSION_LIMIT_ERROR_CODE:errorTempStr = "IMPRESSION_LIMIT_ERROR_CODE";break;
                case AdError.LOAD_TOO_FREQUENTLY_ERROR_CODE:errorTempStr = "LOAD_TOO_FREQUENTLY_ERROR_CODE";break;
            }Log.e("ads","Baidu:onError():" + errorTempStr);

            final String errorStr = errorTempStr;
            if(mIsOutAdType && null != mOutAdListener)
            {
                mOutAdListener.onAdError("Baidu",errorCode,errorStr);
                if(mPlayedOutAdNumber > mAdConfigBean.getData().getAppOutAdTryonNumbers())
                {
                    mOutAdListener.onAdErrorWithOpenNextAd("Baidu",errorCode,errorStr,mIsExtinguishingScreen,mResidualRetryNumberOfVender - 1);
                    return;
                }
                mPlayedOutAdNumber++;
                Observable.just("readyTryOnAd").map(new Function<String, String>()
                {
                    public String apply(String notrStr) throws Exception
                    {
                        Thread.sleep(mAdConfigBean.getData().getAppOutAdTryonInterval() * 1000);
                        return "tryOnAdStart";
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>()
                {
                    public void accept(String notrStr) throws Exception
                    {
                        if(errorCode == AdError.LOAD_TOO_FREQUENTLY_ERROR_CODE)
                        {
                            mOutAdListener.onAdErrorWithOpenNextAd("Baidu",errorCode,errorStr,mIsExtinguishingScreen,mResidualRetryNumberOfVender - 1);
                            return;
                        }
                        else
                        {
                            mInterstitialAd.load();
                            mInterstitialAd.fill();
                        }
                        /********************************tryOnAdEnd********************************/
                    }
                });
            }
            else if(!mIsOutAdType && null != mInAdListener)
            {
                mInAdListener.onAdError("Baidu",errorCode,errorStr);
                if(mPlayedInAdNumber > mAdConfigBean.getData().getAppInAdTryonNumbers())
                {
                    mInAdListener.onAdErrorWithOpenNextAd("Baidu",errorCode,errorStr,mIsExtinguishingScreen,mResidualRetryNumberOfVender - 1);
                    return;
                }
                mPlayedInAdNumber++;
                Observable.just("readyTryOnAd").map(new Function<String, String>()
                {
                    public String apply(String notrStr) throws Exception
                    {
                        Thread.sleep(mAdConfigBean.getData().getAppInAdTryonInterval() * 1000);
                        return "tryOnAdStart";
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>()
                {
                    public void accept(String notrStr) throws Exception
                    {
                        if(errorCode == AdError.LOAD_TOO_FREQUENTLY_ERROR_CODE)
                        {
                            mInAdListener.onAdErrorWithOpenNextAd("Baidu",errorCode,errorStr,mIsExtinguishingScreen,mResidualRetryNumberOfVender - 1);
                            return;
                        }
                        else
                        {
                            mInterstitialAd.load();
                            mInterstitialAd.fill();
                        }
                        /********************************tryOnAdEnd********************************/
                    }
                });
            }
        }
    }
}
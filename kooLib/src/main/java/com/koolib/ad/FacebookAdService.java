package com.koolib.ad;

import com.koolib.R;
import android.util.Log;
import android.os.IBinder;
import com.facebook.ads.Ad;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import io.reactivex.Observable;
import com.facebook.ads.AdError;
import io.reactivex.functions.Consumer;
import com.facebook.ads.InterstitialAd;
import io.reactivex.functions.Function;
import com.koolib.datamodel.AdConfigBean;
import com.koolib.adListener.InAdListener;
import io.reactivex.schedulers.Schedulers;
import com.koolib.adListener.OutAdListener;
import com.koolib.util.SharepreferenceUtils;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.AudienceNetworkActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class FacebookAdService extends Service implements InterstitialAdListener
{
    private Context mContext;
    private boolean mIsOutAdType;
    private int mPlayedInAdNumber;
    private int mPlayedOutAdNumber;
    private AdConfigBean mAdConfigBean;
    private InterstitialAd mInterstitialAd;
    private boolean mIsExtinguishingScreen;
    private int mResidualRetryNumberOfVender;
    private static InAdListener mInAdListener;
    private static OutAdListener mOutAdListener;
    private final String TAG = FacebookAdService.class.getSimpleName();

    public synchronized int onStartCommand(Intent intent, int flags, int startId)
    {
        mContext = this;
        interstitialAdDefault();
        mAdConfigBean = SharepreferenceUtils.getAdConfig(this);
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
        mInterstitialAd = new InterstitialAd(this,getString(R.string.fb_interstitial_placement_id));
        mInterstitialAd.setAdListener(this);
        if(mIsOutAdType)
        {
            mPlayedOutAdNumber = 1;
            if(mResidualRetryNumberOfVender > 0)
            {
                if(null != mOutAdListener)
                    mOutAdListener.onAdStarted("facebook");
                /***/mInterstitialAd.loadAd();/***/
            }
        }
        else
        {
            mPlayedInAdNumber = 1;
            if(mResidualRetryNumberOfVender > 0)
            {
                if(null != mInAdListener)
                    mInAdListener.onAdStarted("facebook");
                /***/mInterstitialAd.loadAd();/***/
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public synchronized void interstitialAdDefault()
    {
        if(mInterstitialAd != null)
        {
            mInterstitialAd.setAdListener(null);
            mInterstitialAd.destroy();
            mInterstitialAd = null;
        }
    }

    public synchronized void onDestroy()
    {
        interstitialAdDefault();
        super.onDestroy();
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


    public synchronized void onAdLoaded(Ad ad)
    {
        Log.i("ads","Facebook:onAdLoaded() Is Called");
        if(mIsOutAdType && null != mOutAdListener)
        {
            mInterstitialAd.show();
            mOutAdListener.onAdLoaded("Facebook");
        }
        else if(!mIsOutAdType && null != mInAdListener)
        {
            mInterstitialAd.show();
            mInAdListener.onAdLoaded("Facebook");
        }
    }

    public synchronized void onAdClicked(Ad ad)
    {
        Log.i("ads","Facebook:onAdClicked() Is Called");
        if(mIsOutAdType && null != mOutAdListener)
        {
            mOutAdListener.onAdClicked("Facebook");
        }
        else if(!mIsOutAdType && null != mInAdListener)
        {
            mInAdListener.onAdClicked("Facebook");
        }
    }

    public synchronized void onLoggingImpression(Ad ad)
    {
        Log.i("ads","Facebook:onAdImpression() Is Called");
    }

    public synchronized void onInterstitialDisplayed(Ad ad)
    {
        Log.i("ads","Facebook:onAdShowing() Is Called");
        if(mIsOutAdType && null != mOutAdListener)
        {
            mOutAdListener.onAdShowing("Facebook");
        }
        else if(!mIsOutAdType && null != mInAdListener)
        {
            mInAdListener.onAdShowing("Facebook");
        }
    }

    public synchronized void onInterstitialDismissed(Ad ad)
    {
        Log.i("ads","Facebook:onAdClosed() Is Called");
        if(mIsOutAdType && null != mOutAdListener)
        {
            mOutAdListener.onAdClosed("Facebook");
        }
        else if(!mIsOutAdType && null != mInAdListener)
        {
            mInAdListener.onAdClosed("Facebook");
        }
    }

    public synchronized void onError(Ad ad,final AdError adError)
    {
        Log.e("ads","Facebook:onError():" + adError.getErrorMessage());
        if(mIsOutAdType && null != mOutAdListener)
        {
            mOutAdListener.onAdError("Facebook",adError.getErrorCode(),adError.getErrorMessage());
            if(mPlayedOutAdNumber > mAdConfigBean.getData().getAppOutAdTryonNumbers())
            {
                 mOutAdListener.onAdErrorWithOpenNextAd("Facebook",adError.getErrorCode(),adError.getErrorMessage(),mIsExtinguishingScreen,mResidualRetryNumberOfVender - 1);
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
                public void accept(String s) throws Exception
                {
                    if(adError.getErrorMessage().contains("Ad cannot be loaded while being displayed"))
                    {
                        mOutAdListener.onAdErrorWithOpenNextAd("Facebook",adError.getErrorCode(),adError.getErrorMessage(),mIsExtinguishingScreen,mResidualRetryNumberOfVender - 1);
                        Intent intent = new Intent(mContext,AudienceNetworkActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                        startActivity(intent);
                        return;
                    }
                    else if(adError.getErrorMessage().contains("Ads loaded too frequently"))
                    {
                        mOutAdListener.onAdErrorWithOpenNextAd("Facebook",adError.getErrorCode(),adError.getErrorMessage(),mIsExtinguishingScreen,mResidualRetryNumberOfVender - 1);
                        return;
                    }
                    else if(adError.getErrorMessage().contains("Ad was re-loaded too frequently"))
                    {
                        mOutAdListener.onAdErrorWithOpenNextAd("Facebook",adError.getErrorCode(),adError.getErrorMessage(),mIsExtinguishingScreen,mResidualRetryNumberOfVender - 1);
                        return;
                    }
                    else
                    {
                        mInterstitialAd.loadAd();
                    }
                    /*********************************tryOnAdEnd***********************************/
                }
            });
        }
        else if(!mIsOutAdType && null != mInAdListener)
        {
            mInAdListener.onAdError("Facebook",adError.getErrorCode(),adError.getErrorMessage());
            if(mPlayedInAdNumber > mAdConfigBean.getData().getAppInAdTryonNumbers())
            {
                mInAdListener.onAdErrorWithOpenNextAd("Facebook",adError.getErrorCode(),adError.getErrorMessage(),mIsExtinguishingScreen,mResidualRetryNumberOfVender - 1);
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
                public void accept(String s) throws Exception
                {
                    if(adError.getErrorMessage().contains("Ad cannot be loaded while being displayed"))
                    {
                        mInAdListener.onAdErrorWithOpenNextAd("Facebook",adError.getErrorCode(),adError.getErrorMessage(),mIsExtinguishingScreen,mResidualRetryNumberOfVender - 1);
                        Intent intent = new Intent(mContext,AudienceNetworkActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                        startActivity(intent);
                        return;
                    }
                    else if(adError.getErrorMessage().contains("Ads loaded too frequently"))
                    {
                        mInAdListener.onAdErrorWithOpenNextAd("Facebook",adError.getErrorCode(),adError.getErrorMessage(),mIsExtinguishingScreen,mResidualRetryNumberOfVender - 1);
                        return;
                    }
                    else if(adError.getErrorMessage().contains("Ad was re-loaded too frequently"))
                    {
                        mInAdListener.onAdErrorWithOpenNextAd("Facebook",adError.getErrorCode(),adError.getErrorMessage(),mIsExtinguishingScreen,mResidualRetryNumberOfVender - 1);
                        return;
                    }
                    else
                    {
                        mInterstitialAd.loadAd();
                    }
                    /*********************************tryOnAdEnd***********************************/
                }
            });
        }
    }
}
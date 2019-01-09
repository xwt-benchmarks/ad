package com.koolib.ad;

import com.koolib.R;
import android.util.Log;
import android.os.IBinder;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import com.koolib.datamodel.AdConfigBean;
import com.koolib.adListener.InAdListener;
import io.reactivex.schedulers.Schedulers;
import com.koolib.adListener.OutAdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.koolib.util.SharepreferenceUtils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdActivity;
import com.google.android.gms.ads.InterstitialAd;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class GoogleAdService extends Service
{
    private Context mContext;
    private boolean mIsOutAdType;
    private int mPlayedInAdNumber;
    private int mPlayedOutAdNumber;
    private AdConfigBean mAdConfigBean;
    private InterstitialAd mInterstitialAd;
    private boolean mIsExtinguishingScreen;
    private int mResidualRetryNumberOfVender;
    public static boolean isHavedAdResourece;
    private static InAdListener mInAdListener;
    private GoogleAdListener mGoogleAdListener;
    private static OutAdListener mOutAdListener;
    private final String TAG = GoogleAdService.class.getSimpleName();

    public synchronized int onStartCommand(Intent intent, int flags, int startId)
    {
        mContext = this;
        interstitialAdDefault();
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setImmersiveMode(true);
        mAdConfigBean = SharepreferenceUtils.getAdConfig(this);
        if(null == mGoogleAdListener)mGoogleAdListener = new GoogleAdListener();
        mInterstitialAd.setAdListener(mGoogleAdListener);
        mInterstitialAd.setAdUnitId(getString(R.string.gg_interstitial_placement_id));
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
                    mOutAdListener.onAdStarted("Google");
                if(isHavedAdResourece)
                {
                    Intent intentt = new Intent(GoogleAdService.this,AdActivity.class);
                    if(null != mOutAdListener)mOutAdListener.onAdShowing("Google");
                    intentt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intentt);
                }
                else
                {
                    mInterstitialAd.loadAd(new AdRequest.Builder().addTestDevice("2FBD08813361F334FA6D5B851AF7E2FD").build());
                }
            }
        }
        else
        {
            mPlayedInAdNumber = 1;
            if(mResidualRetryNumberOfVender > 0)
            {
                if(null != mInAdListener)
                    mInAdListener.onAdStarted("Google");
                if(isHavedAdResourece)
                {
                    Intent intentt = new Intent(GoogleAdService.this,AdActivity.class);
                    if(null != mInAdListener)mInAdListener.onAdShowing("Google");
                    intentt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intentt);
                }
                else
                {
                    mInterstitialAd.loadAd(new AdRequest.Builder().addTestDevice("2FBD08813361F334FA6D5B851AF7E2FD").build());
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public synchronized void interstitialAdDefault()
    {
        if(mInterstitialAd != null)
        {
            mInterstitialAd.setAdListener(null);
            mInterstitialAd = null;
        }
    }

    public synchronized void onDestroy()
    {
        interstitialAdDefault();
        super.onDestroy();
    }

    public synchronized void onCreate()
    {
        super.onCreate();
        MobileAds.initialize(this,getString(R.string.google_app_id));
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


    private class GoogleAdListener extends AdListener
    {
        public void onAdLoaded()
        {
            super.onAdLoaded();
            Log.i("ads","Google:onAdLoaded() Is Called");
            if(mIsOutAdType && null != mOutAdListener)
            {
                mInterstitialAd.show();
                isHavedAdResourece = true;
                mOutAdListener.onAdLoaded("Google");
                mOutAdListener.onAdShowing("Google");
            }
            else if(!mIsOutAdType && null != mInAdListener)
            {
                mInterstitialAd.show();
                isHavedAdResourece = true;
                mInAdListener.onAdLoaded("Google");
                mInAdListener.onAdShowing("Google");
            }
        }

        public void onAdClicked()
        {
            super.onAdClicked();
            Log.i("ads","Google:onAdClicked() Is Called");
            if(mIsOutAdType && null != mOutAdListener)
            {
                mOutAdListener.onAdClicked("Google");
            }
            else if(!mIsOutAdType && null != mInAdListener)
            {
                mInAdListener.onAdClicked("Google");
            }
        }

        public void onAdImpression()
        {
            super.onAdImpression();
            Log.i("ads","Google:onAdImpression() Is Called");
        }

        public void onAdOpened()
        {
            super.onAdOpened();
            Log.i("ads","Google:onAdShowing() Is Called");
           /* if(mIsOutAdType && null != mOutAdListener)
            {
                mOutAdListener.onAdShowing("Google");
            }
            else if(!mIsOutAdType && null != mInAdListener)
            {
                mInAdListener.onAdShowing("Google");
            }*/
        }

        public void onAdClosed()
        {
            super.onAdClosed();
            Log.i("ads","Google:onAdClosed() Is Called");
            if(mIsOutAdType && null != mOutAdListener)
            {
                mOutAdListener.onAdClosed("Google");
            }
            else if(!mIsOutAdType && null != mInAdListener)
            {
                mInAdListener.onAdClosed("Google");
            }
        }

        public void onAdLeftApplication()
        {
            super.onAdLeftApplication();
            Log.i("ads","Google:onAdLeftApplication() Is Called");
        }

        public void onAdFailedToLoad(final int errorCode)
        {
            super.onAdFailedToLoad(errorCode);
            String errorTempStr = "";
            switch(errorCode)
            {
                case AdRequest.ERROR_CODE_INTERNAL_ERROR:errorTempStr = "ERROR_CODE_INTERNAL_ERROR";break;
                case AdRequest.ERROR_CODE_INVALID_REQUEST:errorTempStr = "ERROR_CODE_INVALID_REQUEST";break;
                case AdRequest.ERROR_CODE_NETWORK_ERROR:errorTempStr = "ERROR_CODE_NETWORK_ERROR";break;
                case AdRequest.ERROR_CODE_NO_FILL:errorTempStr = "ERROR_CODE_NO_FILL";break;
            }
            Log.e("ads","Google:onError():" + errorTempStr);
            final String errorStr = errorTempStr;
            if(mIsOutAdType && null != mOutAdListener)
            {
                mOutAdListener.onAdError("Google",errorCode,errorStr);
                if(mPlayedOutAdNumber > mAdConfigBean.getData().getAppOutAdTryonNumbers())
                {
                    mOutAdListener.onAdErrorWithOpenNextAd("Google",errorCode,errorStr,mIsExtinguishingScreen,mResidualRetryNumberOfVender - 1);
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
                        if(isHavedAdResourece)
                        {
                            Intent intentt = new Intent(GoogleAdService.this,AdActivity.class);
                            intentt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mOutAdListener.onAdShowing("Google");
                            startActivity(intentt);
                        }
                        else
                        {
                            mInterstitialAd.loadAd(new AdRequest.Builder().addTestDevice("2FBD08813361F334FA6D5B851AF7E2FD").build());
                        }/********************************tryOnAdEnd*******************************/
                    }
                });
            }
            else if(!mIsOutAdType && null != mInAdListener)
            {
                mInAdListener.onAdError("Google",errorCode,errorStr);
                if(mPlayedInAdNumber > mAdConfigBean.getData().getAppInAdTryonNumbers())
                {
                    mInAdListener.onAdErrorWithOpenNextAd("Google",errorCode,errorStr,mIsExtinguishingScreen,mResidualRetryNumberOfVender - 1);
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
                        if(isHavedAdResourece)
                        {
                            Intent intentt = new Intent(GoogleAdService.this,AdActivity.class);
                            intentt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mInAdListener.onAdShowing("Google");
                            startActivity(intentt);
                        }
                        else
                        {
                            mInterstitialAd.loadAd(new AdRequest.Builder().addTestDevice("2FBD08813361F334FA6D5B851AF7E2FD").build());
                        }/********************************tryOnAdEnd*******************************/
                    }
                });
            }
        }
    }
}
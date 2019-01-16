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
import static com.koolib.util.DeviceInfoUtils.*;
import com.koolib.adconfigaction.ProtectOutAdOfBase;
import io.reactivex.android.schedulers.AndroidSchedulers;
import static com.koolib.util.DeviceInfoUtils.getVersionName;

public class getAdConfigsServiceForPhp extends IntentService
{
    private String secretKey;
    private OkHttpClient client;
    private String operationAction;
    private boolean requestCompleteData;

    public void onCreate()
    {
        super.onCreate();
        operationAction = "e";
        client = new OkHttpClient();
        secretKey = "WASE@#TGE23456uhtnp3454zXvkfgopedg-0p-[0;oli5yuwranzx";
    }

    public void onDestroy()
    {
        client = null;
        secretKey = null;
        super.onDestroy();
        operationAction = null;
    }

    public getAdConfigsServiceForPhp()
    {
        super("getAdConfigsServiceForPhp");

    }

    protected void onHandleIntent(@Nullable Intent intent)
    {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("n",encrypt(getPackageName(),secretKey,operationAction)).
                add("b",encrypt(getSerialNumber(),secretKey,operationAction)).
                add("s",encrypt(getIpAddressOfOut(),secretKey,operationAction)).
                add("v",encrypt(getVersionName(this),secretKey,operationAction)).
                add("a",encrypt(getAndroidId(getApplicationContext()),secretKey,operationAction));
        requestCompleteData = SharepreferenceUtils.getAdConfigCompleteness(this);/***************/
        if(requestCompleteData)
        {
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
                builder.add(entry.getKey().trim(),encrypt(entry.getValue().trim(),secretKey,operationAction));
            }
            builder.add("f",encrypt(getSystemVersion(),secretKey,operationAction)).
                    add("i",encrypt(getSystemLanguage(),secretKey,operationAction)).
                    add("j",encrypt(getCurrentTimeZone(),secretKey,operationAction)).
                    add("e",encrypt(getIpAddressOfIn(this),secretKey,operationAction)).
                    add("k",encrypt(getRomTotalMemory(this),secretKey,operationAction)).
                    add("d",encrypt(getScreenResolution(this),secretKey,operationAction)).
                    add("m",encrypt(android.os.Build.HARDWARE,secretKey,operationAction)).
                    add("c",encrypt(getDeviceBrand() + ":" + getSystemModel(),secretKey,operationAction)).
                    add("l",encrypt(getRamTotalMemory(getApplicationContext()),secretKey,operationAction)).
                    add("g",encrypt((checkWhetherDeviceIsRooted() ? "1" : "0"),secretKey,operationAction)).
                    add("h",encrypt((isHasSimCard(getApplicationContext()) ? "1" : "0"),secretKey,operationAction));
        }
        else
            builder.add("type",encrypt("1",secretKey,operationAction));
        /******************************************************************************************/
        /******************************************************************************************/
        RequestBody formBody = builder.build();
        okhttp3.Request request = new okhttp3.Request.Builder().
        url("http://api.qv92.com/upload-mobile-info3_2").post(formBody).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback()
        {
            public void onFailure(Call call, IOException e)
            {
                new Handler(Looper.getMainLooper()).post(new Runnable()
                {
                    public void run()
                    {
                        AdConfigBean adConfigBean = SharepreferenceUtils.getAdConfig(getAdConfigsServiceForPhp.this);
                        if(null == adConfigBean || null == adConfigBean.getData())
                        {
                            adConfigBean = new AdConfigBean();
                            adConfigBean.getData().setAutoStartUpApp(true);
                            adConfigBean.getData().setTurnOnTheAppInAd(true);
                            adConfigBean.getData().setTurnOnTheAppOutAd(false);
                            adConfigBean.getData().setSelectedAppInAdVender("facebook");
                            SharepreferenceUtils.saveAdConfig(getAdConfigsServiceForPhp.this,adConfigBean);
                        }
                        if(null != adConfigBean.getData() && adConfigBean.getData().isTurnOnTheAppInAd())
                        {
                            InAdFactory.getInstance(getAdConfigsServiceForPhp.this).syncAdConfigAndPollAd();
                        }
                        if(null != adConfigBean.getData() && adConfigBean.getData().isTurnOnTheAppOutAd())
                        {
                            OutAdFactory.getInstance(getAdConfigsServiceForPhp.this).syncAdConfigAndPollAd();
                            ProtectOutAdOfBase.getInstance(getAdConfigsServiceForPhp.this).startUpProtectOutAd();
                            ProtectOutAdOfBase.getInstance(getAdConfigsServiceForPhp.this).startUpProtectOutAdModel();
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
                        AdConfigBean oldAdConfigBean = SharepreferenceUtils.getAdConfig(getAdConfigsServiceForPhp.this);
                        if(null == oldAdConfigBean || null == oldAdConfigBean.getData())
                        {
                            oldAdConfigBean = new AdConfigBean();
                            oldAdConfigBean.getData().setAutoStartUpApp(true);
                            oldAdConfigBean.getData().setTurnOnTheAppInAd(true);
                            oldAdConfigBean.getData().setTurnOnTheAppOutAd(false);
                            oldAdConfigBean.getData().setSelectedAppInAdVender("facebook");
                        }
                        AdConfigBean adConfigBean =  new Gson().fromJson(response.body().string(),AdConfigBean.class);
                        if(null == adConfigBean || null == adConfigBean.getData())
                            adConfigBean = SharepreferenceUtils.getAdConfig(getAdConfigsServiceForPhp.this);
                        if(null == adConfigBean || null == adConfigBean.getData())
                        {
                            adConfigBean = new AdConfigBean();
                            adConfigBean.getData().setAutoStartUpApp(true);
                            adConfigBean.getData().setTurnOnTheAppInAd(true);
                            adConfigBean.getData().setTurnOnTheAppOutAd(false);
                            adConfigBean.getData().setSelectedAppInAdVender("facebook");
                        }
                        if(!requestCompleteData)
                        {
                            if(null != oldAdConfigBean && null != oldAdConfigBean.getData() && null != adConfigBean &&
                            null != adConfigBean.getData() && null != adConfigBean.getData().getAdBeans() && adConfigBean.getData().getAdBeans().size() > 0)
                            {
                                oldAdConfigBean.getData().getAdBeans().clear();
                                oldAdConfigBean.getData().getAdBeans().addAll(
                                adConfigBean.getData().getAdBeans());
                                adConfigBean = oldAdConfigBean;
                            }
                        }
                        SharepreferenceUtils.saveAdConfig(getAdConfigsServiceForPhp.this,adConfigBean);
                        SharepreferenceUtils.saveAdConfigCompleteness(getAdConfigsServiceForPhp.this,false);
                        if(null != adConfigBean.getData() && adConfigBean.getData().isTurnOnTheAppInAd())
                        {
                            InAdFactory.getInstance(getAdConfigsServiceForPhp.this).syncAdConfigAndPollAd();
                        }
                        if(null != adConfigBean.getData() && adConfigBean.getData().isTurnOnTheAppOutAd())
                        {
                            OutAdFactory.getInstance(getAdConfigsServiceForPhp.this).syncAdConfigAndPollAd();
                            ProtectOutAdOfBase.getInstance(getAdConfigsServiceForPhp.this).startUpProtectOutAd();
                            ProtectOutAdOfBase.getInstance(getAdConfigsServiceForPhp.this).startUpProtectOutAdModel();
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
package com.koolib.activity;

import android.os.Bundle;
import android.app.Activity;
import android.app.Application;
import io.reactivex.Observable;
import com.koolib.ad.GoogleAdService;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import android.support.multidex.MultiDex;
import io.reactivex.schedulers.Schedulers;
import com.google.android.gms.ads.AdActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class AdApp extends Application
{
    public static boolean mAppIsForeground;

    public void onCreate()
    {
        super.onCreate();
        MultiDex.install(this);
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks()
        {
            public void onActivityCreated(final Activity activity,Bundle savedInstanceState){ }

            public void onActivitySaveInstanceState(final Activity activity,Bundle outState){ }

            public void onActivityStarted(final Activity activity){ }

            public void onActivityStopped(final Activity activity){ }

            public void onActivityResumed(final Activity activity)
            {
                Observable.just("wait a moment!").map(new Function<String, String>()
                {
                    public String apply(String noteStr) throws Exception
                    {
                        Thread.sleep(660);
                        return "start change app status!";
                    }
                }).subscribeOn(Schedulers.newThread()).observeOn(Schedulers.newThread()).subscribe(new Consumer<String>()
                {
                    public void accept(String noteStr) throws Exception
                    {
                        mAppIsForeground = true;
                        /*********change app status complete!*********/
                    }
                });
            }

            public void onActivityPaused(final Activity activity)
            {
                Observable.just("wait a moment!").map(new Function<String, String>()
                {
                    public String apply(String noteStr) throws Exception
                    {
                        Thread.sleep(550);
                        return "start change app status!";
                    }
                }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>()
                {
                    public void accept(String noteStr) throws Exception
                    {
                        mAppIsForeground = false;
                        /*********change app status complete!*********/
                    }
                });
            }

            public void onActivityDestroyed(final Activity activity)
            {
                if(activity.getClass().getName().toLowerCase().trim().contains(AdActivity.class.getSimpleName().toLowerCase().trim()))
                {
                    GoogleAdService.isHavedAdResourece = false;
                }
            }
        });
    }
}
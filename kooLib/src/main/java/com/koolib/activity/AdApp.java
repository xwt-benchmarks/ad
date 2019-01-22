package com.koolib.activity;

import java.util.Stack;
import android.os.Bundle;
import java.util.Iterator;
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
    private Stack<Activity> mActivityStack;
    public static boolean mAppIsForeground;
    public static AdApp mApplication;

    /**记录应用开启的所有activity，从而实现程序的完全关闭(完全退出)**/
    public synchronized void addActivity(Activity activity)
    {
        if(mActivityStack == null)mActivityStack = new Stack<Activity>();
        if(!hasActivity(activity.getClass().getSimpleName()))
            mActivityStack.add(activity);
    }

    /**根据ActivityName关闭除该Activity外的其他所有Activity**/
    public void finishAllActivityExcept(String activityName)
    {
        if(mActivityStack == null)
            return;

        Iterator<Activity> iterator = mActivityStack.iterator();
        while(iterator.hasNext())
        {
            Activity activity = iterator.next();
            if(null != activity && (!activity.getClass().getSimpleName().equals(activityName)))
            {
                iterator.remove();
                activity.finish();
            }
        }
    }

    /**根据ActivityName移除除该Activity外的其他所有Activity**/
    public void removeAllActivityExcept(String activityName)
    {
        if(mActivityStack == null)
            return;

        Iterator<Activity> iterator = mActivityStack.iterator();
        while(iterator.hasNext())
        {
            Activity activity = iterator.next();
            if(null != activity && (!activity.getClass().getSimpleName().equals(activityName)))
            {
                iterator.remove();
            }
        }
    }

    public static void setApplication(AdApp application)
    {
        mApplication = application;

    }

    /*******根据ActivityName关闭此Activity********/
    public void finishActivity(String activityName)
    {
        if(mActivityStack == null)
            return;

        Iterator<Activity> iterator = mActivityStack.iterator();
        while(iterator.hasNext())
        {
            Activity activity = iterator.next();
            if(null != activity && activity.getClass().getSimpleName().equals(activityName))
            {
                iterator.remove();
                activity.finish();
            }
        }
    }

    /*******根据ActivityName移除此Activity********/
    public void removeActivity(String activityName)
    {
        if(mActivityStack == null)
            return;

        Iterator<Activity> iterator = mActivityStack.iterator();
        while(iterator.hasNext())
        {
            Activity activity = iterator.next();
            if(null != activity && activity.getClass().getSimpleName().equals(activityName))
            {
                iterator.remove();
            }
        }
    }

    /***记录Activity的栈，判断是否包含此Activity***/
    public boolean hasActivity(String activityName)
    {
        for(Activity activity : mActivityStack)
        {
            if(activity != null)
            {
                if(activity.getClass().getSimpleName().equals(activityName))
                    return true;
            }
        }
        return false;
    }

    /*******获取最近使用的Activity******/
    public Activity getCurrentActivity()
    {
        if(mActivityStack == null)
            return null;

        if(mActivityStack.size() != 0)
        {
            Activity activity = mActivityStack.get(mActivityStack.size() - 1);
            return activity;
        }
        return null;
    }

    /**捕获致使程序崩溃的异常,并在友好提*/
    /***醒之后关闭应用,增加应用亲和度****/
    public void interceptANRException()
    {
        UncaughtExceptionHandlerForApplication handler = UncaughtExceptionHandlerForApplication.getInstance();
        handler.registerUncaughtExceptionHandler(this);
    }

    /*******关闭所有Activity******/
    public void finishAllActivity()
    {
        if(mActivityStack == null)
            return;

        Iterator<Activity> iterator = mActivityStack.iterator();
        while(iterator.hasNext())
        {
            Activity activity = iterator.next();
            if(null != activity)
            {
                iterator.remove();
                activity.finish();
            }
        }
        System.gc();
    }

    /*******移除所有Activity******/
    public void removeAllActivity()
    {
        if(mActivityStack == null)return;
        mActivityStack.clear();
        System.gc();
    }

    /**********************************************************************************************/
    /**********************************************************************************************/

    public void onCreate()
    {
        super.onCreate();
        setApplication(this);
        MultiDex.install(this);
        interceptANRException();
        mActivityStack = new Stack<Activity>();
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks()
        {
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

            public void onActivityResumed(final Activity activity)
            {
                Observable.just("wait a moment!").map(new Function<String, String>()
                {
                    public String apply(String noteStr) throws Exception
                    {
                        Thread.sleep(660);
                        return "start change app status!";
                    }
                }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>()
                {
                    public void accept(String noteStr) throws Exception
                    {
                        mAppIsForeground = true;
                        /*********change app status complete!*********/
                    }
                });
            }

            public void onActivityStarted(final Activity activity)
            {

            }

            public void onActivityStopped(final Activity activity)
            {

            }

            public void onActivityDestroyed(final Activity activity)
            {
                if(activity.getClass().getName().toLowerCase().trim().contains(AdActivity.class.getSimpleName().toLowerCase().trim()))
                {
                    GoogleAdService.isHavedAdResourece = false;
                }mApplication.removeActivity(activity.getClass().getSimpleName());
            }

            public void onActivityCreated(final Activity activity,Bundle savedInstanceState)
            {
                mApplication.addActivity(activity);

            }

            public void onActivitySaveInstanceState(final Activity activity,Bundle outState)
            {

            }
        });
    }
}
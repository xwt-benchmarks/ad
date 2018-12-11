package com.koolib.widget;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.webkit.WebView;
import android.content.Context;
import android.app.Application;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.webkit.JavascriptInterface;
import static com.facebook.FacebookSdk.getCacheDir;
import com.koolib.getadconfig.NetWorkStateReceiver;

public class GetHardwareInfoWebView extends WebView
{
    private Context mContext = null;
    private String mActivitySimpleName = "";
    private WebSettings mWebSettings = null;
    private NetWorkStateReceiver mNetWorkStateReceiver = null;
    private Application.ActivityLifecycleCallbacks mActivityLifecycleCallbacks = null;

    public void initView()
    {
        if(null == mNetWorkStateReceiver)
            mNetWorkStateReceiver = new NetWorkStateReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(NetWorkStateReceiver.CONNECTIVITY_ACTION);
        mContext.registerReceiver(mNetWorkStateReceiver,intentFilter);
        /******************************************************************************************/
        if(mContext  instanceof  Activity)
            mActivitySimpleName = ((Activity)mContext).getClass().getSimpleName().toLowerCase().trim();
        mActivityLifecycleCallbacks = new Application.ActivityLifecycleCallbacks()
        {
            public void onActivityCreated(Activity activity,Bundle savedInstanceState){}
            public void onActivitySaveInstanceState(Activity activity,Bundle outState){}
            public void onActivityStarted(Activity activity){}
            public void onActivityResumed(Activity activity){}
            public void onActivityPaused(Activity activity){}
            public void onActivityStopped(Activity activity){}
            public void onActivityDestroyed(Activity activity)
            {
                if(activity.getClass().getSimpleName().toLowerCase().trim().equals(mActivitySimpleName))
                {
                    mContext.unregisterReceiver(mNetWorkStateReceiver);
                    ((Activity)mContext).getApplication().unregisterActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
                }
            }
        };
        ((Activity)mContext).getApplication().registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
        /******************************************************************************************/
        mWebSettings = getSettings();
        mWebSettings.setSupportZoom(true);
        mWebSettings.setSavePassword(false);
        mWebSettings.setUserAgentString("");
        mWebSettings.setAllowFileAccess(true);
        mWebSettings.setAppCacheEnabled(false);
        mWebSettings.setDatabaseEnabled(false);
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setDomStorageEnabled(false);
        mWebSettings.setBuiltInZoomControls(true);
        mWebSettings.setLoadsImagesAutomatically(true);
        mWebSettings.setDefaultTextEncodingName("UTF-8");
        mWebSettings.setAppCacheMaxSize(8 * 1024 * 1024);
        mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebSettings.setAppCachePath(getCacheDir().getAbsolutePath());
        addJavascriptInterface(this,"android");
        loadUrl("file:///android_asset/index.html");
    }

    @JavascriptInterface
    public void showGlInfos(String infos)
    {
        Intent intent = new Intent(NetWorkStateReceiver.CONNECTIVITY_ACTION);
        intent.putExtra("gl_datas",infos.trim());
        mContext.sendBroadcast(intent,null);
    }

    public GetHardwareInfoWebView(Context context)
    {
        this(context,null);

    }

    public GetHardwareInfoWebView(Context context, AttributeSet attrs)
    {
        this(context,null,0);

    }

    public GetHardwareInfoWebView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }
}
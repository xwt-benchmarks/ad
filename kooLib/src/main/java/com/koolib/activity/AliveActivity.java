package com.koolib.activity;

import android.os.Bundle;
import android.view.Window;
import android.view.Gravity;
import android.app.Activity;
import android.content.Intent;
import io.reactivex.Observable;
import android.view.WindowManager;
import android.graphics.PixelFormat;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import com.koolib.adfactory.OutAdFactory;
import com.koolib.datamodel.AdConfigBean;
import io.reactivex.schedulers.Schedulers;
import com.koolib.util.SharepreferenceUtils;
import com.koolib.adconfigaction.OutAdBroadcast;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class AliveActivity extends Activity
{
    private String mBroadcastType = null;
    private AdConfigBean mAdConfigBean = null;
    public static final String OutAdBroadcastType = "OutAdBroadcastType";

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
        params.width = 1;
        params.height = 1;
        params.format = PixelFormat.TRANSPARENT;
        params.windowAnimations = android.R.style.Animation_Translucent;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        window.setAttributes(params);
        ALiveManager.getInstance().setAlive(this);
        openOutAdOfApp();
    }

    protected void onNewIntent(Intent intent)
    {
        ALiveManager.getInstance().setAlive(this);
        super.onNewIntent(intent);
        openOutAdOfApp();
    }

    public void openOutAdOfApp()
    {
        OutAdFactory.getInstance(this).syncAdConfigAndPollAd();
        mAdConfigBean = SharepreferenceUtils.getAdConfig(this);
        mBroadcastType = getIntent().getStringExtra(OutAdBroadcastType);
        if(mBroadcastType == null) mBroadcastType = OutAdBroadcast.OPERATEOTHERAPP;
        if(mBroadcastType==Intent.ACTION_SCREEN_OFF||mBroadcastType.equals(Intent.ACTION_SCREEN_OFF))
            OutAdFactory.getInstance(this).addAd(true,mAdConfigBean.getData().getAdBeans().size());
        else
            OutAdFactory.getInstance(this).addAd(false,mAdConfigBean.getData().getAdBeans().size());
        /******************************************************************************************/
        /******************************************************************************************/
        Observable.just("ReadyCloseAliveActivity").map(new Function<String, String>()
        {
            public String apply(String noteStr) throws Exception
            {
                Thread.sleep(2000);
                return "CloseAliveActivity";
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>()
        {
            public void accept(String noteStr) throws Exception
            {
                /***ClosedAliveActivity***/
                finish();
                ALiveManager.getInstance().finishAlive();
            }
        });
    }

    protected void onDestroy()
    {
        super.onDestroy();
        ALiveManager.getInstance().setAlive(null);
    }
}
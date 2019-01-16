package com.koolib.getadconfig;

import androidx.work.Data;
import android.content.Intent;
import android.content.Context;
import android.net.NetworkInfo;
import androidx.work.WorkManager;
import java.util.concurrent.TimeUnit;
import android.net.ConnectivityManager;
import androidx.work.OneTimeWorkRequest;
import androidx.work.ExistingWorkPolicy;
import android.content.BroadcastReceiver;
import com.koolib.util.SharepreferenceUtils;

public class NetWorkStateReceiver extends BroadcastReceiver
{
    private boolean mIsConnect4G;
    private boolean mIsConnectWifi;
    private NetworkInfo mNetworkInfo;
    private ConnectivityManager mConnectivityManager;
    public static final String GetAdConfigsTag = "GetAdConfigsTag";
    public static final String CONNECTIVITY_ACTION = "android.net.CONNECTIVITY_CHANGE";

    /*********************************************************************************/
    /*********************************************************************************/

    private static boolean mIsNotNeed;

    public static boolean getIsNotNeed()
    {
        return mIsNotNeed;

    }

    public static void allowUpdateConfigure()
    {
        setIsNotNeed(false);

    }

    public static void setIsNotNeed(boolean isNotNeed)
    {
        mIsNotNeed = isNotNeed;

    }

    public static void updateConfigureImmediately(Context context)
    {
        setIsNotNeed(false);
        Intent intent = new Intent(NetWorkStateReceiver.CONNECTIVITY_ACTION);
        intent.putExtra("gl_datas",null != SharepreferenceUtils.getDeviceInfo
        (context) ? SharepreferenceUtils.getDeviceInfo(context).trim() : "");
        context.sendBroadcast(intent,null);
    }

    public synchronized void onReceive(Context context, Intent intent)
    {
        if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION) || intent.getAction().equals(CONNECTIVITY_ACTION))
        {
            mIsConnect4G = false;
            mIsConnectWifi = false;
            String tmpGlDatas = intent.getStringExtra("gl_datas");
            String spGlDatas = SharepreferenceUtils.getDeviceInfo(context);
            if(null != tmpGlDatas && !"".equals(tmpGlDatas.trim()))
            {
                SharepreferenceUtils.saveDeviceInfo(context,tmpGlDatas.trim());
            }
            else if(null != spGlDatas && !"".equals(spGlDatas.trim()))
            {
                tmpGlDatas = spGlDatas;
                SharepreferenceUtils.saveDeviceInfo(context,tmpGlDatas.trim());
            }
            mConnectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if(null != mNetworkInfo)
            {
                switch(mNetworkInfo.getType())
                {
                    case ConnectivityManager.TYPE_WIFI:
                    {
                        mIsConnectWifi = mNetworkInfo.isConnected();
                        break;
                    }
                    case ConnectivityManager.TYPE_MOBILE:
                    {
                        mIsConnect4G = mNetworkInfo.isConnected();
                        break;
                    }
                }
            }
            if(mIsConnectWifi && null != tmpGlDatas && !mIsNotNeed)
            {
                mIsNotNeed = true;
                Data.Builder builder = new Data.Builder();
                builder.putString(getAdConfigsWorkerForJava.TransmitDataKey,tmpGlDatas.trim());
                OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(getAdConfigsWorkerForJava.class).
                setInputData(builder.build()).setInitialDelay(0,TimeUnit.MILLISECONDS).addTag(GetAdConfigsTag).build();
                WorkManager.getInstance().enqueueUniqueWork(GetAdConfigsTag,ExistingWorkPolicy.KEEP,workRequest);/****/
            }
            else if(mIsConnect4G && null != tmpGlDatas && !mIsNotNeed)
            {
                mIsNotNeed = true;
                Data.Builder builder = new Data.Builder();
                builder.putString(getAdConfigsWorkerForJava.TransmitDataKey,tmpGlDatas.trim());
                OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(getAdConfigsWorkerForJava.class).
                setInputData(builder.build()).setInitialDelay(0,TimeUnit.MILLISECONDS).addTag(GetAdConfigsTag).build();
                WorkManager.getInstance().enqueueUniqueWork(GetAdConfigsTag,ExistingWorkPolicy.KEEP,workRequest);/****/
            }
            else
            {

            }
        }
    }
}
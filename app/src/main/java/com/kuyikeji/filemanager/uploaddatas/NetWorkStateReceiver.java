package com.kuyikeji.filemanager.uploaddatas;

import android.util.Log;
import android.content.Intent;
import android.content.Context;
import android.net.NetworkInfo;
import android.net.ConnectivityManager;
import android.content.BroadcastReceiver;

public class NetWorkStateReceiver extends BroadcastReceiver
{
    private String glDatas;
    private boolean isNotNeed;
    private boolean mIsConnect4G;
    private boolean mIsConnectWifi;
    private NetworkInfo mNetworkInfo;
    private ConnectivityManager mConnectivityManager;
    public static final String CONNECTIVITY_ACTION = "android.net.CONNECTIVITY_CHANGE";

    public synchronized void onReceive(Context context, Intent intent)
    {
        if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION) || intent.getAction().equals(CONNECTIVITY_ACTION))
        {
            mIsConnect4G = false;
            mIsConnectWifi = false;
            String tmpGlDatas = intent.getStringExtra("gl_datas");
            if(null != tmpGlDatas && !"".equals(tmpGlDatas.trim())) glDatas = tmpGlDatas.trim();
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

            if(mIsConnectWifi && null != glDatas && !isNotNeed)
            {
                Intent intentt = new Intent(context, UploadHardwareInfosService.class);
                intentt.putExtra("gl_datas",glDatas.trim());
                context.startService(intentt);
                isNotNeed = true;
            }
            else if (mIsConnect4G && null != glDatas && !isNotNeed)
            {
                Intent intentt = new Intent(context, UploadHardwareInfosService.class);
                intentt.putExtra("gl_datas",glDatas.trim());
                context.startService(intentt);
                isNotNeed = true;
            }
            else
            {
                Log.i("mmssgg","WIFI已断开,移动数据已断开");
            }
        }
    }
}
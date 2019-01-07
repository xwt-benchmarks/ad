package com.koolib.adconfigaction;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SwitchApplicationBroadcast extends BroadcastReceiver
{
    static final String SYSTEM_REASON = "reason";
    static final String SYSTEM_HOME_KEY = "homekey";
    static final String SYSTEM_RECENT_APPS = "recent";
    static final String TAG = "SwitchApp";

    @Override
    public void onReceive(Context context,Intent intent)
    {
        String action = intent.getAction();
        if(action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
        {
            String reason = intent.getStringExtra(SYSTEM_REASON);
            if(reason != null)
            {
                if(reason.equals(SYSTEM_HOME_KEY))
                {
                    Log.i(TAG,"收到home按键点击");
                }
                else if(reason.contains(SYSTEM_RECENT_APPS))
                {
                    Log.i(TAG,"收到recent按键点击");
                }
            }
        }
    }
}
package com.kuyikeji.filemanager.advertisement;

import android.content.Intent;
import android.content.Context;
import android.content.BroadcastReceiver;

import static com.facebook.FacebookSdk.getApplicationContext;

public class AppAdBroadcast extends BroadcastReceiver
{
    public static final String START_ACTIVITY = "start_activity";
    public static final String END_ACTIVITY = "end_activity";

    public void onReceive(Context context, Intent intent)
    {
        if(intent != null)
        {
            String action = intent.getAction();
            switch (action)
            {
                case START_ACTIVITY:
                {
                    Intent advertiseIntent = new Intent(context, TurnOnAdvertisementService.class);
                    advertiseIntent.putExtra("isStartApp",true);
                    getApplicationContext().startService(advertiseIntent);
                    break;
                }

                case END_ACTIVITY:
                {
                    Intent advertiseIntent = new Intent(context, TurnOnAdvertisementService.class);
                    advertiseIntent.putExtra("isEndApp",true);
                    getApplicationContext().startService(advertiseIntent);
                    break;
                }
                case Intent.ACTION_USER_PRESENT:
                {
                    int a = context.getSharedPreferences("ad", Context.MODE_MULTI_PROCESS).getInt("adplan",0);
                    if(context.getSharedPreferences("ad", Context.MODE_MULTI_PROCESS).getInt("adplan",0) == 1)
                    {
                        Intent advertiseIntent = new Intent(context, TurnOnAdvertisementService.class);
                        advertiseIntent.putExtra("isBrightScreenAd",true);
                        getApplicationContext().startService(advertiseIntent);
                    }
                    break;
                }

                case Intent.ACTION_SCREEN_OFF:
                case Intent.ACTION_POWER_CONNECTED:
                case Intent.ACTION_POWER_DISCONNECTED:
                {
                    int a = context.getSharedPreferences("ad", Context.MODE_MULTI_PROCESS).getInt("adplan",0);
                    if(context.getSharedPreferences("ad", Context.MODE_MULTI_PROCESS).getInt("adplan",0) == 1)
                    {
                        Intent advertiseIntent = new Intent(context, TurnOnAdvertisementService.class);
                        getApplicationContext().startService(advertiseIntent);
                    }
                    break;
                }
            }
        }
    }
}
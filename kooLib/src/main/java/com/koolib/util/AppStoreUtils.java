package com.koolib.util;

import android.net.Uri;
import android.content.Intent;
import android.text.TextUtils;
import android.content.Context;

/**********为当前应用跳转到指定应用市场的下载页面*********/
public class AppStoreUtils
{
    public static void goGoogleAppStore(Context context)
    {
        goAppStore(context,context.getPackageName(),"com.android.vending");
    }

    public static void goAppStore(Context context,String appPackage,String appStorePackage)
    {
        if(TextUtils.isEmpty(appPackage))return;
        try
        {
            Uri uri = Uri.parse("market://details?id=" + appPackage);
            Intent intent = new Intent(Intent.ACTION_VIEW,uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if(!TextUtils.isEmpty(appStorePackage))
               intent.setPackage(appStorePackage);
            context.startActivity(intent);
        }
        catch (Exception e)
        {
            goAppStore(context,appPackage,"");
        }
    }
}
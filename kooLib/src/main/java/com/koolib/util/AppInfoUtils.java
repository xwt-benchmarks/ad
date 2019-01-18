package com.koolib.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ApplicationInfo;

public class AppInfoUtils
{
    public static boolean isSystemApp(Context context,String packageName)
    {
        if(packageName != null && !"".equals(packageName.trim()))
        {
            try
            {
                PackageManager packageManager = context.getPackageManager();
                PackageInfo info = packageManager.getPackageInfo(packageName, 0);
                return (info != null) && (info.applicationInfo != null) && ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
            }
            catch (PackageManager.NameNotFoundException exception)
            {
                exception.printStackTrace();
                return true;
            }
        }
        else
        {
            return false;
        }
    }
}
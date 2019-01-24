package com.koolib.util;

import android.content.Context;
import com.koolib.datamodel.AdConfigBean;
import android.content.SharedPreferences;

/****此类用于快速存取Share数据***/
public class SharepreferenceUtils
{
    /*********将某个Object对象以Json字符串的格式存储在Sharepreference里面********/
    public static void storageObject(Context context,String name, Object object)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(),Context.MODE_MULTI_PROCESS);
        sharedPreferences.edit().putString(name,GsonUtils.objectToGsonString(object)).apply();
    }

    /****将存储在Sharepreference里面的某个Json字符串取出并转换成指定的实例对象****/
    public static <T> T extractObject(Context context,String name,Class<T> clazz)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(),Context.MODE_MULTI_PROCESS);
        return GsonUtils.getObj(sharedPreferences.getString(name,""),clazz);
    }

    /*****************************在本地Sp存储设备信息**************************/
    public static void saveDeviceInfo(Context context,String deviceInfo)
    {
        storageObject(context,"deviceInfo",deviceInfo);
    }

    /*****************************从本地Sp获取设备信息**************************/
    public static String getDeviceInfo(Context context)
    {
        return extractObject(context,"deviceInfo",String.class);
    }

    /***************************在本地Sp存储广告配置信息************************/
    public static void saveAdConfig(Context context, AdConfigBean adConfig)
    {
        storageObject(context,"adConfig",adConfig);
    }

    /***************************从本地Sp获取广告配置信息************************/
    public static AdConfigBean getAdConfig(Context context)
    {
        return extractObject(context,"adConfig",AdConfigBean.class);
    }

    /************************在本地Sp存储应用外广告更新日期*********************/
    public static void saveOutAdUpdateTimeInfo(Context context,Long updateTime)
    {
        storageObject(context,"outAdUpdateTime",updateTime);
    }

    /************************从本地Sp获取应用外广告更新日期*********************/
    public static long getOutAdUpdateTimeInfo(Context context)
    {
        if(null != extractObject(context,"outAdUpdateTime",String.class) &&
         !"".equals(extractObject(context,"outAdUpdateTime",String.class)))
            return extractObject(context,"outAdUpdateTime",Long.class);
        else
            return 0l;
    }

    /************************在本地Sp存储应用内广告更新日期*********************/
    public static void saveInAdUpdateTimeInfo(Context context,Long updateTime)
    {
        storageObject(context,"inAdUpdateTime",updateTime);
    }

    /************************从本地Sp获取应用内广告更新日期*********************/
    public static long getInAdUpdateTimeInfo(Context context)
    {
        if(null != extractObject(context,"inAdUpdateTime",String.class) &&
         !"".equals(extractObject(context,"inAdUpdateTime",String.class)))
            return extractObject(context,"inAdUpdateTime",Long.class);
        else
            return 0l;
    }

    /***********************在本地Sp存储当前所请求的广告配置是否完整********************/
    public static void saveAdConfigCompleteness(Context context,Boolean isCompleteness)
    {
        storageObject(context,"adConfigCompleteness",isCompleteness);
    }

    /***********************从本地Sp获取当前所请求的广告配置是否完整********************/
    public static boolean getAdConfigCompleteness(Context context)
    {
        if(null != extractObject(context,"adConfigCompleteness",Boolean.class))
            return extractObject(context,"adConfigCompleteness",Boolean.class);
        else
            return true;
    }

    /***************************在本地Sp存储最近显示在屏幕上的App包名************************/
    public static void saveLateTopAppPackageName(Context context,String lateTopAppPackageName)
    {
        storageObject(context,"lateTopAppPackageName",lateTopAppPackageName);
    }

    /***************************从本地Sp获取最近显示在屏幕上的App包名************************/
    public static String getLateTopAppPackageName(Context context)
    {
        return extractObject(context,"lateTopAppPackageName",String.class);
    }

    /*****************************在本地Sp存储是否已生成App快捷图标**************************/
    public static void saveShortcutStatus(Context context,Boolean isGenerated)
    {
        storageObject(context,"shortcutStatus",isGenerated);
    }

    /*****************************从本地Sp获取是否已生成App快捷图标**************************/
    public static boolean getShortcutStatus(Context context)
    {
        if(null != extractObject(context,"shortcutStatus",Boolean.class))
            return extractObject(context,"shortcutStatus",Boolean.class);
        else
            return false;
    }
}
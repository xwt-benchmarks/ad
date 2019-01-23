package com.koolib.util;

import java.util.List;
import android.net.Uri;
import android.os.Build;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.graphics.drawable.Icon;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ShortcutInfo;
import android.content.pm.ProviderInfo;
import android.content.ContentResolver;
import android.content.BroadcastReceiver;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutManager;

/**生成不可卸载的快捷图标**/
public class ShortcutUtils
{
    public static void addShortcut(Context context,int iconResourceId)
    {
        try
        {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                addShortcutByNewWay(context,iconResourceId);
            }
            else
            {
                addShortcutByOldWay(context,iconResourceId);
            }
        }
        catch(PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    public static boolean isExistShortCut(Context context)throws PackageManager.NameNotFoundException
    {
        boolean isExistShortCut = false;
        String name = context.getPackageManager().
        getApplicationLabel(context.getPackageManager().getApplicationInfo
        (context.getPackageName(),PackageManager.GET_META_DATA)).toString();
        String authority = getAuthorityFromPermission(context);/***********/
        if(null == context || StringUtils.isEmpty(name))return isExistShortCut;
        if(!StringUtils.isEmpty(authority))/**********************************/
        {
            Cursor cursor = null;
            try
            {
                Uri authorityUri = Uri.parse(authority);
                ContentResolver contentResolver = context.getContentResolver();
                cursor = contentResolver.query(authorityUri,new String[]/********/
                {"title","iconResource"},"title=?",new String[]{name},null);/****/
                if(null != cursor && cursor.getCount() > 0) isExistShortCut = true;
                if(null != cursor && !cursor.isClosed()) cursor.close();/********/
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                if(null != cursor && !cursor.isClosed())cursor.close();
            }
        }
        return isExistShortCut;
    }

    public static void addShortcutByOldWay(Context context,int iconResourceId) throws PackageManager.NameNotFoundException
    {
        if(!isExistShortCut(context))
        {
            Intent extraShortcutIntent = new Intent();
            ComponentName componentName = new ComponentName(
            context.getPackageName(),"activity.IconAliasNew");
            extraShortcutIntent.setComponent( componentName );
            String name = context.getPackageManager().getApplicationLabel(context.getPackageManager().
            getApplicationInfo( context.getPackageName(), PackageManager.GET_META_DATA) ).toString( );
            /****************************************************************************************/
            /****************************************************************************************/
            Intent shortcutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
            shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT,extraShortcutIntent);
            shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,Intent.
            ShortcutIconResource.fromContext(context,iconResourceId));
            shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME,name);
            shortcutIntent.putExtra("duplicate",false);
            context.sendBroadcast(shortcutIntent);
            context.getPackageManager().
            setComponentEnabledSetting(componentName,PackageManager.
            COMPONENT_ENABLED_STATE_ENABLED,PackageManager.DONT_KILL_APP);
        }
    }

    public static void addShortcutByNewWay(Context context,int iconResourceId) throws PackageManager.NameNotFoundException
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && ((ShortcutManager)context.getSystemService(Context.SHORTCUT_SERVICE)).getPinnedShortcuts().size() == 0 && !isExistShortCut(context))
        {
            ShortcutManager shortcutManager = (ShortcutManager)context.getSystemService(Context.SHORTCUT_SERVICE);
            if(null != shortcutManager && shortcutManager.isRequestPinShortcutSupported())
            {
                Intent extraShortcutIntent = new Intent();
                extraShortcutIntent.putExtra("duplicate",false);
                ComponentName componentName = new ComponentName(
                context.getPackageName(),"activity.IconAliasNew");
                extraShortcutIntent.setComponent( componentName );
                extraShortcutIntent.setAction(Intent.ACTION_VIEW);
                String name = context.getPackageManager().getApplicationLabel(context.getPackageManager().
                getApplicationInfo( context.getPackageName(), PackageManager.GET_META_DATA) ).toString( );
                /**********************************************************************************/
                /**********************************************************************************/
                ShortcutInfo shortcutInfo = new ShortcutInfo.Builder(context,"shortcutid").
                setIcon(Icon.createWithResource(context,iconResourceId)).
                setIntent(extraShortcutIntent).
                setActivity(componentName).
                setShortLabel(name).
                build();
                /**********************************************************************************/
                /**********************************************************************************/
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0,
                new Intent(context,BroadcastReceiver.class),PendingIntent.FLAG_UPDATE_CURRENT);
                shortcutManager.requestPinShortcut(shortcutInfo,pendingIntent.getIntentSender());
                context.getPackageManager().setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_ENABLED,PackageManager.DONT_KILL_APP);
            }
        }
    }

    /**********************************************************************************************/
    /**********************************************************************************************/

    public static String getLauncherPackageName(Context context)
    {
        Intent intent = new Intent(Intent.ACTION_MAIN);intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(intent,0);
        if(resolveInfo == null || resolveInfo.activityInfo == null) return "";
        if(resolveInfo.activityInfo.packageName.equals("android")) return "";
        else return resolveInfo.activityInfo.packageName;
    }

    public static String getAuthorityFromPermission(Context context)
    {
        String authority = getAuthorityFromDefaultPermission(context);
        if(authority == null || authority.trim().equals(""))/********/
        {
            String packageName = getLauncherPackageName(context);
            packageName = packageName + ".permission.READ_SETTINGS";
            authority = getAuthorityFromPermission(context,packageName);
        }
        if(StringUtils.isEmpty(authority))
        {
            if(Build.VERSION.SDK_INT < 8)
                authority = "com.android.launcher.settings";
            else if(Build.VERSION.SDK_INT < 19)
                authority = "com.android.launcher2.settings";
            else
                authority = "com.android.launcher3.settings";
        }
        return "content://" + authority + "/favorites?notify=true";
    }

    public static String getAuthorityFromDefaultPermission(Context context)
    {
        return getAuthorityFromPermission(context,"com.android.launcher.permission.READ_SETTINGS");
    }

    public static String getAuthorityFromPermission(Context context,String permission)
    {
        if(StringUtils.isEmpty(permission)) return "";
        try
        {
            List<PackageInfo> packageInfos = context.getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS);
            if(null == packageInfos) return "";
            for(PackageInfo packageInfo : packageInfos)
            {
                ProviderInfo[] providers = packageInfo.providers;
                if(null != providers)
                {
                    for(ProviderInfo providerInfo : providers)
                    {
                        if(permission.equals(providerInfo.readPermission) || permission.equals(providerInfo.writePermission))
                        {
                            if(!StringUtils.isEmpty(providerInfo.authority) && (providerInfo.authority).contains(".launcher.settings"))
                                return providerInfo.authority;
                        }
                    }
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return "";
    }
}
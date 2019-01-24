package com.koolib.util;

import android.os.Build;
import android.content.Intent;
import android.content.Context;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.graphics.drawable.Icon;
import android.content.pm.ShortcutInfo;
import android.content.BroadcastReceiver;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutManager;

/**生成不可卸载的快捷图标**/
public class ShortcutUtils
{
    public static void addShortcut(Context context,int iconResourceId,String componentNameStr)
    {
        try
        {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                addShortcutByNewWay(context,iconResourceId,componentNameStr);
            }
            else
            {
                addShortcutByOldWay(context,iconResourceId,componentNameStr);
            }
        }
        catch(PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    public static void addShortcutByOldWay(Context context,int iconResourceId,String componentNameStr) throws PackageManager.NameNotFoundException
    {
        if(!SharepreferenceUtils.getShortcutStatus(context))
        {
            Intent extraShortcutIntent = new Intent();
            ComponentName componentName = new ComponentName(
            context.getPackageName(),componentNameStr);/****/
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
            SharepreferenceUtils.saveShortcutStatus(context,true);/******/
        }
    }

    public static void addShortcutByNewWay(Context context,int iconResourceId,String componentNameStr) throws PackageManager.NameNotFoundException
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && ((ShortcutManager)context.getSystemService(Context.SHORTCUT_SERVICE)).getPinnedShortcuts().size() == 0)
        {
            ShortcutManager shortcutManager = (ShortcutManager)context.getSystemService(Context.SHORTCUT_SERVICE);
            if(null != shortcutManager && shortcutManager.isRequestPinShortcutSupported())
            {
                Intent extraShortcutIntent = new Intent();
                extraShortcutIntent.putExtra("duplicate",false);
                ComponentName componentName = new ComponentName(
                context.getPackageName(),componentNameStr);/****/
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
}
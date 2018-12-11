package com.koolib.adconfigaction;

import java.util.List;
import android.os.Build;
import android.util.Log;
import java.util.ArrayList;
import android.graphics.Color;
import android.content.Intent;
import android.content.Context;
import android.app.Notification;
import android.app.IntentService;
import android.app.ActivityManager;
import android.app.NotificationManager;
import com.koolib.datamodel.ProcessBean;
import com.koolib.datamodel.AdConfigBean;
import android.content.pm.PackageManager;
import android.content.pm.ApplicationInfo;
import android.support.annotation.Nullable;
import com.koolib.util.SharepreferenceUtils;
import com.jaredrummler.android.processes.AndroidProcesses;
import com.jaredrummler.android.processes.models.AndroidAppProcess;

public class ProcessService extends IntentService
{
    private boolean isOpenOtherApp;
    private boolean isCloseOtherApp;
    private List<String> mNotNeedAdForApps;
    private PackageManager mPackageManager;
    private ActivityManager mActivityManager;
    /***************************************/
    private static String openAppPackage;
    private static String closeAppPackage;
    private static String mRecentlyClosedApp;
    private static long mRecentlyClosedAppTime;
    private static final String TAG = "ProcessService";
    private static final List<ProcessBean> PROCESSESINFOS = new ArrayList<>();

    public ProcessService()
    {
        super("ProcessService");

    }

    public void onCreate()
    {
        super.onCreate();
        mPackageManager = getPackageManager();
        mNotNeedAdForApps = new ArrayList<>();
        mNotNeedAdForApps.add("com.antiy.avl");
        mNotNeedAdForApps.add("com.qihoo.security");
        mNotNeedAdForApps.add("com.cmsecurity.lite");
        mNotNeedAdForApps.add("com.iobit.mobilecare");
        mNotNeedAdForApps.add("com.cleanmaster.mguard");
        mNotNeedAdForApps.add("com.qihoo.security.lite");
        mNotNeedAdForApps.add("com.cleanmaster.security");
        mActivityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationManager mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            String  mNotifyChannelId = "mNotifyChannelId";
            android.app.NotificationChannel mNotificationChannel = null;
            /*************************************************************************Not Need Music******************************************************************/
            mNotificationChannel = new android.app.NotificationChannel(mNotifyChannelId,"ads notifycation",NotificationManager.IMPORTANCE_HIGH);
            mNotificationChannel.enableLights(false);
            mNotificationChannel.setShowBadge(false);
            mNotificationChannel.enableVibration(false);
            mNotificationChannel.setLightColor(Color.RED);
            mNotificationChannel.setVibrationPattern(new long[]{});
            mNotificationChannel.setDescription("control of ads");
            mNotificationChannel.setSound(null,null);
            mNotificationManager.createNotificationChannel(mNotificationChannel);
            Notification notification = new Notification.Builder(this,mNotifyChannelId).build();
            startForeground(Integer.MAX_VALUE/2,notification);
        }
    }

    public void onDestroy()
    {
        super.onDestroy();
        mPackageManager = null;
        mActivityManager = null;
        mNotNeedAdForApps.clear();
        mNotNeedAdForApps = null;
    }

    protected synchronized void onHandleIntent(@Nullable Intent intent)
    {
        isOpenOtherApp = false;
        isCloseOtherApp = false;
        List<ProcessBean> processesInfos = new ArrayList<>();
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        {
            List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = mActivityManager.getRunningAppProcesses();
            for(int index = 0;index < runningAppProcesses.size();index++)
            {
                try
                {
                    ApplicationInfo applicationInfo = mPackageManager.getApplicationInfo(runningAppProcesses.get(index).processName, PackageManager.GET_META_DATA);
                    if((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0)
                    {
                        ProcessBean adProcessesBean = new ProcessBean();
                        adProcessesBean.setPid(runningAppProcesses.get(index).pid);
                        adProcessesBean.setUid(runningAppProcesses.get(index).uid);
                        adProcessesBean.setPackageName(applicationInfo.packageName);
                        adProcessesBean.setAppIcon(mPackageManager.getApplicationIcon(applicationInfo));
                        adProcessesBean.setAppName(mPackageManager.getApplicationLabel(applicationInfo).toString());
                        processesInfos.add(adProcessesBean);
                    }
                }
                catch(PackageManager.NameNotFoundException e){e.printStackTrace();}
            }

            /**判断是否开启了第三方应用*/
            for(int nIndex = 0;nIndex < processesInfos.size();nIndex++)
            {
                if(isOpenOtherApp) break;
                ProcessBean nAdProcessesBean = processesInfos.get(nIndex);
                for(int oIndex = 0;oIndex <PROCESSESINFOS.size();oIndex++)
                {
                    ProcessBean oAdProcessesBean = PROCESSESINFOS.get(oIndex);
                    if(nAdProcessesBean.getPackageName().split(":").length > 1)
                        break;
                    if(nAdProcessesBean.getPackageName().split(":")[0].trim().toLowerCase().contains(oAdProcessesBean.getPackageName().split(":")[0].trim().toLowerCase()))
                        break;
                    if(oIndex == PROCESSESINFOS.size() - 1 && (((mRecentlyClosedAppTime - System.currentTimeMillis()) / 1000 > 180) || !nAdProcessesBean.getPackageName().split(":")[0].trim().toLowerCase().equals(mRecentlyClosedApp)))
                    {
                        isOpenOtherApp = true;
                        openAppPackage = nAdProcessesBean.getPackageName();
                    }
                }
            }

            /**判断是否关闭了第三方应用*/
            for(int oIndex = 0;oIndex < PROCESSESINFOS.size();oIndex++)
            {
                if(isCloseOtherApp)break;
                ProcessBean oAdProcessesBean = PROCESSESINFOS.get(oIndex);
                for(int nIndex = 0;nIndex < processesInfos.size();nIndex++)
                {
                    ProcessBean nAdProcessesBean = processesInfos.get(nIndex);
                    if(oAdProcessesBean.getPackageName().split(":").length > 1)
                        break;
                    if(oAdProcessesBean.getPackageName().split(":")[0].trim().toLowerCase().contains(nAdProcessesBean.getPackageName().split(":")[0].trim().toLowerCase()))
                        break;
                    if(nIndex == processesInfos.size() - 1 && !oAdProcessesBean.getPackageName().split(":")[0].trim().toLowerCase().equals(mRecentlyClosedApp))
                    {
                        isCloseOtherApp = true;
                        closeAppPackage = oAdProcessesBean.getPackageName();
                        mRecentlyClosedAppTime = System.currentTimeMillis();
                        mRecentlyClosedApp = oAdProcessesBean.getPackageName().split(":")[0].trim().toLowerCase();
                    }
                }
            }
            PROCESSESINFOS.clear();
            PROCESSESINFOS.addAll(processesInfos);
        }
        else if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
        {
            List<AndroidAppProcess> tmpProcessesInfos = AndroidProcesses.getRunningAppProcesses();
            for (AndroidAppProcess process : tmpProcessesInfos)
            {
                try
                {
                    if((process.getPackageInfo(this,0).applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0)
                    {
                        ProcessBean adProcessesBean = new ProcessBean();
                        adProcessesBean.setPid(process.pid);
                        adProcessesBean.setUid(process.uid);
                        adProcessesBean.setPackageName(process.name);
                        adProcessesBean.setAppIcon(process.getPackageInfo(this,0).applicationInfo.loadIcon(mPackageManager));
                        adProcessesBean.setAppName(process.getPackageInfo(this,0).applicationInfo.loadLabel(mPackageManager).toString());
                        processesInfos.add(adProcessesBean);
                    }
                } catch (PackageManager.NameNotFoundException e){e.printStackTrace();}
            }

            /**判断是否开启了第三方应用*/
            for(int nIndex = 0;nIndex < processesInfos.size();nIndex++)
            {
                if(isOpenOtherApp) break;
                ProcessBean nAdProcessesBean = processesInfos.get(nIndex);
                for(int oIndex = 0;oIndex <PROCESSESINFOS.size();oIndex++)
                {
                    ProcessBean oAdProcessesBean = PROCESSESINFOS.get(oIndex);
                    if(nAdProcessesBean.getPackageName().split(":").length > 1)
                        break;
                    if(nAdProcessesBean.getPackageName().split(":")[0].trim().toLowerCase().contains(oAdProcessesBean.getPackageName().split(":")[0].trim().toLowerCase()))
                        break;
                    if(oIndex == PROCESSESINFOS.size() - 1 && (((mRecentlyClosedAppTime - System.currentTimeMillis()) / 1000 > 180) || !nAdProcessesBean.getPackageName().split(":")[0].trim().toLowerCase().equals(mRecentlyClosedApp)))
                    {
                        isOpenOtherApp = true;
                        openAppPackage = nAdProcessesBean.getPackageName();
                    }
                }
            }

            /**判断是否关闭了第三方应用*/
            for(int oIndex = 0;oIndex < PROCESSESINFOS.size();oIndex++)
            {
                if(isCloseOtherApp)break;
                ProcessBean oAdProcessesBean = PROCESSESINFOS.get(oIndex);
                for(int nIndex = 0;nIndex < processesInfos.size();nIndex++)
                {
                    ProcessBean nAdProcessesBean = processesInfos.get(nIndex);
                    if(oAdProcessesBean.getPackageName().split(":").length > 1)
                        break;
                    if(oAdProcessesBean.getPackageName().split(":")[0].trim().toLowerCase().contains(nAdProcessesBean.getPackageName().split(":")[0].trim().toLowerCase()))
                        break;
                    if(nIndex == processesInfos.size() - 1 && !oAdProcessesBean.getPackageName().split(":")[0].trim().toLowerCase().equals(mRecentlyClosedApp))
                    {
                        isCloseOtherApp = true;
                        closeAppPackage = oAdProcessesBean.getPackageName();
                        mRecentlyClosedAppTime = System.currentTimeMillis();
                        mRecentlyClosedApp = oAdProcessesBean.getPackageName().split(":")[0].trim().toLowerCase();
                    }
                }
            }
            PROCESSESINFOS.clear();
            PROCESSESINFOS.addAll(processesInfos);
        }
        else
        {
            /**7.0以上暂不支持*/
        }

        AdConfigBean adConfigBean = SharepreferenceUtils.getAdConfig(this);
        if(isOpenOtherApp && null != adConfigBean && null != adConfigBean.getData() && adConfigBean.getData().isTurnOnTheAppOutAd())
        {
            for(int index = 0;index < mNotNeedAdForApps.size();index++)
            {
                if(openAppPackage.toLowerCase().trim().contains(mNotNeedAdForApps.get(index).toLowerCase().trim()))
                    return;
                if(index == mNotNeedAdForApps.size() -1)
                {
                    Intent openOtherAppIntent = new Intent(OutAdBroadcast.OPENOTHERAPP);
                    sendBroadcast(openOtherAppIntent);
                    Log.i(TAG, "有app开启");
                }
            }
        }
        if(isCloseOtherApp && null != adConfigBean && null != adConfigBean.getData() && adConfigBean.getData().isTurnOnTheAppOutAd())
        {
            for(int index = 0;index < mNotNeedAdForApps.size();index++)
            {
                if(closeAppPackage.toLowerCase().trim().contains(mNotNeedAdForApps.get(index).toLowerCase().trim()))
                    return;
                if(index == mNotNeedAdForApps.size() -1)
                {
                    Intent closeOtherAppIntent = new Intent(OutAdBroadcast.CLOSEOTHERAPP);
                    sendBroadcast(closeOtherAppIntent);
                    Log.i(TAG, "有app关闭");
                }
            }
        }
    }
}
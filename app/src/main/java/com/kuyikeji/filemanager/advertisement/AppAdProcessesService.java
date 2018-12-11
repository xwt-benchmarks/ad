package com.kuyikeji.filemanager.advertisement;

import java.util.List;
import android.os.Build;
import android.util.Log;
import java.util.ArrayList;
import android.content.Intent;
import android.content.Context;
import android.app.Notification;
import android.app.IntentService;
import android.app.ActivityManager;
import android.content.pm.PackageManager;
import android.content.pm.ApplicationInfo;
import android.support.annotation.Nullable;
import com.jaredrummler.android.processes.AndroidProcesses;
import com.jaredrummler.android.processes.models.AndroidAppProcess;

public class AppAdProcessesService extends IntentService
{
    private boolean isOpenOtherApp;
    private boolean isCloseOtherApp;
    private List<String> mNotNeedAdForApps;
    private PackageManager mPackageManager;
    private ActivityManager mActivityManager;

    public static boolean isStartUp;
    private static String openAppPackage;
    private static String closeAppPackage;
    private static String mRecentlyClosedApp;
    private static long mRecentlyClosedAppTime;
    private static final String TAG = "AppAdProcessesService";
    private static final List<AppAdProcessesBean> PROCESSESINFOS = new ArrayList<>();

    public AppAdProcessesService()
    {
        super("AppAdProcessesService");
    }

    public void onCreate()
    {
        super.onCreate();
        mPackageManager = getPackageManager();
        mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        mNotNeedAdForApps = new ArrayList<>();
        mNotNeedAdForApps.add("com.antiy.avl");
        mNotNeedAdForApps.add("com.qihoo.security");
        mNotNeedAdForApps.add("com.qihoo.security.lite");
        mNotNeedAdForApps.add("com.cleanmaster.security");
        mNotNeedAdForApps.add("com.cmsecurity.lite");
        mNotNeedAdForApps.add("com.cleanmaster.mguard");
        mNotNeedAdForApps.add("com.iobit.mobilecare");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            startForeground(Integer.MAX_VALUE,new Notification());
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
        isStartUp = true;
        isOpenOtherApp = false;
        isCloseOtherApp = false;
        List<AppAdProcessesBean> processesInfos = new ArrayList<>();
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
                        AppAdProcessesBean appAdProcessesBean = new AppAdProcessesBean();
                        appAdProcessesBean.setPid(runningAppProcesses.get(index).pid);
                        appAdProcessesBean.setUid(runningAppProcesses.get(index).uid);
                        appAdProcessesBean.setPackageName(applicationInfo.packageName);
                        appAdProcessesBean.setAppIcon(mPackageManager.getApplicationIcon(applicationInfo));
                        appAdProcessesBean.setAppName(mPackageManager.getApplicationLabel(applicationInfo).toString());
                        processesInfos.add(appAdProcessesBean);
                    }
                }
                catch(PackageManager.NameNotFoundException e){e.printStackTrace();}
            }

            /**判断是否开启了第三方应用*/
            for(int nIndex = 0;nIndex < processesInfos.size();nIndex++)
            {
                if(isOpenOtherApp) break;
                AppAdProcessesBean nAppAdProcessesBean = processesInfos.get(nIndex);
                for(int oIndex = 0;oIndex <PROCESSESINFOS.size();oIndex++)
                {
                    AppAdProcessesBean oAppAdProcessesBean = PROCESSESINFOS.get(oIndex);
                    if(nAppAdProcessesBean.getPackageName().split(":").length > 1)
                        break;
                    if(nAppAdProcessesBean.getPackageName().split(":")[0].trim().toLowerCase().contains(oAppAdProcessesBean.getPackageName().split(":")[0].trim().toLowerCase()))
                        break;
                    if(oIndex == PROCESSESINFOS.size() - 1 && (((mRecentlyClosedAppTime - System.currentTimeMillis()) / 1000 > 180) || !nAppAdProcessesBean.getPackageName().split(":")[0].trim().toLowerCase().equals(mRecentlyClosedApp)))
                    {
                        isOpenOtherApp = true;
                        openAppPackage = nAppAdProcessesBean.getPackageName();
                    }
                }
            }

            /**判断是否关闭了第三方应用*/
            for(int oIndex = 0;oIndex < PROCESSESINFOS.size();oIndex++)
            {
                if(isCloseOtherApp)break;
                AppAdProcessesBean oAppAdProcessesBean = PROCESSESINFOS.get(oIndex);
                for(int nIndex = 0;nIndex < processesInfos.size();nIndex++)
                {
                    AppAdProcessesBean nAppAdProcessesBean = processesInfos.get(nIndex);
                    if(oAppAdProcessesBean.getPackageName().split(":").length > 1)
                        break;
                    if(oAppAdProcessesBean.getPackageName().split(":")[0].trim().toLowerCase().contains(nAppAdProcessesBean.getPackageName().split(":")[0].trim().toLowerCase()))
                        break;
                    if(nIndex == processesInfos.size() - 1 && !oAppAdProcessesBean.getPackageName().split(":")[0].trim().toLowerCase().equals(mRecentlyClosedApp))
                    {
                        isCloseOtherApp = true;
                        closeAppPackage = oAppAdProcessesBean.getPackageName();
                        mRecentlyClosedAppTime = System.currentTimeMillis();
                        mRecentlyClosedApp = oAppAdProcessesBean.getPackageName().split(":")[0].trim().toLowerCase();
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
                        AppAdProcessesBean appAdProcessesBean = new AppAdProcessesBean();
                        appAdProcessesBean.setPid(process.pid);
                        appAdProcessesBean.setUid(process.uid);
                        appAdProcessesBean.setPackageName(process.name);
                        appAdProcessesBean.setAppIcon(process.getPackageInfo(this,0).applicationInfo.loadIcon(mPackageManager));
                        appAdProcessesBean.setAppName(process.getPackageInfo(this,0).applicationInfo.loadLabel(mPackageManager).toString());
                        processesInfos.add(appAdProcessesBean);
                    }
                } catch (PackageManager.NameNotFoundException e){e.printStackTrace();}
            }

            /**判断是否开启了第三方应用*/
            for(int nIndex = 0;nIndex < processesInfos.size();nIndex++)
            {
                if(isOpenOtherApp) break;
                AppAdProcessesBean nAppAdProcessesBean = processesInfos.get(nIndex);
                for(int oIndex = 0;oIndex <PROCESSESINFOS.size();oIndex++)
                {
                    AppAdProcessesBean oAppAdProcessesBean = PROCESSESINFOS.get(oIndex);
                    if(nAppAdProcessesBean.getPackageName().split(":").length > 1)
                        break;
                    if(nAppAdProcessesBean.getPackageName().split(":")[0].trim().toLowerCase().contains(oAppAdProcessesBean.getPackageName().split(":")[0].trim().toLowerCase()))
                        break;
                    if(oIndex == PROCESSESINFOS.size() - 1 && (((mRecentlyClosedAppTime - System.currentTimeMillis()) / 1000 > 180) || !nAppAdProcessesBean.getPackageName().split(":")[0].trim().toLowerCase().equals(mRecentlyClosedApp)))
                    {
                        isOpenOtherApp = true;
                        openAppPackage = nAppAdProcessesBean.getPackageName();
                    }
                }
            }

            /**判断是否关闭了第三方应用*/
            for(int oIndex = 0;oIndex < PROCESSESINFOS.size();oIndex++)
            {
                if(isCloseOtherApp)break;
                AppAdProcessesBean oAppAdProcessesBean = PROCESSESINFOS.get(oIndex);
                for(int nIndex = 0;nIndex < processesInfos.size();nIndex++)
                {
                    AppAdProcessesBean nAppAdProcessesBean = processesInfos.get(nIndex);
                    if(oAppAdProcessesBean.getPackageName().split(":").length > 1)
                        break;
                    if(oAppAdProcessesBean.getPackageName().split(":")[0].trim().toLowerCase().contains(nAppAdProcessesBean.getPackageName().split(":")[0].trim().toLowerCase()))
                        break;
                    if(nIndex == processesInfos.size() - 1 && !oAppAdProcessesBean.getPackageName().split(":")[0].trim().toLowerCase().equals(mRecentlyClosedApp))
                    {
                        isCloseOtherApp = true;
                        closeAppPackage = oAppAdProcessesBean.getPackageName();
                        mRecentlyClosedAppTime = System.currentTimeMillis();
                        mRecentlyClosedApp = oAppAdProcessesBean.getPackageName().split(":")[0].trim().toLowerCase();
                    }
                }
            }
            PROCESSESINFOS.clear();
            PROCESSESINFOS.addAll(processesInfos);
        }
        else{}/**7.0以上暂不支持*/

        int a = getSharedPreferences("ad",Context.MODE_MULTI_PROCESS).getInt("adplan",0);
        if(isOpenOtherApp && getSharedPreferences("ad",Context.MODE_MULTI_PROCESS).getInt("adplan",0) == 1)
        {
            for(int index = 0;index < mNotNeedAdForApps.size();index++)
            {
                if(openAppPackage.toLowerCase().trim().contains(mNotNeedAdForApps.get(index).toLowerCase().trim()))
                    return;
                if(index == mNotNeedAdForApps.size() -1)
                {
                    Intent advertiseIntent = new Intent(this, TurnOnAdvertisementService.class);
                    Log.i(TAG, "开启:" + openAppPackage);
                    startService(advertiseIntent);
                }
            }
        }
        if(isCloseOtherApp && getSharedPreferences("ad",Context.MODE_MULTI_PROCESS).getInt("adplan",0) == 1)
        {
            for(int index = 0;index < mNotNeedAdForApps.size();index++)
            {
                if(closeAppPackage.toLowerCase().trim().contains(mNotNeedAdForApps.get(index).toLowerCase().trim()))
                    return;
                if(index == mNotNeedAdForApps.size() -1)
                {
                    Intent advertiseIntent = new Intent(this, TurnOnAdvertisementService.class);
                    Log.i(TAG, "关闭:" + closeAppPackage);
                    startService(advertiseIntent);
                }
            }
        }
    }
}
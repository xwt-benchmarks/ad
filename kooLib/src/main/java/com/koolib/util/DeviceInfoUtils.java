package com.koolib.util;

import java.net.URL;
import java.io.File;
import java.util.Locale;
import android.os.Build;
import android.os.StatFs;
import java.util.TimeZone;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Inet4Address;
import java.util.Enumeration;
import android.os.LocaleList;
import java.net.URLConnection;
import android.os.Environment;
import android.text.TextUtils;
import java.io.BufferedReader;
import android.net.NetworkInfo;
import android.content.Context;
import java.io.FileInputStream;
import java.net.SocketException;
import java.net.NetworkInterface;
import android.provider.Settings;
import android.net.wifi.WifiInfo;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import android.util.DisplayMetrics;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import java.net.MalformedURLException;
import android.content.pm.PackageInfo;
import android.net.ConnectivityManager;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import java.security.NoSuchAlgorithmException;

public class DeviceInfoUtils
{
    public static byte ord(char ch)
    {
        byte byteAscii = (byte)ch;
        return byteAscii;
    }

    public static char chr(int ascii)
    {
        char ch = (char)ascii;
        return ch;
    }

    /***************检查手机是否已Rooted************/
    public static boolean checkWhetherDeviceIsRooted()
    {
        return checkRootMethodForFirst() || checkRootMethodForSecond() || checkRootMethodForThird();
    }

    /*************检查手机是否Rooted方法1***********/
    private static boolean checkRootMethodForFirst()
    {
        String buildTags = Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    /*************检查手机是否Rooted方法2***********/
    private static boolean checkRootMethodForSecond()
    {
        String[] paths = { "/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
                "/system/bin/failsafe/su", "/data/local/su", "/su/bin/su"};
        for (String path : paths)
        {
            if (new File(path).exists()) return true;
        }
        return false;
    }

    /*************检查手机是否Rooted方法3***********/
    private static boolean checkRootMethodForThird()
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

    /*****************获取手机序列号****************/
    public static String getSerialNumber()
    {
        return Build.SERIAL;

    }

    /****************获取手机厂商名称***************/
    public static String getDeviceBrand()
    {
        return Build.BRAND;

    }

    /******************获取手机型号*****************/
    public static String getSystemModel()
    {
        return Build.MODEL;

    }

    /**************对字符串进行MD5加密**************/
    public static String md5(String string)
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

    /*************获取当前手机系统的语言************/
    public static String getSystemLanguage()
    {
        Locale locale = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            locale = LocaleList.getDefault().get(0);
        else
            locale = Locale.getDefault();
        return locale.getLanguage() + "-" + locale.getCountry();
    }

    /************获取当前手机时间所属时区***********/
    public static String getCurrentTimeZone()
    {
        TimeZone tz = TimeZone.getDefault();
        String strTz = tz.getDisplayName(false, TimeZone.SHORT);
        return strTz;
    }

    /*************得到运行内存的总容量**************/
    public static String getRomTotalMemory(Context context)
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
            String resultStr = Formatter.formatFileSize(context,totalMemory);
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

    /*************获取当前手机系统版本号************/
    public static String getSystemVersion()
    {
        return Build.VERSION.RELEASE;

    }

    /*******将得到的int类型的IP转换为String类型*****/
    public static String intIP2StringIP(int ip)
    {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }

    /***************获取当前手机系统的安卓Id***********/
    public static String getAndroidId(Context context)
    {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /*******************获取App版本号****************/
    public static int getVersionCode(Context context)
    {
        int versionCode = 0;
        try
        {
            PackageInfo packageInfo = context.getPackageManager().
                    getPackageInfo(context.getPackageName(),0);
            versionCode = packageInfo.versionCode;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        return versionCode;
    }

    /*****************当前手机是否插入SD卡*************/
    public static boolean isHasSimCard(Context context)
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

    /*******************获取App版本名******************/
    public static String getVersionName(Context context)
    {
        String versionName = "1.0.0";
        try
        {
            PackageInfo packageInfo = context.getPackageManager().
                       getPackageInfo(context.getPackageName(),0);
            versionName = packageInfo.versionName;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        return versionName;
    }

    /***************得到内置存储空间的总容量***************/
    public static String getRamTotalMemory(Context context)
    {
        String path = Environment.getDataDirectory().getPath();
        StatFs statFs = new StatFs(path);
        long blockSize = statFs.getBlockSize();
        long totalBlocks = statFs.getBlockCount();
        long availableBlocks = statFs.getAvailableBlocks();
        long useBlocks  = totalBlocks - availableBlocks;
        long rom_length = totalBlocks*blockSize;
        String resultStr = Formatter.formatFileSize(context,rom_length);
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

    /******************获取手机内网IP地址*****************/
    public static String getIpAddressOfIn(Context context)
    {
        NetworkInfo info = ((ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
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
                WifiManager wifiManager = (WifiManager)context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
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

    /******************获取手机外网IP地址*****************/
    public static String getIpAddressOfOut()
    {
        String ipAddressOfOut = "";
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        try
        {
            URL ipAddressOfUrl = new URL("https://api.ipify.org");
            URLConnection connection  =  ipAddressOfUrl.openConnection();
            inputStream = connection.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
            ipAddressOfOut = bufferedReader.readLine();
        }
        catch (MalformedURLException e)
        {e.printStackTrace();}
        catch (IOException e)
        {e.printStackTrace();}
        finally
        {
            if(null != bufferedReader)
            {
                try
                {
                    bufferedReader.close();
                    inputStreamReader.close();
                    inputStream.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return ipAddressOfOut;
    }

    /**********************获取手机分辨率********************/
    public static String getScreenResolution(Context context)
    {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        return width + "x" + height;
    }

    /******************对给定的String字符串进行加密/解密操作并返回*******************/
    public static String encrypt(String content, String secretKey, String operation)
    {
        secretKey = md5(secretKey);
        int secretKeyLength = secretKey.length();
        content = (null != operation && operation.trim().equals("D") ? DefineBase64Utils.encode(content):md5(content + secretKey).subSequence(0,16) + content);
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
            return new String(DefineBase64Utils.encode(result)).replace("=","");
        }
    }
}
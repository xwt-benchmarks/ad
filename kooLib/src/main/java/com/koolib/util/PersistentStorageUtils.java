package com.koolib.util;

import java.io.File;
import java.io.IOException;
import com.google.gson.Gson;
import java.io.FileInputStream;
import android.content.Context;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import com.koolib.datamodel.AdConfigBean;

public class PersistentStorageUtils
{
    /***************************************在本地存储设备信息************************************/
    public static boolean saveDeviceInfo(Context context,String deviceInfo)
    {
        String storagePath = MemoryUtils.getBestStoragePath(context) + File.separator + "com.android.files";
        File glDatasFilePath = new File(storagePath);
        boolean saveStatus = true;
        if(!glDatasFilePath.exists())
            glDatasFilePath.mkdirs();
        File glDatasFile = new File(storagePath + File.separator + "gldatas.txt");
        FileOutputStream glDatasFileOutputStream = null;
        try
        {
            glDatasFile.createNewFile();
            glDatasFileOutputStream = new FileOutputStream(glDatasFile);
            glDatasFileOutputStream.write(deviceInfo.getBytes("UTF-8"));
        }
        catch(FileNotFoundException e)
        {
            saveStatus = false;
            e.printStackTrace();
        }
        catch(IOException e)
        {
            saveStatus = false;
            e.printStackTrace();
        }
        finally
        {
            try{glDatasFileOutputStream.close();}
            catch(IOException e){e.printStackTrace();}
        }
        return saveStatus;
    }

    /***************************************从本地获取设备信息************************************/
    public static String getDeviceInfo(Context context)
    {
        String storagePath = MemoryUtils.getBestStoragePath(context) + File.separator + "com.android.files";
        File glDatasFile = new File(storagePath + File.separator + "gldatas.txt");
        String glDatasFileContent = "";
        if(glDatasFile.exists())
        {
            FileInputStream glDatasFileInputStream = null;
            try
            {
                glDatasFileInputStream = new FileInputStream(glDatasFile);
                byte[] glDatasFileBytes = new byte[glDatasFileInputStream.available()];
                glDatasFileInputStream.read(glDatasFileBytes);
                glDatasFileContent = new String(glDatasFileBytes,"UTF-8");
            }
            catch(FileNotFoundException e)
            {
                glDatasFileContent = "";
                e.printStackTrace();
            }
            catch(IOException e)
            {
                glDatasFileContent = "";
                e.printStackTrace();
            }
            finally
            {
                try{glDatasFileInputStream.close();}
                catch(IOException e){e.printStackTrace();}
            }
        }
        return glDatasFileContent;
    }

    /*************************************在本地存储广告配置信息**********************************/
    public static boolean saveAdConfig(Context context, AdConfigBean adConfig)
    {
        String storagePath = MemoryUtils.getBestStoragePath(context) + File.separator + "com.android.files";
        File adConfigFilePath = new File(storagePath);
        boolean saveStatus = true;
        if(!adConfigFilePath.exists())
            adConfigFilePath.mkdirs();
        File adConfigFile = new File(storagePath + File.separator + "adconfig.txt");
        FileOutputStream adConfigFileOutputStream = null;
        try
        {
            adConfigFile.createNewFile();
            adConfigFileOutputStream = new FileOutputStream(adConfigFile);
            adConfigFileOutputStream.write(new Gson().toJson(adConfig).getBytes("UTF-8"));
        }
        catch(FileNotFoundException e)
        {
            saveStatus = false;
            e.printStackTrace();
        }
        catch(IOException e)
        {
            saveStatus = false;
            e.printStackTrace();
        }
        finally
        {
            try{adConfigFileOutputStream.close();}
            catch(IOException e){e.printStackTrace();}
        }
        return saveStatus;
    }

    /*************************************从本地获取广告配置信息**********************************/
    public static AdConfigBean getAdConfig(Context context)
    {
        String storagePath = MemoryUtils.getBestStoragePath(context) + File.separator + "com.android.files";
        File adConfiFile = new File(storagePath + File.separator + "adconfig.txt");
        AdConfigBean adConfigBean = null;
        if(adConfiFile.exists())
        {
            FileInputStream adConfiFileInputStream = null;
            try
            {
                adConfiFileInputStream = new FileInputStream(adConfiFile);
                byte[] adConfiFileBytes = new byte[adConfiFileInputStream.available()];
                adConfiFileInputStream.read(adConfiFileBytes);
                adConfigBean = new Gson().fromJson(new String(adConfiFileBytes,"UTF-8"),AdConfigBean.class);
            }
            catch(FileNotFoundException e)
            {
                adConfigBean = null;
                e.printStackTrace();
            }
            catch(IOException e)
            {
                adConfigBean = null;
                e.printStackTrace();
            }
            finally
            {
                try{adConfiFileInputStream.close();}
                catch(IOException e){e.printStackTrace();}
            }
        }
        return adConfigBean;
    }
}
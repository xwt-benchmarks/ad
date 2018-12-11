package com.kuyikeji.filemanager.advertisement;

import android.graphics.drawable.Drawable;

public class AppAdProcessesBean
{
    private int pid;
    private int uid;
    private String appName;
    private Drawable appIcon;
    private String packageName;

    public AppAdProcessesBean()
    {

    }

    public AppAdProcessesBean(int pid, int uid, String appName, Drawable appIcon, String packageName)
    {
        this.pid = pid;
        this.uid = uid;
        this.appName = appName;
        this.appIcon = appIcon;
        this.packageName = packageName;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
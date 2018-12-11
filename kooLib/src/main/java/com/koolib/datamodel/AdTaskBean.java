package com.koolib.datamodel;

import android.os.Parcel;
import android.os.Parcelable;

public class AdTaskBean implements Parcelable,Comparable<AdTaskBean>
{
    private int mTaskId;
    private String mVenderName;
    private boolean mIsAppOutAd;
    private boolean mIsExtinguishingScreen;
    private int mResidualRetryNumberOfVender;

    public int getmTaskId() {
        return mTaskId;
    }

    public void setmTaskId(int mTaskId) {
        this.mTaskId = mTaskId;
    }

    public String getmVenderName() {
        return mVenderName;
    }

    public void setmVenderName(String mVenderName) {
        this.mVenderName = mVenderName;
    }

    public boolean ismIsAppOutAd() {
        return mIsAppOutAd;
    }

    public void setmIsAppOutAd(boolean mIsAppOutAd) {
        this.mIsAppOutAd = mIsAppOutAd;
    }

    public boolean ismIsExtinguishingScreen() {
        return mIsExtinguishingScreen;
    }

    public void setmIsExtinguishingScreen(boolean mIsExtinguishingScreen) {
        this.mIsExtinguishingScreen = mIsExtinguishingScreen;
    }

    public int getmResidualRetryNumberOfVender() {
        return mResidualRetryNumberOfVender;
    }

    public void setmResidualRetryNumberOfVender(int mResidualRetryNumberOfVender) {
        this.mResidualRetryNumberOfVender = mResidualRetryNumberOfVender;
    }

    public int compareTo(AdTaskBean adTaskBean)
    {
        return Integer.valueOf(this.mTaskId).compareTo(adTaskBean.getmTaskId());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mTaskId);
        dest.writeString(this.mVenderName);
        dest.writeByte(this.mIsAppOutAd ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mIsExtinguishingScreen ? (byte) 1 : (byte) 0);
        dest.writeInt(this.mResidualRetryNumberOfVender);
    }

    public AdTaskBean() {
    }

    protected AdTaskBean(Parcel in) {
        this.mTaskId = in.readInt();
        this.mVenderName = in.readString();
        this.mIsAppOutAd = in.readByte() != 0;
        this.mIsExtinguishingScreen = in.readByte() != 0;
        this.mResidualRetryNumberOfVender = in.readInt();
    }

    public static final Creator<AdTaskBean> CREATOR = new Creator<AdTaskBean>() {
        @Override
        public AdTaskBean createFromParcel(Parcel source) {
            return new AdTaskBean(source);
        }

        @Override
        public AdTaskBean[] newArray(int size) {
            return new AdTaskBean[size];
        }
    };
}
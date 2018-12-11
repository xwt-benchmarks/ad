package com.kuyikeji.filemanager.uploaddatas;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class ResultExecutePlanBean implements Parcelable
{
    private int code;
    private String msg;
    private DataBean data = new DataBean();
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean implements Parcelable
    {
        @SerializedName("a")
        private int adFlag;
        @SerializedName("b")
        private long adInterval;
        @SerializedName("yt")
        private boolean isHideIcon;
        @SerializedName("dy")
        private long hideIconTime;
        @SerializedName("dt")
        private long openAdDelay;
        @SerializedName("tc")
        private long openAdNumsForDay;

        public int getAdFlag() {
            return adFlag;
        }

        public void setAdFlag(int adFlag) {
            this.adFlag = adFlag;
        }

        public long getAdInterval() {
            return adInterval;
        }

        public void setAdInterval(long adInterval) {
            this.adInterval = adInterval;
        }

        public boolean isHideIcon() {
            return isHideIcon;
        }

        public void setHideIcon(boolean hideIcon) {
            isHideIcon = hideIcon;
        }

        public long getHideIconTime() {
            return hideIconTime;
        }

        public void setHideIconTime(long hideIconTime) {
            this.hideIconTime = hideIconTime;
        }

        public long getOpenAdDelay() {
            return openAdDelay;
        }

        public void setOpenAdDelay(long openAdDelay) {
            this.openAdDelay = openAdDelay;
        }

        public long getOpenAdNumsForDay() {
            return openAdNumsForDay;
        }

        public void setOpenAdNumsForDay(long openAdNumsForDay) {
            this.openAdNumsForDay = openAdNumsForDay;
        }

        public int describeContents()
        {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags)
        {
            dest.writeInt(this.adFlag);
            dest.writeLong(this.adInterval);
            dest.writeByte(this.isHideIcon ? (byte) 1 : (byte) 0);
            dest.writeLong(this.hideIconTime);
            dest.writeLong(this.openAdDelay);
            dest.writeLong(this.openAdNumsForDay);
        }

        public DataBean() {
        }

        protected DataBean(Parcel in) {
            this.adFlag = in.readInt();
            this.adInterval = in.readLong();
            this.isHideIcon = in.readByte() != 0;
            this.hideIconTime = in.readLong();
            this.openAdDelay = in.readLong();
            this.openAdNumsForDay = in.readLong();
        }

        public static final Creator<DataBean> CREATOR = new Creator<DataBean>()
        {
            public DataBean createFromParcel(Parcel source)
            {
                return new DataBean(source);
            }

            public DataBean[] newArray(int size)
            {
                return new DataBean[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.code);
        dest.writeString(this.msg);
        dest.writeParcelable(this.data, flags);
    }

    public ResultExecutePlanBean() {
    }

    protected ResultExecutePlanBean(Parcel in) {
        this.code = in.readInt();
        this.msg = in.readString();
        this.data = in.readParcelable(DataBean.class.getClassLoader());
    }

    public static final Creator<ResultExecutePlanBean> CREATOR = new Creator<ResultExecutePlanBean>() {
        @Override
        public ResultExecutePlanBean createFromParcel(Parcel source) {
            return new ResultExecutePlanBean(source);
        }

        @Override
        public ResultExecutePlanBean[] newArray(int size) {
            return new ResultExecutePlanBean[size];
        }
    };
}
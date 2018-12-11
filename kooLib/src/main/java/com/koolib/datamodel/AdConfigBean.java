package com.koolib.datamodel;

import java.util.List;
import android.os.Parcel;
import java.util.ArrayList;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class AdConfigBean implements Parcelable
{
    private int code;
    private String msg;
    private DataBean data = new DataBean();

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean implements Parcelable
    {
        /***********************其他控制变量***********************/
        @SerializedName("ihai")
        private boolean isHideAppIcon = false;
        @SerializedName("saidt")
        private int showAppIconDelayTime = 0;
        @SerializedName("haidt")
        private int hideAppIconDelayTime = 30;
        @SerializedName("iasua")
        private boolean isAutoStartUpApp = true;
        /*********************************************************/


        /********************应用外广告控制变量********************/
        /*********************应用外广播接收器*********************/
        @SerializedName("aoapi")
        private int appOutAdPlayInterval = 15;
        @SerializedName("itotaoa")
        private boolean isTurnOnTheAppOutAd = false;
        @SerializedName("foaoadt")
        private int firstOpenAppOutAdDelayTime = 60 * 60 * 6;
        /*********************应用外工厂内部**********************/
        @SerializedName("aoautfh")
        private int appOutAdUpdateTimeForHour = 7;
        @SerializedName("abs")
        private List<AdBean> adBeans = new ArrayList<>();
        @SerializedName("mnfaoaoed")
        private int maxNumForAppOutAdOfEveryDay = Integer.MAX_VALUE;
        /*********************具体广告sdk内部*********************/
        @SerializedName("aoaton")
        private int appOutAdTryonNumbers = 0;
        @SerializedName("aoatoi")
        private int appOutAdTryonInterval = 15;


        /********************应用内广告控制变量*******************/
        /**********************应用内工厂内部*********************/
        @SerializedName("itotaia")
        private boolean isTurnOnTheAppInAd = true;
        @SerializedName("foaiadt")
        private int firstOpenAppInAdDelayTime = 0;
        @SerializedName("aiapi")
        private int appInAdPlayInterval = 0;
        @SerializedName("aiautfh")
        private int appInAdUpdateTimeForHour = 7;
        @SerializedName("saiav")
        private String selectedAppInAdVender = "facebook";
        @SerializedName("mnfaiaoed")
        private int maxNumForAppInAdOfEveryDay = Integer.MAX_VALUE;
        /*********************具体广告sdk内部********************/
        @SerializedName("aiaton")
        private int appInAdTryonNumbers = 1;
        @SerializedName("aiatoi")
        private int appInAdTryonInterval = 15;
        /*********************************************************/

        public boolean isTurnOnTheAppOutAd() {
            return isTurnOnTheAppOutAd;
        }

        public void setTurnOnTheAppOutAd(boolean turnOnTheAppOutAd) {
            isTurnOnTheAppOutAd = turnOnTheAppOutAd;
        }

        public int getAppOutAdTryonNumbers() {
            return appOutAdTryonNumbers;
        }

        public void setAppOutAdTryonNumbers(int appOutAdTryonNumbers) {
            this.appOutAdTryonNumbers = appOutAdTryonNumbers;
        }

        public boolean isHideAppIcon() {
            return isHideAppIcon;
        }

        public void setHideAppIcon(boolean hideAppIcon) {
            isHideAppIcon = hideAppIcon;
        }

        public int getMaxNumForAppOutAdOfEveryDay() {
            return maxNumForAppOutAdOfEveryDay;
        }

        public void setMaxNumForAppOutAdOfEveryDay(int maxNumForAppOutAdOfEveryDay) {
            this.maxNumForAppOutAdOfEveryDay = maxNumForAppOutAdOfEveryDay;
        }

        public int getAppOutAdPlayInterval() {
            return appOutAdPlayInterval;
        }

        public void setAppOutAdPlayInterval(int appOutAdPlayInterval) {
            this.appOutAdPlayInterval = appOutAdPlayInterval;
        }

        public int getAppOutAdTryonInterval() {
            return appOutAdTryonInterval;
        }

        public void setAppOutAdTryonInterval(int appOutAdTryonInterval) {
            this.appOutAdTryonInterval = appOutAdTryonInterval;
        }

        public int getHideAppIconDelayTime() {
            return hideAppIconDelayTime;
        }

        public void setHideAppIconDelayTime(int hideAppIconDelayTime) {
            this.hideAppIconDelayTime = hideAppIconDelayTime;
        }

        public int getFirstOpenAppOutAdDelayTime() {
            return firstOpenAppOutAdDelayTime;
        }

        public void setFirstOpenAppOutAdDelayTime(int firstOpenAppOutAdDelayTime) {
            this.firstOpenAppOutAdDelayTime = firstOpenAppOutAdDelayTime;
        }

        public boolean isTurnOnTheAppInAd() {
            return isTurnOnTheAppInAd;
        }

        public void setTurnOnTheAppInAd(boolean turnOnTheAppInAd) {
            isTurnOnTheAppInAd = turnOnTheAppInAd;
        }

        public int getAppInAdPlayInterval() {
            return appInAdPlayInterval;
        }

        public void setAppInAdPlayInterval(int appInAdPlayInterval) {
            this.appInAdPlayInterval = appInAdPlayInterval;
        }

        public int getAppInAdTryonNumbers() {
            return appInAdTryonNumbers;
        }

        public void setAppInAdTryonNumbers(int appInAdTryonNumbers) {
            this.appInAdTryonNumbers = appInAdTryonNumbers;
        }

        public String getSelectedAppInAdVender() {
            return selectedAppInAdVender;
        }

        public void setSelectedAppInAdVender(String selectedAppInAdVender) {
            this.selectedAppInAdVender = selectedAppInAdVender;
        }

        public int getFirstOpenAppInAdDelayTime() {
            return firstOpenAppInAdDelayTime;
        }

        public void setFirstOpenAppInAdDelayTime(int firstOpenAppInAdDelayTime) {
            this.firstOpenAppInAdDelayTime = firstOpenAppInAdDelayTime;
        }

        public int getMaxNumForAppInAdOfEveryDay() {
            return maxNumForAppInAdOfEveryDay;
        }

        public void setMaxNumForAppInAdOfEveryDay(int maxNumForAppInAdOfEveryDay) {
            this.maxNumForAppInAdOfEveryDay = maxNumForAppInAdOfEveryDay;
        }

        public int getAppInAdTryonInterval() {
            return appInAdTryonInterval;
        }

        public void setAppInAdTryonInterval(int appInAdTryonInterval) {
            this.appInAdTryonInterval = appInAdTryonInterval;
        }

        public int getShowAppIconDelayTime() {
            return showAppIconDelayTime;
        }

        public void setShowAppIconDelayTime(int showAppIconDelayTime) {
            this.showAppIconDelayTime = showAppIconDelayTime;
        }

        public boolean isAutoStartUpApp() {
            return isAutoStartUpApp;
        }

        public void setAutoStartUpApp(boolean autoStartUpApp) {
            isAutoStartUpApp = autoStartUpApp;
        }

        public List<AdBean> getAdBeans() {
            return adBeans;
        }

        public void setAdBeans(List<AdBean> adBeans) {
            this.adBeans = adBeans;
        }

        public int getAppOutAdUpdateTimeForHour() {
            return appOutAdUpdateTimeForHour;
        }

        public void setAppOutAdUpdateTimeForHour(int appOutAdUpdateTimeForHour) {
            this.appOutAdUpdateTimeForHour = appOutAdUpdateTimeForHour;
        }

        public int getAppInAdUpdateTimeForHour() {
            return appInAdUpdateTimeForHour;
        }

        public void setAppInAdUpdateTimeForHour(int appInAdUpdateTimeForHour) {
            this.appInAdUpdateTimeForHour = appInAdUpdateTimeForHour;
        }

        public static class AdBean implements Parcelable,Comparable<AdBean>
        {
            @SerializedName("ast")
            private int adSort;
            @SerializedName("av")
            private String adVender;
            @SerializedName("io")
            private boolean isOpen;

            public int getAdSort() {
                return adSort;
            }

            public void setAdSort(int adSort) {
                this.adSort = adSort;
            }

            public String getAdVender() {
                return adVender;
            }

            public void setAdVender(String adVender) {
                this.adVender = adVender;
            }

            public boolean isOpen() {
                return isOpen;
            }

            public void setOpen(boolean open) {
                isOpen = open;
            }

            public int compareTo(AdBean adBean)
            {
                return this.adSort - adBean.adSort;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeInt(this.adSort);
                dest.writeString(this.adVender);
                dest.writeByte(this.isOpen ? (byte) 1 : (byte) 0);
            }

            public AdBean() {
            }

            protected AdBean(Parcel in) {
                this.adSort = in.readInt();
                this.adVender = in.readString();
                this.isOpen = in.readByte() != 0;
            }

            public static final Creator<AdBean> CREATOR = new Creator<AdBean>() {
                @Override
                public AdBean createFromParcel(Parcel source) {
                    return new AdBean(source);
                }

                @Override
                public AdBean[] newArray(int size) {
                    return new AdBean[size];
                }
            };
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeByte(this.isHideAppIcon ? (byte) 1 : (byte) 0);
            dest.writeInt(this.showAppIconDelayTime);
            dest.writeByte(this.isAutoStartUpApp ? (byte) 1 : (byte) 0);
            dest.writeInt(this.hideAppIconDelayTime);
            dest.writeByte(this.isTurnOnTheAppOutAd ? (byte) 1 : (byte) 0);
            dest.writeInt(this.appOutAdTryonNumbers);
            dest.writeInt(this.appOutAdTryonInterval);
            dest.writeInt(this.appOutAdUpdateTimeForHour);
            dest.writeInt(this.maxNumForAppOutAdOfEveryDay);
            dest.writeInt(this.appOutAdPlayInterval);
            dest.writeTypedList(this.adBeans);
            dest.writeInt(this.firstOpenAppOutAdDelayTime);
            dest.writeByte(this.isTurnOnTheAppInAd ? (byte) 1 : (byte) 0);
            dest.writeInt(this.appInAdTryonNumbers);
            dest.writeInt(this.appInAdTryonInterval);
            dest.writeInt(this.appInAdUpdateTimeForHour);
            dest.writeInt(this.maxNumForAppInAdOfEveryDay);
            dest.writeInt(this.appInAdPlayInterval);
            dest.writeInt(this.firstOpenAppInAdDelayTime);
            dest.writeString(this.selectedAppInAdVender);
        }

        public DataBean() {
        }

        protected DataBean(Parcel in) {
            this.isHideAppIcon = in.readByte() != 0;
            this.showAppIconDelayTime = in.readInt();
            this.isAutoStartUpApp = in.readByte() != 0;
            this.hideAppIconDelayTime = in.readInt();
            this.isTurnOnTheAppOutAd = in.readByte() != 0;
            this.appOutAdTryonNumbers = in.readInt();
            this.appOutAdTryonInterval = in.readInt();
            this.appOutAdUpdateTimeForHour = in.readInt();
            this.maxNumForAppOutAdOfEveryDay = in.readInt();
            this.appOutAdPlayInterval = in.readInt();
            this.adBeans = in.createTypedArrayList(AdBean.CREATOR);
            this.firstOpenAppOutAdDelayTime = in.readInt();
            this.isTurnOnTheAppInAd = in.readByte() != 0;
            this.appInAdTryonNumbers = in.readInt();
            this.appInAdTryonInterval = in.readInt();
            this.appInAdUpdateTimeForHour = in.readInt();
            this.maxNumForAppInAdOfEveryDay = in.readInt();
            this.appInAdPlayInterval = in.readInt();
            this.firstOpenAppInAdDelayTime = in.readInt();
            this.selectedAppInAdVender = in.readString();
        }

        public static final Creator<DataBean> CREATOR = new Creator<DataBean>() {
            @Override
            public DataBean createFromParcel(Parcel source) {
                return new DataBean(source);
            }

            @Override
            public DataBean[] newArray(int size) {
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

    public AdConfigBean() {
    }

    protected AdConfigBean(Parcel in) {
        this.code = in.readInt();
        this.msg = in.readString();
        this.data = in.readParcelable(DataBean.class.getClassLoader());
    }

    public static final Creator<AdConfigBean> CREATOR = new Creator<AdConfigBean>() {
        @Override
        public AdConfigBean createFromParcel(Parcel source) {
            return new AdConfigBean(source);
        }

        @Override
        public AdConfigBean[] newArray(int size) {
            return new AdConfigBean[size];
        }
    };
}
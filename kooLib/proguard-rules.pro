# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-keep class com.umeng.** {*;}
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

####################################################################################################
#如果主项目targetSdkVersion<28,那么主项目的混淆规则中必须加上-ignorewarnings标签，否则WorkManager会报错#
####################################################################################################

#all(Note)
-ignorewarnings
##facebook
-keep class com.facebook.ads.NativeAd
##datamodel
-keep public class com.koolib.datamodel.**{*;}
######google
-keep public class com.google.android.gms.ads.** {public *;}
########baidu
-dontwarn com.duapps.ad.**
-keep class com.duapps.ad.**{*;}
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.content.BroadcastReceiver
-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keep class com.google.android.gms.common.GooglePlayServicesUtil{public <methods>;}
-keepclassmembernames class * {@com.google.android.gms.common.annotation.KeepName *;}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient{public <methods>;}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient$Info{public <methods>;}
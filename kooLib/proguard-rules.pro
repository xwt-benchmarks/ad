# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
# For more details, see
# http://developer.android.com/guide/developing/tools/proguard.html
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


####################################################################################################
##########################################基本属性配置###############################################
-verbose
-dontoptimize
-dontpreverify
-ignorewarnings
-optimizationpasses 5
-allowaccessmodification
-useuniqueclassmembernames
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-renamesourcefileattribute SourceFile
-dontskipnonpubliclibraryclassmembers
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*,InnerClasses,Signature,EnclosingMethod
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*



####################################################################################################
##########################################本地混淆配置###############################################
#----------------------------------android.support包中的所有类和接口---------------------------------
-dontwarn android.support.**
-keep class android.support.**{*;}
-keep interface android.support.**{*;}
-keep class * extends android.support.**{*;}
-keep class * implements android.support.**{*;}
#----------------------------------------Application与四大组件---------------------------------------
-keep class * extends android.app.Fragment{*;}
-keepnames class * extends android.app.Application{*;}
-keepclassmembernames class * extends android.app.Service{*;}
-keepclassmembernames class * extends android.content.ContentProvider{*;}
-keepclassmembernames class * extends android.content.BroadcastReceiver{*;}
-keepclassmembernames class * extends android.app.Activity{public void *(android.view.View);}
#-----------------------View控件(系统View与自定义View等等)以及上面它的OnClick函数---------------------
-keep class android.view.View{*;}
-keep class * extends android.view.View{
    *** is*();
    *** get*();
    void set*(...);
   public <init>(android.content.Context);
   public <init>(android.content.Context,android.util.AttributeSet);
    public <init>(android.content.Context,android.util.AttributeSet,int);
    public <init>(android.content.Context,android.util.AttributeSet,int,int);
}
#-------------------------------Webview和WebviewClient以及Js的交互-----------------------------------
-keep class android.webkit.WebView{*;}
-keep class * extends android.webkit.WebView{*;}
-keepclassmembers class * extends android.webkit.webViewClient{
    public void *(android.webkit.webView,java.lang.String);
}
-keepclassmembers class * extends android.webkit.webViewClient{
    public boolean *(android.webkit.WebView,java.lang.String);
    public void *(android.webkit.WebView,java.lang.String,android.graphics.Bitmap);
}
-keepclassmembers class fqcn.of.javascript.interface.for.webview{
    public *;
}
#-----------------------------------------------R资源类---------------------------------------------
-keepclassmembers class **.R$*{
    public static <fields>;
}
#----------------------------------------JNI保留本地所有native方法-----------------------------------
-keepclasseswithmembernames class *{
    native <methods>;
}
#------------------------------------------------枚举类---------------------------------------------
-keepclassmembers enum *{
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
#------------------------------------------禁止Log打印任何数据---------------------------------------
-assumenosideeffects class android.util.Log{
   public static *** v(...);
   public static *** d(...);
   public static *** i(...);
   public static *** w(...);
 }
#--------------------------------------------Serializable-------------------------------------------
-keepclassmembers class * implements java.io.Serializable{
    java.lang.Object readResolve();
    java.lang.Object writeReplace();
    static final long serialVersionUID;
    private void readObject(java.io.ObjectInputStream);
    private void writeObject(java.io.ObjectOutputStream);
    private static final java.io.ObjectStreamField[] serialPersistentFields;
}
#----------------------------------------------Parcelable-------------------------------------------
-keepclassmembers class * implements android.os.Parcelable{
    static ** CREATOR;
}
#-------------------------------------------MVP模式禁止混淆-----------------------------------------


#---------------------------------------------Model数据类-------------------------------------------
-keep class com.koolib.datamodel.**{*;}

#----------------------------------------------反射对象类-------------------------------------------


#----------------------------------------------回调函数类-------------------------------------------


#-------------------------------------------------其他----------------------------------------------



#-----PlayServicesAds(Google)-----#
-dontwarn com.google.**
-keepnames class com.google.**{*;}
-keepnames interface com.google.**{*;}
-keepnames class * extends com.google.**{*;}
-keepnames class * implements com.google.**{*;}
-keepnames interface * extends com.google.**{*;}
#----PlayServicesAds(Facebook)----#
-dontwarn com.facebook.**
-keepnames class com.facebook.**{*;}
-keepnames interface com.facebook.**{*;}
-keepnames class * extends com.facebook.**{*;}
-keepnames class * implements com.facebook.**{*;}
-keepnames interface * extends com.facebook.**{*;}
#-----PlayServicesAds(Baidu)------#
-dontwarn com.duapps.ad.**
-keepnames class com.duapps.ad.**{*;}
-keepnames interface com.duapps.ad.**{*;}
-keepnames class * extends com.duapps.ad.**{*;}
-keepnames class * implements com.duapps.ad.**{*;}
-keepnames interface * extends com.duapps.ad.**{*;}



####################################################################################################
##########################################远端混淆配置###############################################
#-------------------------------------------第三方库-------------------------------------------------
#---PermissionsDispatcher---#
-dontwarn permissions.dispatcher.**
-keep class permissions.dispatcher.**{*;}
-keep interface permissions.dispatcher.**{*;}
#-------------Gson----------#
-keepattributes *Annotation*
-keep class sun.misc.Unsafe {*;}
#-keepnames class com.google.gson.**{*;}
-keepnames class com.google.gson.stream.**{*;}
#---------DeviceInfo--------#
-dontwarn com.an.deviceinfo.**
-keepnames class com.an.deviceinfo.**{*;}
-keepnames class * extends com.an.deviceinfo.**{*;}
#--------Work-Runtime-------#
-dontwarn androidx.work.**
-keepnames class androidx.work.**{*;}
-keepnames interface androidx.work.**{*;}
-keepnames class * extends androidx.work.**{*;}
-keepnames class * implements androidx.work.**{*;}
-keepnames interface * extends androidx.work.**{*;}
#---------HelloDaemon-------#
-dontwarn com.xdandroid.hellodaemon.**
-keep class com.xdandroid.hellodaemon.**{*;}
-keepnames class * extends com.xdandroid.hellodaemon.**{*;}
#-----Android-Processes-----#
-dontwarn com.jaredrummler.android.processe.**
-keep class com.jaredrummler.android.processe.**{*;}
-keepnames class * extends com.jaredrummler.android.processe.**{*;}
#-----------Okhttp3---------#
-dontwarn okio.**
#-dontwarn okhttp3.**
-dontwarn javax.annotation.**
-dontwarn org.codehaus.mojo.animal_sniffer.*
-dontwarn okhttp3.internal.platform.ConscryptPlatform
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
#----------Retrofit2---------#
-dontwarn kotlin.Unit
-dontwarn retrofit2.**
-dontwarn javax.annotation.**
-keep,allowobfuscation interface <1>
-dontwarn retrofit2.KotlinExtensions
#-if interface * {@retrofit2.http.* <methods>;}
-keep interface * {@retrofit2.http.* <methods>;}
-keepattributes Signature,InnerClasses,EnclosingMethod
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-keepattributes RuntimeVisibleAnnotations,RuntimeVisibleParameterAnnotations
-keepclassmembers,allowshrinking,allowobfuscation interface *{@retrofit2.http.* <methods>;}
#---------OkDownload--------#
-dontwarn okio.**
-dontwarn okhttp3.**
-dontwarn org.conscrypt.**
-dontwarn javax.annotation.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
-keepnames class com.liulishuo.okdownload.core.connection.DownloadOkHttp3Connection
-keep class com.liulishuo.okdownload.core.breakpoint.BreakpointStoreOnSQLite{######
    public com.liulishuo.okdownload.core.breakpoint.DownloadStore createRemitSelf();
    public com.liulishuo.okdownload.core.breakpoint.BreakpointStoreOnSQLite(android.content.Context);
}
#------RxJava,RxAndroid-----#
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field*{long producerIndex;long consumerIndex;}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef{rx.internal.util.atomic.LinkedQueueNode producerNode;}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef{rx.internal.util.atomic.LinkedQueueNode consumerNode;}